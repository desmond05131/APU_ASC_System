package views;

import java.awt.*;
import javax.swing.*;
import models.User;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private User currentUser;

    public MainFrame() {
        setTitle("APU Automotive Service Centre");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize and add the Login Panel
        LoginPanel loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "LOGIN");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // Centralized method to switch views safely
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
        // Ensure UI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}