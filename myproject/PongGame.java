import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PongGame extends JFrame {
    public PongGame() {
        setTitle("Ping Pong");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        add(new PongPanel());
        pack();
        setLocationRelativeTo(null);
    }

    private class PongPanel extends JPanel implements ActionListener {
        private final int WIDTH = 800, HEIGHT = 600;
        private final int PADDLE_W = 15, PADDLE_H = 100;
        private final int BALL_SIZE = 20;
        
        private int playerY = HEIGHT/2 - PADDLE_H/2;
        private int aiY = HEIGHT/2 - PADDLE_H/2;
        private int ballX = WIDTH/2 - BALL_SIZE/2, ballY = HEIGHT/2 - BALL_SIZE/2;
        private int ballVelocityX = -5, ballVelocityY = 4;
        private int playerScore = 0, aiScore = 0;
        
        private boolean upPressed = false, downPressed = false;
        private Timer timer;

        public PongPanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(Color.BLACK);
            setFocusable(true);
            
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) upPressed = true;
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = true;
                }
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) upPressed = false;
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = false;
                }
            });
            timer = new Timer(1000/60, this);
            timer.start();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            // Draw paddles
            g.fillRect(20, playerY, PADDLE_W, PADDLE_H); // Player
            g.fillRect(WIDTH - 35, aiY, PADDLE_W, PADDLE_H); // AI
            // Draw ball
            g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
            // Draw line
            for(int i=0; i<HEIGHT; i+=30) g.drawLine(WIDTH/2, i, WIDTH/2, i+15);
            // Draw scores
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString(String.valueOf(playerScore), WIDTH/2 - 70, 50);
            g.drawString(String.valueOf(aiScore), WIDTH/2 + 40, 50);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Move Player
            if (upPressed && playerY > 0) playerY -= 6;
            if (downPressed && playerY < HEIGHT - PADDLE_H) playerY += 6;
            
            // Move AI (Simple tracking logic)
            if (ballY < aiY + PADDLE_H/2 && aiY > 0) aiY -= 4;
            if (ballY > aiY + PADDLE_H/2 && aiY < HEIGHT - PADDLE_H) aiY += 4;
            
            // Move Ball
            ballX += ballVelocityX;
            ballY += ballVelocityY;
            
            // Ball bounces off top & bottom
            if (ballY <= 0 || ballY >= HEIGHT - BALL_SIZE) ballVelocityY *= -1;
            
            // Ball bounces off Player paddle
            if (ballX <= 20 + PADDLE_W && ballY + BALL_SIZE >= playerY && ballY <= playerY + PADDLE_H) {
                ballVelocityX = Math.abs(ballVelocityX); // move right
            }
            // Ball bounces off AI paddle
            if (ballX + BALL_SIZE >= WIDTH - 35 && ballY + BALL_SIZE >= aiY && ballY <= aiY + PADDLE_H) {
                ballVelocityX = -Math.abs(ballVelocityX); // move left
            }
            
            // Score points
            if (ballX < 0) {
                aiScore++;
                resetBall();
            } else if (ballX > WIDTH) {
                playerScore++;
                resetBall();
            }
            
            repaint();
        }
        
        private void resetBall() {
            ballX = WIDTH/2 - BALL_SIZE/2;
            ballY = HEIGHT/2 - BALL_SIZE/2;
            ballVelocityX = (Math.random() > 0.5 ? 5 : -5);
            ballVelocityY = (Math.random() > 0.5 ? 4 : -4);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PongGame().setVisible(true));
    }
}
