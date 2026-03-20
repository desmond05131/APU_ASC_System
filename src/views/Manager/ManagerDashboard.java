package views.Manager;

import java.awt.*;
import javax.swing.*;
import models.User;
import views.MainFrame;
import views.ManageProfilePanel;
import views.components.Navbar;

public class ManagerDashboard extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel contentArea;
    private final ManageStaffPanel staffPanel;
    private final ManageServicePanel servicePanel;
    private final AnalyzeReportPanel reportPanel; // Added the report panel reference

    public ManagerDashboard(MainFrame parent) {
        setLayout(new BorderLayout());

        // 1. Setup internal CardLayout for sub-panels
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        // 2. Initialize sub-panels
        // We pass 'parent' so ManageProfilePanel can get the current user
        contentArea.add(new ManageProfilePanel(parent), "PROFILE");
        
        staffPanel = new ManageStaffPanel(this);
        contentArea.add(staffPanel, "MANAGE_STAFF");
        
        servicePanel = new ManageServicePanel(this);
        contentArea.add(servicePanel, "MANAGE_SERVICES");
        
        // Replaced the placeholder with the actual AnalyzeReportPanel
        reportPanel = new AnalyzeReportPanel(); 
        contentArea.add(reportPanel, "REPORTS");

        // 3. Add Navbar and pass this dashboard instance to it
        Navbar nav = new Navbar(parent, this);
        add(nav, BorderLayout.NORTH);
        add(contentArea, BorderLayout.CENTER);
    }

    // Method for the Navbar to call
    public void switchContent(String viewName) {
        cardLayout.show(contentArea, viewName);
    }

    // Method to open user detail panel for editing staff
    public void openUserDetail(User user) {
        // Create a new instance of the detail panel every time to clear/set data
        contentArea.add(new UserDetailPanel(this, user), "USER_DETAIL");
        cardLayout.show(contentArea, "USER_DETAIL");
    }

    public void refreshStaffList() {
        staffPanel.refreshTable();
    }

    public void showServiceManagement() {
        cardLayout.show(contentArea, "MANAGE_SERVICES");
    }

    public void showServiceDetail(models.Service service) {
        contentArea.add(new ServiceDetailPanel(this, service), "SERVICE_DETAIL");
        cardLayout.show(contentArea, "SERVICE_DETAIL");
    }
}