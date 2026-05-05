package views.components;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import views.Dashboard;
import views.MainFrame;

public class Navbar extends JPanel {
    private final ArrayList<NavButton> buttons = new ArrayList<>();

    public Navbar(MainFrame parent, Dashboard dashboard, String role) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 210, 220)));

        // --- Row 1: Header strip ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(176, 196, 218));
        header.setPreferredSize(new Dimension(0, 52));
        header.setMaximumSize(new Dimension(Short.MAX_VALUE, 52));
        header.setMinimumSize(new Dimension(0, 52));
        header.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        JLabel titleLabel = new JLabel("APU Automotive Service Centre");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(25, 30, 55));
        header.add(titleLabel, BorderLayout.WEST);

        // --- Row 2: Nav tabs + Logout ---
        JPanel navRow = new JPanel(new BorderLayout());
        navRow.setBackground(Color.WHITE);
        navRow.setPreferredSize(new Dimension(0, 78));
        navRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 78));
        navRow.setMinimumSize(new Dimension(0, 78));
        navRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 232)));

        JPanel navItems = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 8));
        navItems.setOpaque(false);
        navItems.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        setupButtons(navItems, dashboard, role);

        JPanel logoutSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 20));
        logoutSection.setOpaque(false);
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 223, 228));
        logoutBtn.setForeground(new Color(50, 55, 75));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(195, 200, 212)),
            BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> parent.showView("LOGIN"));
        logoutSection.add(logoutBtn);

        navRow.add(navItems, BorderLayout.CENTER);
        navRow.add(logoutSection, BorderLayout.EAST);

        add(header);
        add(navRow);
    }

    private void setupButtons(JPanel panel, Dashboard dashboard, String role) {
        switch (role) {
            case "Manager" -> {
                addNavButton(panel, dashboard, "My Profile",      "profile.png",  "profile_on_click.png", "PROFILE");
                addNavButton(panel, dashboard, "Manage Staff",    "staff.png",    "staff.png",            "MANAGE_STAFF");
                addNavButton(panel, dashboard, "Manage Reports",  "report.png",   "report.png",           "REPORTS");
                addNavButton(panel, dashboard, "Manage Service",  "service.png",  "service.png",          "MANAGE_SERVICES");
                addNavButton(panel, dashboard, "Review Feedback", "feedback.png", "feedback.png",         "REVIEW_FEEDBACK");
            }
            // Future roles — add cases here as each dashboard is implemented:
            // case "Technician"   -> { ... }
            // case "CounterStaff" -> { ... }
            // case "Customer"     -> { ... }
        }
        if (!buttons.isEmpty()) buttons.get(0).setActive(true);
    }

    private void addNavButton(JPanel panel, Dashboard dashboard,
                              String text, String icon, String activeIcon, String viewName) {
        NavButton btn = new NavButton(text, icon, activeIcon);
        btn.addActionListener(e -> {
            buttons.forEach(b -> b.setActive(false));
            btn.setActive(true);
            dashboard.switchContent(viewName);
        });
        buttons.add(btn);
        panel.add(btn);
    }
}
