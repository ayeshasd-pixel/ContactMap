import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame {
    private final JTextField nameField = new JTextField(18);
    private final JTextField phoneField = new JTextField(18);
    private final JTextField searchField = new JTextField(15);

    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"Name", "Phone"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final HashMap<String, String> contacts = new HashMap<>();

    public Main() {
        setTitle("ContactMap");
        setSize(750, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        main.setBackground(new Color(245, 247, 250));

        JLabel heading = new JLabel("ContactMap");
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(new Color(35, 55, 90));

        JLabel subtitle = new JLabel("Your contacts, organized.");
        subtitle.setForeground(Color.GRAY);

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);
        header.add(heading);
        header.add(subtitle);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setOpaque(false);
        form.add(new JLabel("Name"));
        form.add(nameField);
        form.add(new JLabel("Phone number"));
        form.add(phoneField);

        JButton addButton = new JButton("Add Contact");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear Fields");
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setOpaque(false);
        actions.add(addButton);
        actions.add(updateButton);
        actions.add(deleteButton);
        actions.add(clearButton);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);

        JPanel inputs = new JPanel(new BorderLayout(0, 8));
        inputs.setOpaque(false);
        inputs.add(form, BorderLayout.CENTER);
        inputs.add(actions, BorderLayout.SOUTH);
        top.add(inputs, BorderLayout.SOUTH);

        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(680, 220));

        main.add(top, BorderLayout.NORTH);
        main.add(scrollPane, BorderLayout.CENTER);
        main.add(searchPanel, BorderLayout.SOUTH);

        setContentPane(main);

        addButton.addActionListener(e -> addContact());
        updateButton.addActionListener(e -> updateContact());
        deleteButton.addActionListener(e -> deleteContact());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchContacts());
        showAllButton.addActionListener(e -> refreshTable(contacts));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                nameField.setText(model.getValueAt(row, 0).toString());
                phoneField.setText(model.getValueAt(row, 1).toString());
            }
        });

        setVisible(true);
    }

    private void addContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            message("Enter both name and phone number.");
            return;
        }

        String key = name.toLowerCase();

        if (contacts.containsKey(key)) {
            message("A contact with this name already exists.");
            return;
        }

        contacts.put(key, phone);
        refreshTable(contacts);
        clearFields();
    }

    private void updateContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            message("Select a contact and enter its new details.");
            return;
        }

        String key = name.toLowerCase();

        if (!contacts.containsKey(key)) {
            message("Contact not found.");
            return;
        }

        contacts.put(key, phone);
        refreshTable(contacts);
        clearFields();
    }

    private void deleteContact() {
        int row = table.getSelectedRow();

        if (row < 0) {
            message("Select a contact from the table first.");
            return;
        }

        String name = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this, "Delete " + name + "?",
            "Confirm deletion", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            contacts.remove(name.toLowerCase());
            refreshTable(contacts);
            clearFields();
        }
    }

    private void searchContacts() {
        String query = searchField.getText().trim().toLowerCase();
        HashMap<String, String> results = new HashMap<>();

        for (Map.Entry<String, String> entry : contacts.entrySet()) {
            if (entry.getKey().contains(query)) {
                results.put(entry.getKey(), entry.getValue());
            }
        }

        refreshTable(results);
    }

    private void refreshTable(Map<String, String> data) {
        model.setRowCount(0);

        data.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String displayName = entry.getKey();
                model.addRow(new Object[]{displayName, entry.getValue()});
            });
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        table.clearSelection();
        nameField.requestFocusInWindow();
    }

    private void message(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}

