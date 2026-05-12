package views.Customer;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import models.Feedback;
import services.FileHandler;

public class AddFeedbackPanel extends JPanel {

    private final JComboBox<String> ratingBox;
    private final JTextArea txtComment;

    private final CustomerDashboard dashboard;

    public AddFeedbackPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel header = new JLabel("  Add Feedback");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(0, 50));
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        form.add(new JLabel("Rating:"));
        ratingBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        form.add(ratingBox);

        form.add(new JLabel("Comment:"));
        txtComment = new JTextArea(3, 20);
        form.add(new JScrollPane(txtComment));

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();

        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");

        submitBtn.addActionListener(e -> saveFeedback());
        cancelBtn.addActionListener(e -> dashboard.switchContent("FEEDBACK"));

        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void saveFeedback() {
        String comment = txtComment.getText().trim();

        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a comment!");
            return;
        }

        ArrayList<String> existing = FileHandler.readData("feedback.txt");
        String newId = String.format("F%03d", existing.size() + 1);

        int rating = Integer.parseInt((String) ratingBox.getSelectedItem());
        String customerName = dashboard.getMainFrame().getCurrentUser().getName();
        String date = LocalDate.now().toString();

        Feedback fb = new Feedback(newId, rating, customerName, comment, date);
        FileHandler.writeData("feedback.txt", fb.toString());

        JOptionPane.showMessageDialog(this, "Feedback submitted!");
        txtComment.setText("");
        dashboard.switchContent("FEEDBACK");
    }
}
