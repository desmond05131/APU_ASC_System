package views.Manager;

import controllers.FeedbackController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import models.Feedback;

public class ReviewFeedbackPanel extends JPanel {
    private JTable            feedbackTable;
    private DefaultTableModel tableModel;
    private JTextField        txtServiceSearch, txtCustomerSearch, txtDateSearch;
    private JComboBox<String> cmbRating, cmbCategory;
    private JCheckBox         showDeletedCb;
    private final FeedbackController controller;
    private ArrayList<Feedback> cachedFeedback = new ArrayList<>();

    public ReviewFeedbackPanel() {
        controller = new FeedbackController();

        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 14, 20));
        setBackground(Color.WHITE);

        add(buildFilterSection(), BorderLayout.NORTH);
        add(buildTable(),         BorderLayout.CENTER);
        add(buildFooter(),        BorderLayout.SOUTH);

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

        // Row 0 — Service Name | Filter by Rating | Category
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0;    box.add(label("Service Name :"), g);
        g.gridx = 1; g.weightx = 0.28; txtServiceSearch = searchField(); box.add(txtServiceSearch, g);
        g.gridx = 2; g.weightx = 0;    box.add(label("Filter by Rating (1-5):"), g);
        g.gridx = 3; g.weightx = 0.16;
        cmbRating = new JComboBox<>(new String[]{"All", "1", "2", "3", "4", "5"});
        cmbRating.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.add(cmbRating, g);
        g.gridx = 4; g.weightx = 0;    box.add(label("Category :"), g);
        g.gridx = 5; g.weightx = 0.22;
        cmbCategory = new JComboBox<>(new String[]{
                "All", "Customer-CounterStaff", "Customer-Technician", "Customer-Overall", "Technician"});
        cmbCategory.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.add(cmbCategory, g);

        // Row 1 — Customer Name | Date
        g.gridy = 1;
        g.gridx = 0; g.weightx = 0;    box.add(label("Customer Name :"), g);
        g.gridx = 1; g.weightx = 0.28; txtCustomerSearch = searchField(); box.add(txtCustomerSearch, g);
        g.gridx = 2; g.weightx = 0;    box.add(label("Date :"), g);
        g.gridx = 3; g.weightx = 0.16; txtDateSearch = searchField(); box.add(txtDateSearch, g);

        // Row 2 — Refresh | Clear | Show deleted checkbox
        g.gridy = 2; g.gridx = 0; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        JButton refresh = styledButton("Refresh", new Color(100, 100, 248));
        refresh.addActionListener(e -> refreshTable());
        box.add(refresh, g);

        g.gridx = 1; g.weightx = 0;
        JButton clear = styledButton("Clear", new Color(200, 48, 60));
        clear.addActionListener(e -> clearFilters());
        box.add(clear, g);

        g.gridx = 2; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL;
        showDeletedCb = new JCheckBox("Show deleted feedback");
        showDeletedCb.setOpaque(false);
        showDeletedCb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        showDeletedCb.addActionListener(e -> applyFilters());
        box.add(showDeletedCb, g);
        g.gridwidth = 1;

        wrapper.add(box, BorderLayout.CENTER);
        return wrapper;
    }

    private JTextField searchField() {
        JTextField f = new JTextField(14);
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
        String[] cols = {"Feedback ID", "Customer Name", "Service Name", "Category", "Rating", "Comment", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        feedbackTable = new JTable(tableModel);
        feedbackTable.setRowHeight(32);
        feedbackTable.setGridColor(new Color(220, 226, 234));
        feedbackTable.setShowGrid(true);
        feedbackTable.setSelectionBackground(new Color(220, 225, 255));
        feedbackTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        feedbackTable.getTableHeader().setBackground(new Color(245, 247, 250));
        feedbackTable.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // Renderer: deleted rows appear in red
        feedbackTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (row >= tableModel.getRowCount()) return c;
                String id = tableModel.getValueAt(row, 0).toString();
                boolean deleted = cachedFeedback.stream()
                        .filter(f -> f.getFeedbackId().equals(id))
                        .findFirst().map(Feedback::isDeleted).orElse(false);
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

        return new JScrollPane(feedbackTable);
    }

    // ------------------------------------------------------------------ Footer
    private JPanel buildFooter() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        row.setOpaque(false);

        JButton btnDelete = styledButton("Delete Feedback", new Color(200, 48, 60));
        btnDelete.addActionListener(e -> handleDelete());
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

    // ------------------------------------------------------------------ Logic
    private void handleDelete() {
        int row = feedbackTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a feedback entry to delete."); return; }
        String id = tableModel.getValueAt(row, 0).toString();
        Feedback f = cachedFeedback.stream().filter(fb -> fb.getFeedbackId().equals(id)).findFirst().orElse(null);
        if (f == null) return;
        if (f.isDeleted()) { JOptionPane.showMessageDialog(this, "This feedback is already deleted."); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete feedback " + id + " from " + f.getCustomerName() + "?\n"
                + "It will be marked as deleted and visible via \"Show deleted feedback\".",
                "Confirm Soft Delete", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            controller.deleteFeedback(id);
            JOptionPane.showMessageDialog(this, "Feedback deleted successfully.");
            refreshTable();
        }
    }

    private void clearFilters() {
        txtServiceSearch.setText("");
        txtCustomerSearch.setText("");
        txtDateSearch.setText("");
        cmbRating.setSelectedIndex(0);
        cmbCategory.setSelectedIndex(0);
        showDeletedCb.setSelected(false);
        refreshTable();
    }

    public final void refreshTable() {
        cachedFeedback = controller.getAllFeedbackIncludingDeleted();
        applyFilters();
    }

    private void applyFilters() {
        String serviceQ  = txtServiceSearch  != null ? txtServiceSearch.getText().trim()  : "";
        String customerQ = txtCustomerSearch != null ? txtCustomerSearch.getText().trim() : "";
        String dateQ     = txtDateSearch     != null ? txtDateSearch.getText().trim()     : "";
        String ratingQ   = cmbRating         != null ? (String) cmbRating.getSelectedItem()   : "All";
        String catQ      = cmbCategory       != null ? (String) cmbCategory.getSelectedItem() : "All";
        boolean showD    = showDeletedCb     != null && showDeletedCb.isSelected();

        tableModel.setRowCount(0);
        for (Feedback f : cachedFeedback) {
            if (f.isDeleted() && !showD) continue;
            if (!serviceQ.isEmpty()  && !f.getServiceName().toLowerCase().contains(serviceQ.toLowerCase())) continue;
            if (!customerQ.isEmpty() && !f.getCustomerName().toLowerCase().contains(customerQ.toLowerCase())) continue;
            if (!dateQ.isEmpty()     && !f.getDate().contains(dateQ)) continue;
            if (!"All".equals(ratingQ) && !String.valueOf(f.getRating()).equals(ratingQ)) continue;
            if (!"All".equals(catQ)    && !f.getCategory().equalsIgnoreCase(catQ)) continue;

            tableModel.addRow(new Object[]{
                f.getFeedbackId(), f.getCustomerName(), f.getServiceName(),
                f.getCategory(), f.getRating(), f.getComment(), f.getDate()
            });
        }
        feedbackTable.repaint();
    }
}
