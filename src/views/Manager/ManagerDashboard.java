package views.Manager;

import java.awt.*;
import javax.swing.*;
import views.MainFrame;
import views.components.Navbar;

public class ManagerDashboard extends JPanel {
    public ManagerDashboard(MainFrame parent) {
        setLayout(new BorderLayout());

        // Add the shared Navbar
        Navbar nav = new Navbar(parent, parent.getCurrentUser());
        add(nav, BorderLayout.NORTH);

        // The content area (this is where ManageProfilePanel will go)
        JPanel contentArea = new JPanel(); 
        add(contentArea, BorderLayout.CENTER);
    }
}