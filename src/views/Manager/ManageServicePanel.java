package views.Manager;

import controllers.ServiceController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import models.Service;

public class ManageServicePanel extends JPanel {
    private JTable             serviceTable;
    private DefaultTableModel  tableModel;
    private JTextField         txtNameSearch, txtIdSearch, txtPriceSearch;
    private JComboBox<String>  catFilter;
    private JCheckBox          showDeletedCb;
    private final ServiceController controller;
    private final ManagerDashboard  dashboard;
    private ArrayList<Service> cachedServices = new ArrayList<>();

    public ManageServicePanel(ManagerDashboard dashboard) {
        this.dashboard  = dashboard;
        this.controller = new ServiceController();

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

        // Row 0 — Service Name | Price
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0;     box.add(label("Service Name :"), g);
        g.gridx = 1; g.weightx = 0.35;  txtNameSearch = searchField(); box.add(txtNameSearch, g);
        g.gridx = 2; g.weightx = 0;     box.add(searchIconButton(), g);
        g.gridx = 3; g.weightx = 0;     box.add(label("Price :"), g);
        g.gridx = 4; g.weightx = 0.25;  txtPriceSearch = searchField(); box.add(txtPriceSearch, g);

        // Row 1 — Service ID | Category
        g.gridy = 1;
        g.gridx = 0; g.weightx = 0;     box.add(label("Service ID :"), g);
        g.gridx = 1; g.weightx = 0.35;  txtIdSearch = searchField(); box.add(txtIdSearch, g);
        g.gridx = 2; g.weightx = 0;     box.add(searchIconButton(), g);
        g.gridx = 3; g.weightx = 0;     box.add(label("Category :"), g);
        g.gridx = 4; g.weightx = 0.25;
        catFilter = new JComboBox<>(new String[]{"All", "Normal", "Major"});
        catFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.add(catFilter, g);

        // Row 2 — Refresh + Show deleted checkbox
        g.gridy = 2; g.gridx = 0; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        JButton refresh = styledButton("Refresh", new Color(100, 100, 248));
        refresh.addActionListener(e -> refreshTable());
        box.add(refresh, g);

        g.gridx = 1; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL;
        showDeletedCb = new JCheckBox("Show deleted service");
        showDeletedCb.setOpaque(false);
        showDeletedCb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        showDeletedCb.addActionListener(e -> applyFilters());
        box.add(showDeletedCb, g);
        g.gridwidth = 1;

        wrapper.add(box, BorderLayout.CENTER);
        return wrapper;
    }

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
        String[] cols = {"Service ID", "Service Name", "Category", "Description", "Price"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        serviceTable = new JTable(tableModel);
        serviceTable.setRowHeight(32);
        serviceTable.setGridColor(new Color(220, 226, 234));
        serviceTable.setShowGrid(true);
        serviceTable.setSelectionBackground(new Color(220, 225, 255));
        serviceTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        serviceTable.getTableHeader().setBackground(new Color(245, 247, 250));
        serviceTable.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // Renderer: deleted rows appear in red
        serviceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (row >= tableModel.getRowCount()) return c;
                String id = tableModel.getValueAt(row, 0).toString();
                boolean deleted = cachedServices.stream()
                        .filter(s -> s.getId().equals(id))
                        .findFirst().map(Service::isDeleted).orElse(false);
                if (deleted) {
                    c.setForeground(new Color(180, 30, 30));
                    c.setBackground(isSelected ? new Color(255, 190, 190) : new Color(255, 238, 238));
                } else {
                    c.setForeground(Color.BLACK);
                    c.setBackground(isSelected ? new Color(220, 225, 255) : Color.WHITE);
                }
                return c;
            }
        });

        return new JScrollPane(serviceTable);
    }

    // ------------------------------------------------------------------ Buttons
    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        row.setOpaque(false);

        JButton btnAdd    = styledButton("Add Service",    new Color(100, 100, 248));
        JButton btnUpdate = styledButton("Update Service", new Color(100, 100, 248));
        JButton btnClear  = styledButton("Clear",          new Color(100, 100, 248));
        JButton btnDelete = styledButton("Delete Service", new Color(200, 48, 60));

        btnAdd.addActionListener(e -> dashboard.showServiceDetail(null));

        btnUpdate.addActionListener(e -> {
            Service s = getSelectedService();
            if (s == null)       { showMsg("Please select a service to update."); return; }
            if (s.isDeleted())   { showMsg("Cannot edit a deleted service.");      return; }
            dashboard.showServiceDetail(s);
        });

        btnClear.addActionListener(e -> {
            txtNameSearch.setText("");
            txtIdSearch.setText("");
            txtPriceSearch.setText("");
            catFilter.setSelectedIndex(0);
            showDeletedCb.setSelected(false);
            refreshTable();
        });

        btnDelete.addActionListener(e -> {
            Service s = getSelectedService();
            if (s == null)     { showMsg("Please select a service to delete."); return; }
            if (s.isDeleted()) { showMsg("This service is already deleted.");   return; }
            int ok = JOptionPane.showConfirmDialog(this,
                "Delete \"" + s.getName() + "\" (" + s.getId() + ")?\n"
                + "It will be marked as deleted and visible via \"Show deleted service\".",
                "Confirm Soft Delete", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                controller.deleteService(s.getId());
                JOptionPane.showMessageDialog(this, "Service deleted successfully.");
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
    private Service getSelectedService() {
        int row = serviceTable.getSelectedRow();
        if (row == -1) return null;
        String id = tableModel.getValueAt(row, 0).toString();
        return cachedServices.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public final void refreshTable() {
        cachedServices = controller.getAllServicesIncludingDeleted();
        applyFilters();
    }

    private void applyFilters() {
        String nameQ  = txtNameSearch  != null ? txtNameSearch.getText().trim().toLowerCase()  : "";
        String idQ    = txtIdSearch    != null ? txtIdSearch.getText().trim().toLowerCase()    : "";
        String priceQ = txtPriceSearch != null ? txtPriceSearch.getText().trim()               : "";
        String catQ   = catFilter      != null ? (String) catFilter.getSelectedItem()          : "All";
        boolean showD = showDeletedCb  != null && showDeletedCb.isSelected();

        tableModel.setRowCount(0);
        for (Service s : cachedServices) {
            if (s.isDeleted() && !showD) continue;
            boolean matchName  = nameQ.isEmpty()  || s.getName().toLowerCase().contains(nameQ);
            boolean matchId    = idQ.isEmpty()    || s.getId().toLowerCase().contains(idQ);
            boolean matchPrice = priceQ.isEmpty() || String.format("%.2f", s.getPrice()).contains(priceQ);
            boolean matchCat   = "All".equals(catQ) || s.getCategory().equalsIgnoreCase(catQ);

            if (matchName && matchId && matchPrice && matchCat) {
                tableModel.addRow(new Object[]{
                    s.getId(), s.getName(), s.getCategory(),
                    s.getDescription(), String.format("RM %.2f", s.getPrice())
                });
            }
        }
        serviceTable.repaint();
    }
}
