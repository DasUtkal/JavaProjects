import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JFrame {
    public FlappyBird() {
        setTitle("Flappy Bird");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        add(new FlappyPanel());
        pack();
        setLocationRelativeTo(null);
    }

    private class FlappyPanel extends JPanel implements ActionListener {
        private final int WIDTH = 800, HEIGHT = 600;
        private int birdY = HEIGHT/2, birdVelocity = 0;
        private final int GRAVITY = 1, JUMP = -11;
        private ArrayList<Rectangle> pipes;
        private Random random;
        private Timer timer;
        private int score = 0;
        private boolean gameOver = false, started = false;

        public FlappyPanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(135, 206, 235)); // Sky blue
            setFocusable(true);
            
            pipes = new ArrayList<>();
            random = new Random();
            addPipe(true);
            addPipe(true);
            addPipe(true);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) { jump(); }
            });
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_SPACE) jump(); }
            });

            timer = new Timer(20, this);
            timer.start();
        }

        private void addPipe(boolean start) {
            int space = 150;
            int width = 80;
            int minHeight = 50;
            int height = minHeight + random.nextInt(300);
            
            int x = start ? WIDTH + pipes.size() * 300 : WIDTH;
            // Top pipe
            pipes.add(new Rectangle(x, 0, width, height));
            // Bottom pipe
            pipes.add(new Rectangle(x, height + space, width, HEIGHT - height - space));
        }

        private void jump() {
            if (gameOver) {
                birdY = HEIGHT/2;
                birdVelocity = 0;
                score = 0;
                gameOver = false;
                pipes.clear();
                addPipe(true);
                addPipe(true);
                addPipe(true);
            } else {
                started = true;
                birdVelocity = JUMP;
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw Ground
            g.setColor(new Color(222, 216, 149));
            g.fillRect(0, HEIGHT - 50, WIDTH, 50);

            // Draw pipes
            g.setColor(new Color(0, 150, 0));
            for(Rectangle pipe : pipes) {
                g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
            }
            
            // Draw bird
            g.setColor(Color.YELLOW);
            g.fillOval(WIDTH/4 - 10, birdY, 30, 30);
            g.setColor(Color.BLACK);
            g.drawOval(WIDTH/4 - 10, birdY, 30, 30);

            // Draw text
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(Color.WHITE);
            if (!started) {
                g.drawString("Press Space to Start", 150, HEIGHT/2);
            } else if (gameOver) {
                g.drawString("Game Over: " + score, 200, HEIGHT/2);
            } else {
                g.drawString(String.valueOf(score), WIDTH/2 - 25, 50);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (started && !gameOver) {
                birdVelocity += GRAVITY;
                birdY += birdVelocity;
                
                ArrayList<Rectangle> copy = new ArrayList<>(pipes);
                for (int i = 0; i < copy.size(); i++) {
                    Rectangle pipe = copy.get(i);
                    pipe.x -= 5;
                }
                
                // Remove off-screen pipes and add new ones and update score
                for (int i = 0; i < copy.size(); i+=2) {
                    Rectangle pipe = copy.get(i);
                    if (pipe.x + pipe.width < 0) {
                        pipes.remove(pipe);
                        pipes.remove(copy.get(i+1)); // bottom pipe too
                        addPipe(false);
                        score++;
                    }
                }
                
                // Collisions
                Rectangle birdRect = new Rectangle(WIDTH/4 - 10, birdY, 30, 30);
                for(Rectangle pipe : pipes) {
                    if (pipe.intersects(birdRect)) {
                        gameOver = true;
                    }
                }
                if (birdY > HEIGHT - 50 || birdY < 0) gameOver = true;
            }
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlappyBird().setVisible(true));
    }
}
