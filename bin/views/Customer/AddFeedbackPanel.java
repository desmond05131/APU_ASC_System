package views.Customer;

import javax.swing.*;
import java.awt.*;

public class AddFeedbackPanel extends JPanel {

    private JTextField txtTopic;
    private JComboBox<String> ratingBox;
    private JTextArea txtComment;

    private CustomerDashboard dashboard;

    public AddFeedbackPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ================= HEADER
        JLabel header = new JLabel("  Add Feedback");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(0, 50));
        add(header, BorderLayout.NORTH);

        // ================= FORM
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // TOPIC
        form.add(new JLabel("Topic:"));
        txtTopic = new JTextField();
        form.add(txtTopic);

        // RATING
        form.add(new JLabel("Rating:"));
        ratingBox = new JComboBox<>(new String[]{
                "1", "2", "3", "4", "5"
        });
        form.add(ratingBox);

        // COMMENT
        form.add(new JLabel("Comment:"));
        txtComment = new JTextArea(3, 20);
        form.add(new JScrollPane(txtComment));

        add(form, BorderLayout.CENTER);

        // ================= BUTTONS
        JPanel btnPanel = new JPanel();

        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");

        submitBtn.addActionListener(e -> saveFeedback());
        cancelBtn.addActionListener(e -> dashboard.switchContent("FEEDBACK"));

        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    // ================= SAVE FEEDBACK
    private void saveFeedback() {

        String topic = txtTopic.getText();
        String ratingStr = ratingBox.getSelectedItem().toString();
        String comment = txtComment.getText();

        if (topic.isEmpty() || comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        int rating = Integer.parseInt(ratingStr);

        // 🔥 NEW STRUCTURE
        Feedback fb = new Feedback(topic, rating, comment);

        FeedbackFileHandler.saveFeedback(fb);

        JOptionPane.showMessageDialog(this, "Feedback submitted!");

        dashboard.switchContent("FEEDBACK");
    }
}