package views.Manager;

import controllers.StaffController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import models.User;

public class ManageStaffPanel extends JPanel {
    private JTable             staffTable;
    private DefaultTableModel  tableModel;
    private JTextField         txtNameSearch, txtIdSearch;
    private JComboBox<String>  roleFilter;
    private JCheckBox          showDeletedCb;
    private final StaffController  controller;
    private final ManagerDashboard dashboard;
    // Always holds ALL users (active + deleted) so the renderer can look up deleted status.
    private ArrayList<User> cachedUsers = new ArrayList<>();

    public ManageStaffPanel(ManagerDashboard dashboard) {
        this.dashboard  = dashboard;
        this.controller = new StaffController();

        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 14, 20));
        setBackground(Color.WHITE);

        add(buildFilterSection(), BorderLayout.NORTH);
        add(buildTable(),         BorderLayout.CENTER);
        add(buildButtonRow(),     BorderLayout.SOUTH);

        refreshTable();
    }

    // ------------------------------------------------------------------ Filter
    private JPanel buildFilterSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);

        JLabel title = new JLabel("Filtering");
        title.setFont(new Font("SansSerif", Font.PLAIN, 13));
        title.setForeground(new Color(80, 80, 100));
        wrapper.add(title, BorderLayout.NORTH);

        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 222)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 14, 10, 14);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Row 0 — Staff Name | Role
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        box.add(label("Staff Name :"), g);

        g.gridx = 1; g.weightx = 0.35;
        txtNameSearch = searchField();
        box.add(txtNameSearch, g);

        g.gridx = 2; g.weightx = 0;
        box.add(searchIconButton(), g);

        g.gridx = 3; g.weightx = 0;
        box.add(label("Role :"), g);

        g.gridx = 4; g.weightx = 0.25;
        roleFilter = new JComboBox<>(new String[]{"All", "Manager", "Counter Staff", "Technician"});
        roleFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.add(roleFilter, g);

        // Row 1 — User ID
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        box.add(label("User ID :"), g);

        g.gridx = 1; g.weightx = 0.35;
        txtIdSearch = searchField();
        box.add(txtIdSearch, g);

        g.gridx = 2; g.weightx = 0;
        box.add(searchIconButton(), g);

        // Row 2 — Refresh + Show deleted checkbox
        g.gridx = 0; g.gridy = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        JButton refresh = styledButton("Refresh", new Color(100, 100, 248));
        refresh.addActionListener(e -> refreshTable());
        box.add(refresh, g);

        g.gridx = 1; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL;
        showDeletedCb = new JCheckBox("Show deleted account");
        showDeletedCb.setOpaque(false);
        showDeletedCb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        showDeletedCb.addActionListener(e -> applyFilters());
        box.add(showDeletedCb, g);
        g.gridwidth = 1;

        wrapper.add(box, BorderLayout.CENTER);
        return wrapper;
    }

    /** A small icon-only button that triggers the filter — same behaviour as Refresh. */
    private JButton searchIconButton() {
        JButton btn = new JButton("🔍");
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Search");
        btn.addActionListener(e -> applyFilters());
        return btn;
    }

    private JTextField searchField() {
        JTextField f = new JTextField(16);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return f;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    // ------------------------------------------------------------------ Table
    private JScrollPane buildTable() {
        String[] cols = {"User ID", "User Name", "Role", "Email", "Contact Number", "Password"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        staffTable = new JTable(tableModel);
        staffTable.setRowHeight(32);
        staffTable.setGridColor(new Color(220, 226, 234));
        staffTable.setShowGrid(true);
        staffTable.setSelectionBackground(new Color(220, 225, 255));
        staffTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        staffTable.getTableHeader().setBackground(new Color(245, 247, 250));
        staffTable.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // Renderer: deleted rows appear in red
        staffTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (row >= tableModel.getRowCount()) return c;
                String id = tableModel.getValueAt(row, 0).toString();
                boolean deleted = cachedUsers.stream()
                        .filter(u -> u.getId().equals(id))
                        .findFirst()
                        .map(User::isDeleted)
                        .orElse(false);
                if (deleted) {
                    c.setForeground(new Color(180, 30, 30));
                    c.setBackground(isSelected ? new Color(255, 190, 190)
                                               : new Color(255, 238, 238));
                } else {
                    c.setForeground(Color.BLACK);
                    c.setBackground(isSelected ? new Color(220, 225, 255) : Color.WHITE);
                }
                return c;
            }
        });

        return new JScrollPane(staffTable);
    }

    // ------------------------------------------------------------------ Buttons
    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        row.setOpaque(false);

        JButton btnAdd    = styledButton("Add Staff",    new Color(100, 100, 248));
        JButton btnUpdate = styledButton("Update Staff", new Color(100, 100, 248));
        JButton btnClear  = styledButton("Clear",        new Color(100, 100, 248));
        JButton btnDelete = styledButton("Delete Staff", new Color(200, 48, 60));

        btnAdd.addActionListener(e -> dashboard.openUserDetail(null));

        btnUpdate.addActionListener(e -> {
            User u = getSelectedUser();
            if (u == null) { showMsg("Please select a staff member to update."); return; }
            if (u.isDeleted()) { showMsg("Cannot edit a deleted account."); return; }
            dashboard.openUserDetail(u);
        });

        btnClear.addActionListener(e -> {
            txtNameSearch.setText("");
            txtIdSearch.setText("");
            roleFilter.setSelectedIndex(0);
            showDeletedCb.setSelected(false);
            refreshTable();
        });

        btnDelete.addActionListener(e -> {
            User u = getSelectedUser();
            if (u == null)       { showMsg("Please select a staff member to delete."); return; }
            if (u.isDeleted())   { showMsg("This account is already deleted.");        return; }
            int ok = JOptionPane.showConfirmDialog(this,
                "Delete " + u.getName() + " (" + u.getId() + ")?\n"
                + "The account will be marked as deleted and can be viewed via "
                + "\"Show deleted account\".",
                "Confirm Soft Delete", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION && controller.deleteStaff(u.getId())) {
                JOptionPane.showMessageDialog(this, "Account deleted successfully.");
                refreshTable();
            }
        });

        row.add(btnAdd);
        row.add(btnUpdate);
        row.add(btnClear);
        row.add(btnDelete);
        return row;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ------------------------------------------------------------------ Helpers
    private User getSelectedUser() {
        int row = staffTable.getSelectedRow();
        if (row == -1) return null;
        String id = tableModel.getValueAt(row, 0).toString();
        return cachedUsers.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    /** Reload from file (picks up any external changes) then re-apply filters. */
    public final void refreshTable() {
        cachedUsers = controller.getAllUsersIncludingDeleted();
        applyFilters();
    }

    private void applyFilters() {
        String nameQ  = txtNameSearch != null ? txtNameSearch.getText().trim().toLowerCase() : "";
        String idQ    = txtIdSearch   != null ? txtIdSearch.getText().trim().toLowerCase()   : "";
        String roleQ  = roleFilter    != null ? (String) roleFilter.getSelectedItem()        : "All";
        boolean showD = showDeletedCb != null && showDeletedCb.isSelected();

        tableModel.setRowCount(0);
        for (User u : cachedUsers) {
            // Hide deleted users unless the checkbox is ticked
            if (u.isDeleted() && !showD) continue;

            boolean matchName = nameQ.isEmpty() || u.getName().toLowerCase().contains(nameQ);
            boolean matchId   = idQ.isEmpty()   || u.getId().toLowerCase().contains(idQ);
            boolean matchRole = "All".equals(roleQ)
                    || u.getRole().equalsIgnoreCase(roleQ.replace(" ", ""));

            if (matchName && matchId && matchRole) {
                tableModel.addRow(new Object[]{
                    u.getId(), u.getName(), u.getRole(),
                    u.getEmail(), u.getContactNumber(), "*******"
                });
            }
        }
        // Force the custom renderer to repaint so red rows appear immediately
        staffTable.repaint();
    }
}
