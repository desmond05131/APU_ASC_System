package views;

import java.awt.*;
import javax.swing.*;
import models.User;
import views.Manager.ManagerDashboard;
// Import other dashboards as you create them
// import views.Technician.TechnicianDashboard;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private User currentUser;

    public MainFrame() {
        setTitle("APU Automotive Service Centre");
        setSize(1000, 750); // Increased size for better dashboard visibility
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 1. Register Login Panel
        mainPanel.add(new LoginPanel(this), "LOGIN");

        // 2. Register Dashboards (Add others here as stubs are filled)
        // These keys MUST match the roles returned by your AuthController
        mainPanel.add(new ManagerDashboard(this), "MANAGER");
        // mainPanel.add(new TechnicianDashboard(this), "TECHNICIAN");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showView(String viewName) {
        cardLayout.show(mainPanel, viewName);
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