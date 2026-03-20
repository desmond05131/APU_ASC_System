package views.Manager;

import controllers.StaffController;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.User;

public class ManageStaffPanel extends JPanel {
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private final StaffController controller;
    private final ManagerDashboard dashboard;

    public ManageStaffPanel(ManagerDashboard dashboard) {
        this.dashboard = dashboard;
        this.controller = new StaffController();
        
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        setupTopPanel();
        setupTable();
        setupBottomPanel();
        refreshTable();
    }

    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> filterTable(txtSearch.getText()));
        
        searchPanel.add(new JLabel("Search Name/ID:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        topPanel.add(searchPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void setupTable() {
        String[] columns = {"User ID", "Full Name", "Email Address", "System Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        staffTable = new JTable(tableModel);
        staffTable.setRowHeight(30);
        add(new JScrollPane(staffTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = new JButton("Add New Staff");
        JButton btnEdit = new JButton("Edit Selected");
        JButton btnDelete = new JButton("Delete");

        // Navigate to UserDetailPanel in "Add" mode
        btnAdd.addActionListener(e -> dashboard.openUserDetail(null));

        // Navigate to UserDetailPanel in "Edit" mode with selected user
        btnEdit.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                String id = tableModel.getValueAt(row, 0).toString();
                User selectedUser = controller.getStaffOnly().stream()
                        .filter(u -> u.getId().equals(id)).findFirst().orElse(null);
                dashboard.openUserDetail(selectedUser);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a staff member to edit.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                String id = tableModel.getValueAt(row, 0).toString();
                if (controller.deleteStaff(id)) refreshTable();
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public final void refreshTable() {
        populateTable(controller.getStaffOnly());
    }

    private void filterTable(String query) {
        var filtered = controller.getStaffOnly().stream()
            .filter(u -> u.getName().toLowerCase().contains(query.toLowerCase()) || u.getId().contains(query))
            .toList();
        populateTable(new java.util.ArrayList<>(filtered));
    }

    private void populateTable(java.util.ArrayList<User> staff) {
        tableModel.setRowCount(0);
        for (User u : staff) {
            tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole()});
        }
    }
}