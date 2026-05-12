package views.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import services.FileHandler;

public final class ServicePaymentHistoryPanel extends JPanel {

    private final DefaultTableModel model;
    private final CustomerDashboard dashboard;

    public ServicePaymentHistoryPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        headerPanel.setPreferredSize(new Dimension(0, 50));

        JLabel header = new JLabel("  Service & Payment History");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(header, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Payment ID", "Service Type", "Service Date", "Amount", "Payment Date"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(Color.WHITE);

        JButton btnBack = new JButton("← Back");
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> dashboard.switchContent("DASHBOARD"));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadPaymentData());

        footer.add(btnBack);
        footer.add(btnRefresh);
        add(footer, BorderLayout.SOUTH);

        loadPaymentData();
    }

    private void loadPaymentData() {
        model.setRowCount(0);

        String customerId = dashboard.getMainFrame().getCurrentUser().getId();

        // Build appointment lookup keyed by appointmentId
        // appointments.txt: appointmentId|serviceType|status|scheduledDate|totalAmount|customerId|technicianId|staffId
        Map<String, String[]> appointmentMap = new HashMap<>();
        for (String line : FileHandler.readData("appointments.txt")) {
            String[] parts = line.split("\\|", 8);
            if (parts.length == 8) {
                appointmentMap.put(parts[0], parts);
            }
        }

        // Walk payments, filter by customerId, join with appointment data
        // payments.txt: paymentId|appointmentId|customerId|amount|paymentDate
        ArrayList<String> payments = FileHandler.readData("payments.txt");
        for (String line : payments) {
            String[] pay = line.split("\\|", 5);
            if (pay.length == 5 && pay[2].equals(customerId)) {
                String[] appt = appointmentMap.get(pay[1]);
                String serviceType  = (appt != null) ? appt[1] : "-";
                String serviceDate  = (appt != null) ? appt[3] : "-";

                model.addRow(new Object[]{
                        pay[0],
                        serviceType,
                        serviceDate,
                        "RM " + pay[3],
                        pay[4]
                });
            }
        }
    }
}
