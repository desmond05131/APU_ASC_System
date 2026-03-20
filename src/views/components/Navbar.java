package views.components;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import views.MainFrame;
import views.Manager.ManagerDashboard;

public class Navbar extends JPanel {
    private final ArrayList<NavButton> buttons = new ArrayList<>();
    private final ManagerDashboard dashboard;

    public Navbar(MainFrame parent, ManagerDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 80));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel title = new JLabel("  APU Automotive Service Centre");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.WEST);

        JPanel navItems = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        navItems.setOpaque(false);
        
        setupManagerButtons(navItems);
        add(navItems, BorderLayout.CENTER);

        // Logout logic remains linked to MainFrame
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> parent.showView("LOGIN"));
        add(logoutBtn, BorderLayout.EAST);
    }

    private void setupManagerButtons(JPanel panel) {
        addButton(panel, "My Profile", "profile.png", "profile_on_click.png", "PROFILE");
        addButton(panel, "Manage Staff", "staff.png", "staff.png", "MANAGE_STAFF");
        addButton(panel, "Reports", "report.png", "report.png", "REPORTS");
        
        if (!buttons.isEmpty()) buttons.get(0).setActive(true);
    }

    private void addButton(JPanel panel, String text, String icon, String activeIcon, String viewName) {
        NavButton btn = new NavButton(text, icon, activeIcon);
        btn.addActionListener(e -> {
            for (NavButton b : buttons) b.setActive(false);
            btn.setActive(true);
            
            // Crucial Change: Switch content within the Dashboard, not the Frame
            dashboard.switchContent(viewName);
        });
        buttons.add(btn);
        panel.add(btn);
    }
}