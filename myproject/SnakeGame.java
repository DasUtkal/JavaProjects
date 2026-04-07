import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JFrame {
    public SnakeGame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        pack();
        setLocationRelativeTo(null);
    }

    private class GamePanel extends JPanel implements ActionListener {
        private final int TILE_SIZE = 25;
        private final int WIDTH = 600;
        private final int HEIGHT = 600;
        
        private ArrayList<Point> snake;
        private Point apple;
        private Random random;
        private char direction = 'R'; // U, D, L, R
        private boolean running = false;
        private Timer timer;

        public GamePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(Color.BLACK);
            setFocusable(true);
            
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch(e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            if(direction != 'R') direction = 'L';
                            break;
                        case KeyEvent.VK_RIGHT:
                            if(direction != 'L') direction = 'R';
                            break;
                        case KeyEvent.VK_UP:
                            if(direction != 'D') direction = 'U';
                            break;
                        case KeyEvent.VK_DOWN:
                            if(direction != 'U') direction = 'D';
                            break;
                    }
                }
            });
            startGame();
        }

        private void startGame() {
            snake = new ArrayList<>();
            snake.add(new Point(WIDTH/2, HEIGHT/2));
            snake.add(new Point(WIDTH/2 - TILE_SIZE, HEIGHT/2));
            snake.add(new Point(WIDTH/2 - 2*TILE_SIZE, HEIGHT/2));
            
            random = new Random();
            spawnApple();
            
            running = true;
            timer = new Timer(100, this);
            timer.start();
        }

        private void spawnApple() {
            int x = random.nextInt((int)(WIDTH/TILE_SIZE)) * TILE_SIZE;
            int y = random.nextInt((int)(HEIGHT/TILE_SIZE)) * TILE_SIZE;
            apple = new Point(x, y);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (running) {
                g.setColor(Color.RED);
                g.fillOval(apple.x, apple.y, TILE_SIZE, TILE_SIZE);
                
                for (int i = 0; i < snake.size(); i++) {
                    Point p = snake.get(i);
                    g.setColor(i == 0 ? Color.GREEN : new Color(45, 180, 0));
                    g.fillRect(p.x, p.y, TILE_SIZE, TILE_SIZE);
                }
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString("Score: " + (snake.size() - 3), 10, 20);
            } else {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (running) {
                move();
                checkApple();
                checkCollisions();
            }
            repaint();
        }

        private void move() {
            Point head = snake.get(0);
            Point newHead = new Point(head.x, head.y);
            
            switch(direction) {
                case 'U': newHead.y -= TILE_SIZE; break;
                case 'D': newHead.y += TILE_SIZE; break;
                case 'L': newHead.x -= TILE_SIZE; break;
                case 'R': newHead.x += TILE_SIZE; break;
            }
            
            snake.add(0, newHead);
            snake.remove(snake.size() - 1); // remove tail
        }

        private void checkApple() {
            if (snake.get(0).equals(apple)) {
                // Grow
                snake.add(new Point(snake.get(snake.size()-1)));
                spawnApple();
            }
        }

        private void checkCollisions() {
            Point head = snake.get(0);
            // check walls
            if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
                running = false;
            }
            // check self body
            for (int i = 1; i < snake.size(); i++) {
                if (head.equals(snake.get(i))) {
                    running = false;
                    break;
                }
            }
            if (!running) timer.stop();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeGame().setVisible(true));
    }
}
