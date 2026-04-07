
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        String xmlFile = new java.io.File("haarcascade/haarcascade_frontalface_default.xml").getAbsolutePath();
        CascadeClassifier classifier = new CascadeClassifier(xmlFile);

        if (classifier.empty()) {
            System.out.println("Error loading XML file");
            return;
        }

        Mat image = Imgcodecs.imread(new java.io.File("test.jpg").getAbsolutePath());

        if (image.empty()) {
            System.out.println("Error loading image");
            return;
        }

        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(image, faces);

        System.out.println("Faces detected: " + faces.toArray().length);

        // Draw rectangles around detected faces
        for (Rect rect : faces.toArray()) {
            org.opencv.imgproc.Imgproc.rectangle(
                    image,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0), // Green color
                    3 // Thickness
            );
        }

        // Save the updated image
        String outputPath = new java.io.File("output.jpg").getAbsolutePath();
        Imgcodecs.imwrite(outputPath, image);
        System.out.println("Drawing saved to: " + outputPath);
    }
}