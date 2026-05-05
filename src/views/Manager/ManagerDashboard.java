package views.Manager;

import java.awt.*;
import javax.swing.*;
import models.User;
import views.Dashboard;
import views.MainFrame;
import views.ManageProfilePanel;
import views.components.Navbar;

public class ManagerDashboard extends JPanel implements Dashboard {
    private final CardLayout cardLayout;
    private final JPanel contentArea;
    private final ManageStaffPanel staffPanel;
    private final ManageServicePanel servicePanel;
    private final AnalyzeReportPanel reportPanel;
    private final ReviewFeedbackPanel feedbackPanel;

    public ManagerDashboard(MainFrame parent) {
        setLayout(new BorderLayout());

        cardLayout   = new CardLayout();
        contentArea  = new JPanel(cardLayout);

        contentArea.add(new ManageProfilePanel(parent), "PROFILE");

        staffPanel = new ManageStaffPanel(this);
        contentArea.add(staffPanel, "MANAGE_STAFF");

        servicePanel = new ManageServicePanel(this);
        contentArea.add(servicePanel, "MANAGE_SERVICES");

        reportPanel = new AnalyzeReportPanel();
        contentArea.add(reportPanel, "REPORTS");

        feedbackPanel = new ReviewFeedbackPanel();
        contentArea.add(feedbackPanel, "REVIEW_FEEDBACK");

        String role = parent.getCurrentUser() != null ? parent.getCurrentUser().getRole() : "Manager";
        Navbar nav = new Navbar(parent, this, role);
        add(nav, BorderLayout.NORTH);
        add(contentArea, BorderLayout.CENTER);
    }

    @Override
    public void switchContent(String viewName) {
        cardLayout.show(contentArea, viewName);
    }

    public void openUserDetail(User user) {
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
