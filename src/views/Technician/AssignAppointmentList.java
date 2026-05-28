package views.Technician;

import controllers.AppointmentController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Appointment;
import models.AppointmentStatus;
import services.FileHandler;

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

    // Table
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

    // Buttons
    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        row.setOpaque(false);

        JButton btnRefresh  = styledButton("Refresh",        new Color(100, 100, 248));
        JButton btnComments = styledButton("View Comments",  new Color(100, 100, 248));
        JButton btnComplete = styledButton("Mark Completed", new Color(100, 100, 248));

        btnRefresh .addActionListener(e -> refreshTable());
        btnComments.addActionListener(e -> handleViewComments());
        btnComplete.addActionListener(e -> handleMarkCompleted());

        row.add(btnRefresh);
        row.add(btnComments);
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

    // Logic
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

    private void handleViewComments() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.");
            return;
        }
        String customerId = tableModel.getValueAt(row, 5).toString();

        StringBuilder sb = new StringBuilder();
        for (String line : FileHandler.readData("feedback.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            // New format: feedbackId|customerId|rating|name|comment|date|svcName|category|status
            if (p.length < 9) continue;
            if (isNumeric(p[1])) continue;          // skip legacy 8-field rows
            if (!"ACTIVE".equals(p[8])) continue;   // skip deleted
            if (!p[1].equals(customerId)) continue;  // filter by appointment's customer
            if (!p[7].startsWith("Customer-")) continue; // customer-authored only

            String subject = p[7].replace("Customer-", "");
            int rating = 0;
            try { rating = Integer.parseInt(p[2].trim()); } catch (NumberFormatException ignored) {}

            sb.append("[").append(subject).append(" — ").append(p[5])
              .append(" — ").append(rating).append("★]\n")
              .append(p[4]).append("\n\n");
        }

        String msg = sb.isEmpty()
                ? "No customer comments for this appointment yet."
                : sb.toString().trim();

        JTextArea area = new JTextArea(msg, 10, 40);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Customer Comments — " + customerId, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(0, 8));
        dialog.add(new JScrollPane(area), BorderLayout.CENTER);
        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        foot.add(close);
        dialog.add(foot, BorderLayout.SOUTH);
        dialog.setSize(520, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        String techId = dashboard.getMainFrame().getCurrentUser().getId();
        for (Appointment appt : AppointmentController.getAllAppointments()) {
            if (!appt.getTechnicianId().equals(techId)) continue;
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

    private static boolean isNumeric(String s) {
        try { Integer.parseInt(s.trim()); return true; }
        catch (NumberFormatException e) { return false; }
    }
}
