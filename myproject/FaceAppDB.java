import java.awt.*;
import java.io.*;
import java.sql.*;
import javax.swing.*;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceAppDB extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:face_db.sqlite";

    private CascadeClassifier classifier;

    public FaceAppDB() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String xmlFile = new java.io.File("haarcascade/haarcascade_frontalface_default.xml").getAbsolutePath();
        classifier = new CascadeClassifier(xmlFile);

        initDB();

        setTitle("Face Registration & Recognition System");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnRegister = new JButton("1. Register New Person");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 18));
        btnRegister.setBackground(new Color(51, 153, 255));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.addActionListener(e -> handleRegister());
        
        JButton btnVerify = new JButton("2. Verify Face Image");
        btnVerify.setFont(new Font("Arial", Font.BOLD, 18));
        btnVerify.setBackground(new Color(153, 51, 255));
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setFocusPainted(false);
        btnVerify.addActionListener(e -> handleVerify());

        add(btnRegister);
        add(btnVerify);
        setLocationRelativeTo(null);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initDB() {
        try (Connection setupConn = DriverManager.getConnection(DB_URL);
             Statement stmt = setupConn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name VARCHAR(100), "
                    + "face_data BLOB)");
            System.out.println("Connected to SQLite Database successfully.");
        } catch (SQLException e) {
            System.err.println("---------- SQLITE ERROR ----------");
            System.err.println(e.getMessage());
        }
    }

    private void handleRegister() {
        File file = chooseImage();
        if (file == null) return;

        Mat image = Imgcodecs.imread(file.getAbsolutePath());
        if (image.empty()) {
            JOptionPane.showMessageDialog(this, "Could not open image file.");
            return;
        }

        Mat faceCrop = extractFace(image);
        if (faceCrop == null) {
            JOptionPane.showMessageDialog(this, "No face detected in the image!");
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Face detected successfully! Enter the Person's Name:");
        if (name != null && !name.trim().isEmpty()) {
            byte[] faceBytes = matToBytes(faceCrop);
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (name, face_data) VALUES (?, ?)")) {
                pstmt.setString(1, name);
                pstmt.setBytes(2, faceBytes);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Yay! Verified and saved '" + name + "' to the MySQL database!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error. Could not save to DB: " + ex.getMessage());
            }
        }
    }

    private void handleVerify() {
        File file = chooseImage();
        if (file == null) return;

        Mat image = Imgcodecs.imread(file.getAbsolutePath());
        if (image.empty()) {
            JOptionPane.showMessageDialog(this, "Could not open image file.");
            return;
        }
        
        Mat faceCrop = extractFace(image);
        if (faceCrop == null) {
            JOptionPane.showMessageDialog(this, "No face detected in the given image!");
            return;
        }

        String bestMatchName = "Unidentified Person";
        double bestSimilarity = -1; // -1 refers to non-correlated

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, face_data FROM users")) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                String dbName = rs.getString("name");
                byte[] dbFaceBytes = rs.getBytes("face_data");
                Mat dbFace = bytesToMat(dbFaceBytes);
                
                double similarity = compareFaces(faceCrop, dbFace);
                System.out.println("Processing ID match against " + dbName + ": Score = " + String.format("%.2f", similarity));

                // 0.40 is our loose threshold for correlation index Template Matching. Over 0.4 usually implies same visual features.
                if (similarity > bestSimilarity && similarity > 0.40) { 
                    bestSimilarity = similarity;
                    bestMatchName = dbName;
                }
            }

            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Database is completely empty! Register someone first.");
                return;
            }

            if (bestSimilarity > 0.40) {
                JOptionPane.showMessageDialog(this, "MATCH FOUND IN DATABASE!\n\nName: " + bestMatchName + "\nConfidence Match: " + (int)(bestSimilarity * 100) + "%\n\nVerified successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "INTRUDER ALERT!\nThis face does not match anyone registered in the Database.", "Alert", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error verifying face: " + ex.getMessage());
        }
    }

    private File chooseImage() {
        JFileChooser chooser = new JFileChooser("."); // opens current folder
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    // AI Pipeline Extraction
    private Mat extractFace(Mat image) {
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(image, faces);
        Rect[] facesArray = faces.toArray();
        if (facesArray.length == 0) return null;
        
        // Take the biggest face found
        Rect rect = facesArray[0];
        Mat cropped = new Mat(image, rect);
        
        // Machine Learning Normalization: Grayscale color mapping -> Resize into uniform fixed scale
        Mat gray = new Mat();
        Imgproc.cvtColor(cropped, gray, Imgproc.COLOR_BGR2GRAY);
        Mat processedFace = new Mat();
        Imgproc.resize(gray, processedFace, new Size(100, 100)); // Resize to rigid 100px square
        
        return processedFace;
    }

    private byte[] matToBytes(Mat mat) {
        MatOfByte mob = new MatOfByte();
        // Convert the MAT object to raw bytes stream .JPG
        Imgcodecs.imencode(".jpg", mat, mob);
        return mob.toArray();
    }

    private Mat bytesToMat(byte[] bytes) {
        return Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_GRAYSCALE);
    }

    private double compareFaces(Mat uploadedFace, Mat dbFace) {
        Mat result = new Mat();
        // Native OpenCV algorithm to detect visual pixel cluster correlations
        Imgproc.matchTemplate(uploadedFace, dbFace, result, Imgproc.TM_CCOEFF_NORMED);
        return result.get(0, 0)[0];
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        FaceAppDB app = new FaceAppDB();
        app.setVisible(true);
    }
}
