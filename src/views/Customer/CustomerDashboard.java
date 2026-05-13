package views.Customer;

import java.awt.*;
import javax.swing.*;
import views.Dashboard;
import views.MainFrame;
import views.ManageProfilePanel;
import views.components.Navbar;

public final class CustomerDashboard extends JPanel implements Dashboard {
    private final CardLayout cardLayout;
    private final JPanel contentArea;
    private final FeedbackPanel feedbackPanel;
    private final AddFeedbackPanel addFeedbackPanel;
    private final MainFrame mainFrame;

    public CustomerDashboard(MainFrame parent) {
        this.mainFrame = parent;
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        contentArea.add(new CustomerDashboardPanel(this), "DASHBOARD");
        contentArea.add(new ManageProfilePanel(parent), "PROFILE");
        contentArea.add(new ServicePaymentHistoryPanel(this), "HISTORY");

        feedbackPanel = new FeedbackPanel(this);
        contentArea.add(feedbackPanel, "FEEDBACK");
        addFeedbackPanel = new AddFeedbackPanel(this);
        contentArea.add(addFeedbackPanel, "ADD_FEEDBACK");

        add(new Navbar(parent, this, "Customer"), BorderLayout.NORTH);
        add(contentArea, BorderLayout.CENTER);

        cardLayout.show(contentArea, "DASHBOARD");
    }

    @Override
    public void switchContent(String viewName) {
        if ("FEEDBACK".equals(viewName))     feedbackPanel.loadFeedback();
        if ("ADD_FEEDBACK".equals(viewName)) addFeedbackPanel.loadCompletedAppointments();
        cardLayout.show(contentArea, viewName);
    }

    public void openAddFeedback() {
        switchContent("ADD_FEEDBACK");
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }
}
