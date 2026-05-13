package views.Technician;

import controllers.AppointmentController;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Appointment;
import models.AppointmentStatus;

public class AssignAppointmentList extends JPanel {
    private JTable            table;
    private DefaultTableModel tableModel;
    private final TechnicianDashboard dashboard;

    public AssignAppointmentList(TechnicianDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 14, 20));
        setBackground(Color.WHITE);

        add(buildTable(),     BorderLayout.CENTER);
        add(buildButtonRow(), BorderLayout.SOUTH);

        refreshTable();
    }

    // ------------------------------------------------------------------ Table
    private JScrollPane buildTable() {
        String[] cols = {"Appointment ID", "Service Type", "Status",
                         "Scheduled Date", "Total Amount (RM)", "Customer ID"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setGridColor(new Color(220, 226, 234));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(220, 225, 255));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(245, 247, 250));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return new JScrollPane(table);
    }

    // ------------------------------------------------------------------ Buttons
    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        row.setOpaque(false);

        JButton btnRefresh  = styledButton("Refresh",          new Color(100, 100, 248));
        JButton btnComplete = styledButton("Mark Completed",    new Color(100, 100, 248));

        btnRefresh.addActionListener(e -> refreshTable());
        btnComplete.addActionListener(e -> handleMarkCompleted());

        row.add(btnRefresh);
        row.add(btnComplete);
        return row;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ------------------------------------------------------------------ Logic
    private void handleMarkCompleted() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.");
            return;
        }
        String apptId = tableModel.getValueAt(row, 0).toString();
        String status = tableModel.getValueAt(row, 2).toString();
        if ("COMPLETED".equals(status) || "PAID".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "Appointment " + apptId + " is already " + status + ".");
            return;
        }
        AppointmentController.updateStatus(apptId, AppointmentStatus.COMPLETED);
        JOptionPane.showMessageDialog(this,
                "Appointment " + apptId + " marked as Completed.");
        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        String techId = dashboard.getMainFrame().getCurrentUser().getId();
        for (Appointment appt : AppointmentController.getAllAppointments()) {
            // Appointment has no getTechnicianId() — extract from file-format string at index 6
            String[] parts = appt.toFileFormat().split("\\|");
            if (parts.length < 7 || !parts[6].equals(techId)) continue;
            tableModel.addRow(new Object[]{
                appt.getAppointmentId(),
                appt.getServiceType(),
                appt.getStatus().name(),
                appt.getScheduledDate(),
                String.format("%.2f", appt.getTotalAmount()),
                appt.getCustomerId()
            });
        }
    }
}
