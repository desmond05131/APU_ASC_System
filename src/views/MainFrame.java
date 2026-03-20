package views;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import models.User;
import views.Manager.ManagerDashboard;
// Import other dashboards as you create them
// import views.Technician.TechnicianDashboard;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private User currentUser;
    private final Map<String, JPanel> dashboards = new HashMap<>();

    public MainFrame() {
        setTitle("APU Automotive Service Centre");
        setSize(1000, 750); // Increased size for better dashboard visibility
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 1. Register Login Panel
        mainPanel.add(new LoginPanel(this), "LOGIN");

        // 2. Dashboards will be created lazily when first accessed
        // This avoids NullPointerException when dashboards try to access currentUser before login

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showView(String viewName) {
        // Lazily initialize dashboard if not already created
        if (!dashboards.containsKey(viewName)) {
            JPanel dashboard = createDashboard(viewName);
            if (dashboard != null) {
                dashboards.put(viewName, dashboard);
                mainPanel.add(dashboard, viewName);
            }
        }
        cardLayout.show(mainPanel, viewName);
    }

    private JPanel createDashboard(String role) {
        return switch (role) {
            case "MANAGER" -> new ManagerDashboard(this);
            // case "TECHNICIAN" -> new TechnicianDashboard(this);
            default -> null;
        };
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}