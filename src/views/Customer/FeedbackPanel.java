package views.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import models.Feedback;
import services.FileHandler;

public final class FeedbackPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;
    private final CustomerDashboard dashboard;

    private static final int COL_ID       = 0;
    private static final int COL_RATING   = 1;
    private static final int COL_CUSTOMER = 2;
    private static final int COL_COMMENT  = 3;
    private static final int COL_DATE     = 4;

    public FeedbackPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        JLabel header = new JLabel("  Feedback & Comments");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(header, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Rating", "Customer", "Comment", "Date"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == COL_RATING || column == COL_COMMENT;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);

        // Hide ID column
        table.getColumnModel().getColumn(COL_ID).setMinWidth(0);
        table.getColumnModel().getColumn(COL_ID).setMaxWidth(0);
        table.getColumnModel().getColumn(COL_ID).setWidth(0);

        JComboBox<String> ratingCombo = new JComboBox<>(
                new String[]{"1 ⭐", "2 ⭐", "3 ⭐", "4 ⭐", "5 ⭐"});
        table.getColumnModel().getColumn(COL_RATING).setCellEditor(new DefaultCellEditor(ratingCombo));
        table.putClientProperty("terminateEditOnFocusLost", true);

        table.getDefaultEditor(Object.class).addCellEditorListener(
                new javax.swing.event.CellEditorListener() {
                    @Override public void editingStopped(javax.swing.event.ChangeEvent e) {
                        int row = table.getEditingRow();
                        if (row != -1) saveEditedRow(row);
                    }
                    @Override public void editingCanceled(javax.swing.event.ChangeEvent e) {}
                });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Feedback");
        deleteItem.addActionListener(e -> deleteSelected());
        popupMenu.add(deleteItem);
        table.setComponentPopupMenu(popupMenu);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1) table.setRowSelectionInterval(row, row);
            }
        });

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setBackground(Color.WHITE);
        JButton backBtn = new JButton("← Back");
        backBtn.addActionListener(e -> dashboard.switchContent("DASHBOARD"));
        left.add(backBtn);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(Color.WHITE);
        JButton addBtn = new JButton("Add Feedback");
        addBtn.addActionListener(e -> dashboard.switchContent("ADD_FEEDBACK"));
        right.add(addBtn);

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        loadFeedback();
    }

    public void loadFeedback() {
        model.setRowCount(0);
        String currentId = dashboard.getMainFrame().getCurrentUser().getId();
        for (String line : FileHandler.readData("feedback.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 5) continue;

            boolean isNew = p.length >= 3 && !isNumeric(p[1]);
            if (isNew) {
                // New format: feedbackId|customerId|rating|customerName|comment|date|...|status
                if (!p[1].equals(currentId)) continue;
                if (p.length >= 9 && "DELETED".equals(p[8])) continue;
                try {
                    int rating = Integer.parseInt(p[2].trim());
                    model.addRow(new Object[]{p[0], rating + " ⭐", p[3], p[4], p[5]});
                } catch (NumberFormatException ignored) {}
            } else {
                // Legacy format: feedbackId|rating|customerName|comment|date|...|status
                // No customerId - can only show; skip so we don't show other people's data
                // (After full migration this branch is unreachable)
            }
        }
    }

    private void saveEditedRow(int row) {
        if (row < 0 || row >= model.getRowCount()) return;

        String feedbackId   = String.valueOf(model.getValueAt(row, COL_ID));
        String customerName = String.valueOf(model.getValueAt(row, COL_CUSTOMER));
        String date         = String.valueOf(model.getValueAt(row, COL_DATE));
        String comment      = String.valueOf(model.getValueAt(row, COL_COMMENT));
        String ratingText   = String.valueOf(model.getValueAt(row, COL_RATING)).replaceAll("[^0-9]", "");
        int rating = 1;
        try { rating = Integer.parseInt(ratingText); } catch (NumberFormatException ignored) {}

        // Read existing line to preserve customerId, serviceName, category
        String customerId = "", serviceName = "", category = "";
        for (String line : FileHandler.readData("feedback.txt")) {
            if (!line.startsWith(feedbackId + "|")) continue;
            String[] p = line.split("\\|");
            boolean isNew = p.length >= 3 && !isNumeric(p[1]);
            if (isNew) {
                customerId  = p.length > 1 ? p[1] : "";
                serviceName = p.length > 6 ? p[6] : "";
                category    = p.length > 7 ? p[7] : "";
            } else {
                serviceName = p.length > 5 ? p[5] : "";
                category    = p.length > 6 ? p[6] : "";
            }
            break;
        }

        Feedback updated = new Feedback(feedbackId, customerId, rating, customerName, comment, date);
        updated.setServiceName(serviceName);
        updated.setCategory(category);
        FileHandler.updateLine("feedback.txt", feedbackId, updated.toString());
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String feedbackId = String.valueOf(model.getValueAt(row, COL_ID));
        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete this feedback?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ArrayList<String> lines = FileHandler.readData("feedback.txt");
            lines.removeIf(line -> line.startsWith(feedbackId + "|"));
            FileHandler.writeData("feedback.txt", lines);
            loadFeedback();
        }
    }

    private static boolean isNumeric(String s) {
        try { Integer.parseInt(s.trim()); return true; }
        catch (NumberFormatException e) { return false; }
    }
}
