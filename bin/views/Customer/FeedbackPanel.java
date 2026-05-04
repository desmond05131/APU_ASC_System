package views.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FeedbackPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private CustomerDashboard dashboard;

    public FeedbackPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // =========================
        // HEADER
        // =========================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        headerPanel.setPreferredSize(new Dimension(0, 50));

        JLabel header = new JLabel("  Feedback & Comments");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(header, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // =========================
        // TABLE
        // =========================
        String[] columns = {"ID", "Topic", "Rating", "Comment"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID not editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);

        // hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // =========================
        // RATING DROPDOWN
        // =========================
        JComboBox<String> ratingCombo = new JComboBox<>(new String[]{
                "1 ⭐", "2 ⭐", "3 ⭐", "4 ⭐", "5 ⭐"
        });
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(ratingCombo));

        // =========================
        // FIX 1: ensure edit commits on focus loss
        // =========================
        table.putClientProperty("terminateEditOnFocusLost", true);

        // =========================
        // FIX 2: SAFE EDIT LISTENER (CORRECT ROW)
        // =========================
        table.getDefaultEditor(Object.class).addCellEditorListener(new javax.swing.event.CellEditorListener() {

            @Override
            public void editingStopped(javax.swing.event.ChangeEvent e) {
                int row = table.getEditingRow(); // ALWAYS correct row

                if (row != -1) {
                    saveEditedRow(row);
                }
            }

            @Override
            public void editingCanceled(javax.swing.event.ChangeEvent e) {
                // no action needed
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        // =========================
        // DELETE MENU
        // =========================
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem deleteItem = new JMenuItem("Delete Feedback");
        deleteItem.addActionListener(e -> deleteSelected());

        popupMenu.add(deleteItem);
        table.setComponentPopupMenu(popupMenu);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1) {
                    table.setRowSelectionInterval(row, row);
                }
            }
        });

        // =========================
        // FOOTER
        // =========================
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
        dashboard.switchContent("ADD_FEEDBACK");
        right.add(addBtn);

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);

        loadFeedback();
    }

    // =========================
    // LOAD DATA
    // =========================
    public void loadFeedback() {

        model.setRowCount(0);

        List<String> lines = FeedbackFileHandler.readAll("feedback.txt");

        for (String line : lines) {
            try {
                Feedback fb = Feedback.fromFileString(line);

                model.addRow(new Object[]{
                        fb.getFeedbackId(),
                        fb.getTopic(),
                        fb.getRating() + " ⭐",
                        fb.getComment()
                });

            } catch (Exception e) {
                System.out.println("Skipping corrupted line");
            }
        }
    }

    // =========================
    // SAVE EDIT (FIXED)
    // =========================
    private void saveEditedRow(int row) {

        if (row < 0 || row >= model.getRowCount()) return;

        String feedbackId = String.valueOf(model.getValueAt(row, 0));

        List<String> lines = FeedbackFileHandler.readAll("feedback.txt");

        for (int i = 0; i < lines.size(); i++) {

            Feedback fb = Feedback.fromFileString(lines.get(i));

            if (fb.getFeedbackId().equals(feedbackId)) {

                String topic = String.valueOf(model.getValueAt(row, 1));
                String comment = String.valueOf(model.getValueAt(row, 3));

                String ratingText = String.valueOf(model.getValueAt(row, 2))
                        .replaceAll("[^0-9]", "");

                int rating = 1;

                try {
                    rating = Integer.parseInt(ratingText);
                } catch (Exception ignored) {}

                fb.setTopic(topic);
                fb.setRating(rating);
                fb.setComment(comment);

                lines.set(i, fb.toFileString());
                break;
            }
        }

        FeedbackFileHandler.writeAll("feedback.txt", lines);
    }

    // =========================
    // DELETE
    // =========================
    private void deleteSelected() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        String feedbackId = String.valueOf(model.getValueAt(row, 0));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this feedback?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            FeedbackFileHandler.delete("feedback.txt", feedbackId);
            loadFeedback();
        }
    }
}