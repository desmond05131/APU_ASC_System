package views.components;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import models.User;
import views.MainFrame;

public class Navbar extends JPanel {
    private final MainFrame parent;
    private final ArrayList<NavButton> buttons = new ArrayList<>();

    public Navbar(MainFrame parent, User user) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 80));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Left: Logo/Title
        JLabel title = new JLabel("  APU Automotive Service Centre");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.WEST);

        // Center: Navigation Items
        JPanel navItems = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        navItems.setOpaque(false);
        
        if (user.getRole().equals("Manager")) {
            setupManagerButtons(navItems);
        }

        add(navItems, BorderLayout.CENTER);

        // Right: Logout
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        logoutPanel.setOpaque(false);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setIcon(new ImageIcon(new ImageIcon("src/assets/icons/logout-svgrepo-com.png")
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        logoutBtn.addActionListener(e -> parent.showView("LOGIN"));
        
        logoutPanel.add(logoutBtn);
        add(logoutPanel, BorderLayout.EAST);
    }

    private void setupManagerButtons(JPanel panel) {
        // Define buttons with their default and active icons
        addButton(panel, "My Profile", "profile.png", "profile_on_click.png", "PROFILE");
        addButton(panel, "Manage Staff", "staff.png", "staff_on_click.png", "MANAGE_STAFF");
        addButton(panel, "Manage Reports", "report.png", "report_on_click.png", "REPORTS");
        addButton(panel, "Manage Service", "service.png", "service_on_click.png", "SERVICE");
        addButton(panel, "Review Feedback", "feedback.png", "feedback_on_click.png", "FEEDBACK");

        // Set 'My Profile' as the initial active button
        if (!buttons.isEmpty()) buttons.get(0).setActive(true);
    }

    private void addButton(JPanel panel, String text, String icon, String activeIcon, String viewName) {
        NavButton btn = new NavButton(text, icon, activeIcon);
        btn.addActionListener(e -> {
            // Reset all buttons and set current as active
            for (NavButton b : buttons) b.setActive(false);
            btn.setActive(true);
            
            // Switch the view in MainFrame
            parent.showView(viewName);
        });
        buttons.add(btn);
        panel.add(btn);
    }
}