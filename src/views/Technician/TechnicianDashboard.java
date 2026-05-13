package views.Technician;

import java.awt.*;
import javax.swing.*;
import views.Dashboard;
import views.MainFrame;
import views.ManageProfilePanel;
import views.components.Navbar;

public class TechnicianDashboard extends JPanel implements Dashboard {
    private final CardLayout   cardLayout;
    private final JPanel       contentArea;
    private final MainFrame    mainFrame;
    private final AssignAppointmentList appointmentList;
    private final ProvideFeedbackPanel  feedbackPanel;

    public TechnicianDashboard(MainFrame parent) {
        this.mainFrame = parent;
        setLayout(new BorderLayout());

        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);

        contentArea.add(new ManageProfilePanel(parent), "PROFILE");

        appointmentList = new AssignAppointmentList(this);
        contentArea.add(appointmentList, "APPOINTMENTS");

        feedbackPanel = new ProvideFeedbackPanel(this);
        contentArea.add(feedbackPanel, "FEEDBACK");

        String role = parent.getCurrentUser() != null
                ? parent.getCurrentUser().getRole() : "Technician";
        add(new Navbar(parent, this, role), BorderLayout.NORTH);
        add(contentArea, BorderLayout.CENTER);

        cardLayout.show(contentArea, "APPOINTMENTS");
    }

    @Override
    public void switchContent(String viewName) {
        if ("FEEDBACK".equals(viewName)) feedbackPanel.loadCompletedAppointments();
        cardLayout.show(contentArea, viewName);
    }

    public MainFrame getMainFrame() { return mainFrame; }
}
