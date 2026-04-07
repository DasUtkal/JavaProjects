import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToDoList extends JFrame {
    private DefaultListModel<String> listModel;
    private JList<String> taskList;
    private JTextField taskInput;

    public ToDoList() {
        setTitle("Java GUI To-Do List");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top Panel: Input Field and Add Button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(10, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        taskInput = new JTextField();
        taskInput.setFont(new Font("Arial", Font.PLAIN, 18));
        
        JButton addButton = new JButton("Add Task");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(60, 179, 113)); // Green
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        topPanel.add(taskInput, BorderLayout.CENTER);
        topPanel.add(addButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Scrollable Task List
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 18));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Alternate row colors using a simple cell renderer
        taskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (!isSelected) {
                    c.setBackground(index % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 15, 5, 15),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));

        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Remove Button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        JButton removeButton = new JButton("Remove Selected Task");
        removeButton.setFont(new Font("Arial", Font.BOLD, 14));
        removeButton.setBackground(new Color(220, 20, 60)); // Red
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);

        bottomPanel.add(removeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Event Listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        taskInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex != -1) {
                    listModel.remove(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(ToDoList.this, "Please select a task to remove first.", "No Task Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    private void addTask() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            listModel.addElement("• " + task);
            taskInput.setText("");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new ToDoList());
    }
}
