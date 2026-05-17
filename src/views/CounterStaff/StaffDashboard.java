package views.CounterStaff;

import java.awt.*;
import javax.swing.*;
import models.Customer;
import views.Dashboard;
import views.MainFrame;
import views.ManageProfilePanel;
import views.components.Navbar;

public class StaffDashboard extends JPanel implements Dashboard {

    private final CardLayout   cardLayout;
    private final JPanel       contentArea;
    private final MainFrame    parentFrame;

    private final ManageCustomerPanel    customerPanel;
    private final ManageAppointmentPanel appointmentPanel;
    private final PaymentAndReceiptPanel paymentPanel;

    public StaffDashboard(MainFrame parent) {
        this.parentFrame = parent;
        setLayout(new BorderLayout());

        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);

        contentArea.add(new ManageProfilePanel(parent), "PROFILE");

        customerPanel    = new ManageCustomerPanel(this);
        appointmentPanel = new ManageAppointmentPanel(this);
        paymentPanel     = new PaymentAndReceiptPanel(this);

        contentArea.add(customerPanel,    "MANAGE_CUSTOMERS");
        contentArea.add(appointmentPanel, "MANAGE_APPOINTMENTS");
        contentArea.add(paymentPanel,     "PAYMENT");

        String role = parent.getCurrentUser() != null
                ? parent.getCurrentUser().getRole() : "CounterStaff";
        add(new Navbar(parent, this, role), BorderLayout.NORTH);
        add(contentArea, BorderLayout.CENTER);

        cardLayout.show(contentArea, "MANAGE_APPOINTMENTS");
    }

    // ── Dashboard interface ──────────────────────────────────────────────────

    @Override
    public void switchContent(String viewName) {
        switch (viewName) {
            case "MANAGE_CUSTOMERS"    -> customerPanel.refreshTable();
            case "MANAGE_APPOINTMENTS" -> appointmentPanel.refreshTable();
            case "PAYMENT"             -> paymentPanel.refreshAll();
        }
        cardLayout.show(contentArea, viewName);
    }

    // ── Cross-panel coordination ─────────────────────────────────────────────

    /** Open the customer add/edit panel in the CardLayout area. */
    public void openCustomerDetail(Customer customer) {
        contentArea.add(new CustomerDetailPanel(this, customer), "CUSTOMER_DETAIL");
        cardLayout.show(contentArea, "CUSTOMER_DETAIL");
    }

    /** Navigate to Payment panel pre-loaded with a specific appointment. */
    public void openPaymentForAppointment(String appointmentId) {
        paymentPanel.preloadAppointment(appointmentId);
        cardLayout.show(contentArea, "PAYMENT");
    }

    public void refreshCustomerList()    { customerPanel.refreshTable(); }
    public void refreshAppointmentList() { appointmentPanel.refreshTable(); }
    public MainFrame getParentFrame()    { return parentFrame; }
}
