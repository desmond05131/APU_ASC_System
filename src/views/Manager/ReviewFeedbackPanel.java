package views.Manager;

import controllers.FeedbackController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Feedback;

public class ReviewFeedbackPanel extends JPanel {
    private final JTable feedbackTable;
    private final DefaultTableModel tableModel;
    private JTextField txtSearchId;
    private JComboBox<String> cmbRating;
    private FeedbackController controller;

    public ReviewFeedbackPanel() {
        controller = new FeedbackController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Header Section ---
        JLabel lblTitle = new JLabel("Review Feedback History", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        // --- Filter Section ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Feedback ID:"));
        txtSearchId = new JTextField(15);
        filterPanel.add(txtSearchId);

        filterPanel.add(new JLabel("Rating:"));
        String[] ratings = {"All", "1 Star", "2 Star", "3 Star", "4 Star", "5 Star"};
        cmbRating = new JComboBox<>(ratings);
        filterPanel.add(cmbRating);

        JButton btnSearch = new JButton("Search");
        JButton btnReset = new JButton("Reset");
        filterPanel.add(btnSearch);
        filterPanel.add(btnReset);

        // --- Table Section ---
        String[] columns = {"Feedback ID", "Customer Name", "Rating", "Comments", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        feedbackTable = new JTable(tableModel);
        feedbackTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(feedbackTable);

        // Assemble Middle
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- Footer Section ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Close");
        footerPanel.add(btnClose);
        add(footerPanel, BorderLayout.SOUTH);

        // --- Event Listeners ---
        btnSearch.addActionListener(e -> performSearch());
        btnReset.addActionListener(e -> {
            txtSearchId.setText("");
            cmbRating.setSelectedIndex(0);
            loadTableData(controller.getAllFeedback());
        });
        
        // Initial Data Load
        loadTableData(controller.getAllFeedback());
    }

    private void loadTableData(ArrayList<Feedback> list) {
        tableModel.setRowCount(0);
        for (Feedback f : list) {
            tableModel.addRow(new Object[]{
                f.getFeedbackId(),
                f.getCustomerName(),
                f.getRating() + " Stars",
                f.getComment(),
                f.getDate()
            });
        }
    }

    private void performSearch() {
        String id = txtSearchId.getText().trim();
        String rating = (String) cmbRating.getSelectedItem();
        loadTableData(controller.search(id, rating));
    }
}