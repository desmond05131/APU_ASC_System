package views.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboardPanel extends JPanel {

    private CustomerDashboard dashboard;

    public CustomerDashboardPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ================= CENTER CONTENT ONLY =================
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);

        JButton btnHistory = new JButton("Service & Payment History");
        JButton btnFeedback = new JButton("Feedback & Comments");

        btnHistory.setFocusPainted(false);
        btnFeedback.setFocusPainted(false);

        btnHistory.addActionListener(e -> dashboard.switchContent("HISTORY"));
        btnFeedback.addActionListener(e -> dashboard.switchContent("FEEDBACK"));

        centerPanel.add(btnHistory);
        centerPanel.add(btnFeedback);

        add(centerPanel, BorderLayout.CENTER);
    }
}