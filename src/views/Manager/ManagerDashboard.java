package views.Manager;

import java.awt.*;
import javax.swing.*;
import views.MainFrame;
import views.ManageProfilePanel;
import views.components.Navbar;

public class ManagerDashboard extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel contentArea;

    public ManagerDashboard(MainFrame parent) {
        setLayout(new BorderLayout());

        // 1. Setup internal CardLayout for sub-panels
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        // 2. Initialize sub-panels
        // We pass 'parent' so ManageProfilePanel can get the current user
        contentArea.add(new ManageProfilePanel(parent), "PROFILE");
        
        // Add stubs for other Manager panels
        contentArea.add(new JPanel(), "MANAGE_STAFF"); // Placeholder
        contentArea.add(new JPanel(), "REPORTS");      // Placeholder

        // 3. Add Navbar and pass this dashboard instance to it
        Navbar nav = new Navbar(parent, this);
        add(nav, BorderLayout.NORTH);
        add(contentArea, BorderLayout.CENTER);
    }

    // Method for the Navbar to call
    public void switchContent(String viewName) {
        cardLayout.show(contentArea, viewName);
    }
}