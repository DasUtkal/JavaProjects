import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator extends JFrame implements ActionListener {
    private JTextField display;
    private String currentOperator = "";
    private double firstOperand = 0;
    private boolean startNewNumber = true;

    public Calculator() {
        setTitle("Java GUI Calculator");
        setSize(320, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display Screen
        display = new JTextField("0");
        display.setFont(new Font("Arial", Font.BOLD, 36));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setPreferredSize(new Dimension(300, 80));
        add(display, BorderLayout.NORTH);

        // Buttons Grid
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "C", "0", "=", "+"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 24));
            button.setFocusPainted(false);
            
            if ("/*-+".contains(text)) {
                button.setBackground(new Color(255, 153, 51)); // Orange operators
                button.setForeground(Color.WHITE);
            } else if ("C".equals(text)) {
                button.setBackground(new Color(255, 102, 102)); // Red clear
                button.setForeground(Color.WHITE);
            } else if ("=".equals(text)) {
                button.setBackground(new Color(102, 204, 0)); // Green equals
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(new Color(224, 224, 224)); // Light grey numbers
            }

            button.addActionListener(this);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("0123456789".contains(command)) {
            if (startNewNumber) {
                display.setText(command);
                startNewNumber = false;
            } else {
                if(display.getText().equals("0")) {
                    display.setText(command);
                } else {
                    display.setText(display.getText() + command);
                }
            }
        } else if ("C".equals(command)) {
            display.setText("0");
            firstOperand = 0;
            currentOperator = "";
            startNewNumber = true;
        } else if ("=".equals(command)) {
            if (!currentOperator.isEmpty()) {
                calculate();
                currentOperator = "";
                startNewNumber = true;
            }
        } else { // Operator (+, -, *, /)
            if (!startNewNumber && !currentOperator.isEmpty()) {
                calculate();
            }
            try {
                firstOperand = Double.parseDouble(display.getText());
            } catch(NumberFormatException ex) {
                firstOperand = 0;
            }
            currentOperator = command;
            startNewNumber = true;
        }
    }

    private void calculate() {
        try {
            double secondOperand = Double.parseDouble(display.getText());
            double result = 0;

            switch (currentOperator) {
                case "+": result = firstOperand + secondOperand; break;
                case "-": result = firstOperand - secondOperand; break;
                case "*": result = firstOperand * secondOperand; break;
                case "/": 
                    if (secondOperand != 0) {
                        result = firstOperand / secondOperand; 
                    } else {
                        display.setText("Error");
                        startNewNumber = true;
                        return;
                    }
                    break;
            }
            
            // Format result to remove decimal if it's a whole number
            if (result == (long) result) {
                display.setText(String.format("%d", (long) result));
            } else {
                display.setText(String.valueOf(result));
            }
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }

    public static void main(String[] args) {
        // Use modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new Calculator());
    }
}
