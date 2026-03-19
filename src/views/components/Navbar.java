package views.components;

import java.awt.*;
import javax.swing.*;
import models.User;
import views.MainFrame;

public class Navbar extends JPanel {
    private final MainFrame parent;

    public Navbar(MainFrame parent, User user) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 80));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // 1. Logo/Title Section (Left)
        JLabel title = new JLabel("  APU Automotive Service Centre");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.WEST);

        // 2. Navigation Items (Center)
        JPanel navItems = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navItems.setOpaque(false);
        
        // Role-based logic for Manager
        if (user.getRole().equals("Manager")) {
            addManagerButtons(navItems);
        }
        // Add other roles here later...

        add(navItems, BorderLayout.CENTER);

        // 3. Logout Section (Right)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        rightPanel.setOpaque(false);
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> parent.showView("LOGIN"));
        rightPanel.add(logoutBtn);
        add(rightPanel, BorderLayout.EAST);
    }

    private void addManagerButtons(JPanel panel) {
        NavButton btnProfile = new NavButton("My Profile", "user_icon.png");
        NavButton btnStaff = new NavButton("Manage Staff", "staff_icon.png");
        NavButton btnReports = new NavButton("Analyze Reports", "reports_icon.png");
        NavButton btnService = new NavButton("Manage Service", "car_icon.png");
        NavButton btnFeedback = new NavButton("Review Feedback", "feedback_icon.png");

        // Set 'My Profile' as active by default
        btnProfile.setActive(true);

        panel.add(btnProfile);
        panel.add(btnStaff);
        panel.add(btnReports);
        panel.add(btnService);
        panel.add(btnFeedback);
    }
}