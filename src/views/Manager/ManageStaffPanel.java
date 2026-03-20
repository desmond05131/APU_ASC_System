package views.Manager;

import controllers.StaffController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Technician;
import models.User;

public class ManageStaffPanel extends JPanel {
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtName, txtUsername, txtId;
    private JPasswordField txtPassword;
    private JComboBox<String> roleCombo;
    private final StaffController controller;

    public ManageStaffPanel() {
        controller = new StaffController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setupSearchPanel();
        setupTable();
        setupFormPanel();
        refreshTable();
    }

    private void setupSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        JButton btnReset = new JButton("Reset");

        btnSearch.addActionListener(e -> filterTable(txtSearch.getText()));
        btnReset.addActionListener(e -> { txtSearch.setText(""); refreshTable(); });

        searchPanel.add(new JLabel("Search Name/ID:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);
        add(searchPanel, BorderLayout.NORTH);
    }

    private void setupTable() {
        String[] columns = {"ID", "Name", "Email", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        staffTable = new JTable(tableModel);
        
        // Load data into form when row is clicked
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                Object email = tableModel.getValueAt(row, 2);
                txtUsername.setText(email != null ? email.toString() : "");
                roleCombo.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                txtPassword.setEnabled(false); // Can't edit password here
            }
        });

        add(new JScrollPane(staffTable), BorderLayout.CENTER);
    }

    private void setupFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(300, 400));
        form.setBorder(BorderFactory.createTitledBorder("Staff Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        txtId = new JTextField(); txtId.setEditable(false);
        txtName = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        roleCombo = new JComboBox<>(new String[]{"Technician", "Counter Staff"});

        addComponent(form, new JLabel("ID:"), gbc, 0, 0);
        addComponent(form, txtId, gbc, 1, 0);
        addComponent(form, new JLabel("Name:"), gbc, 0, 1);
        addComponent(form, txtName, gbc, 1, 1);
        addComponent(form, new JLabel("Username:"), gbc, 0, 2);
        addComponent(form, txtUsername, gbc, 1, 2);
        addComponent(form, new JLabel("Password:"), gbc, 0, 3);
        addComponent(form, txtPassword, gbc, 1, 3);
        addComponent(form, new JLabel("Role:"), gbc, 0, 4);
        addComponent(form, roleCombo, gbc, 1, 4);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        btnAdd.addActionListener(e -> {
            if (controller.addStaff(txtName.getText(), txtUsername.getText(), 
                new String(txtPassword.getPassword()), roleCombo.getSelectedItem().toString())) {
                refreshTable();
                clearForm();
            }
        });

        btnDelete.addActionListener(e -> {
            if (controller.deleteStaff(txtId.getText())) {
                refreshTable();
                clearForm();
            }
        });

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        add(form, BorderLayout.EAST);
    }

    private void addComponent(JPanel p, JComponent c, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x; gbc.gridy = y; p.add(c, gbc);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        ArrayList<User> staff = controller.getStaffOnly();
        if (staff != null) {
            for (User u : staff) {
                if (u != null) {
                    String role = u instanceof Technician ? "Technician" : "Counter Staff";
                    String email = u.getEmail();
                    String emailStr = (email == null) ? "" : email;
                    tableModel.addRow(new Object[]{u.getId(), u.getName(), emailStr, role});
                }
            }
        }
    }

    private void filterTable(String query) {
        // Simple search logic
        tableModel.setRowCount(0);
        ArrayList<User> staff = controller.getStaffOnly();
        if (staff != null) {
            for (User u : staff) {
                if (u != null && u.getName() != null && u.getId() != null) {
                    if (u.getName().toLowerCase().contains(query.toLowerCase()) || u.getId().contains(query)) {
                        String role = u instanceof Technician ? "Technician" : "Counter Staff";
                        String email = u.getEmail();
                        String emailStr = (email == null) ? "" : email;
                        tableModel.addRow(new Object[]{u.getId(), u.getName(), emailStr, role});
                    }
                }
            }
        }
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtUsername.setText("");
        txtPassword.setText(""); txtPassword.setEnabled(true);
    }
}