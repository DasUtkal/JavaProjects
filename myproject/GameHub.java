import javax.swing.*;
import java.awt.*;

public class GameHub extends JFrame {
    public GameHub() {
        setTitle("Ultimate Java Game Hub");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(40, 44, 52));

        setLayout(new GridLayout(5, 1, 10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("GAME HUB Arcade", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        add(title);

        add(createButton("Tic-Tac-Toe", new Color(255, 99, 71), () -> new TicTacToe().setVisible(true)));
        add(createButton("Snake", new Color(60, 179, 113), () -> new SnakeGame().setVisible(true)));
        add(createButton("Ping Pong", new Color(30, 144, 255), () -> new PongGame().setVisible(true)));
        add(createButton("Flappy Bird", new Color(255, 215, 0), () -> new FlappyBird().setVisible(true)));
    }

    private JButton createButton(String text, Color color, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameHub().setVisible(true));
    }
}
