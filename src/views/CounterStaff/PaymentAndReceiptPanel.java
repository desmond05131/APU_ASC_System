package views.CounterStaff;

import controllers.AppointmentController;
import controllers.CustomerController;
import controllers.PaymentController;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import models.Appointment;
import models.AppointmentStatus;
import models.Customer;
import models.Payment;
import services.FileHandler;
import services.ReceiptService;

/**
 * PaymentAndReceiptPanel — Counter Staff view.
 * Collects payment for completed appointments, generates receipts,
 * and lets staff re-view any past receipt by clicking the history table.
 *
 * OOP: Encapsulation, Separation of Concerns, Composition.
 */
public class PaymentAndReceiptPanel extends JPanel {

    private static final String PAYMENT_FILE = "payments.txt";

    private JTable            pendingTable;
    private DefaultTableModel pendingModel;
    private JTable            historyTable;
    private DefaultTableModel historyModel;

    private JTextArea receiptArea;
    private JLabel    lblApptId, lblCustomer, lblService, lblAmount, lblStatus;

    private final StaffDashboard dashboard;

    private static final String[] PENDING_COLS = {
        "Appt ID", "Service", "Scheduled Date", "Customer", "Amount (RM)"
    };
    private static final String[] HISTORY_COLS = {
        "Payment ID", "Appt ID", "Customer ID", "Amount (RM)", "Date Paid"
    };

    public PaymentAndReceiptPanel(StaffDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 14, 20));
        setBackground(Color.WHITE);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(480);
        split.setResizeWeight(0.55);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        refreshAll();
    }

    // ── Left panel ───────────────────────────────────────────────────────────

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel(new BorderLayout(0, 10));
        left.setBackground(Color.WHITE);

        // Completed (unpaid) appointments
        pendingModel = new DefaultTableModel(PENDING_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        pendingTable = new JTable(pendingModel);
        pendingTable.setRowHeight(26);
        pendingTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pendingTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        pendingTable.getTableHeader().setBackground(new Color(245, 247, 250));
        pendingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) previewSelected();
        });

        JPanel pendingWrapper = new JPanel(new BorderLayout(0, 4));
        pendingWrapper.setOpaque(false);
        pendingWrapper.setBorder(titledBorder("Completed Appointments — Ready to Pay"));
        pendingWrapper.add(new JScrollPane(pendingTable), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnRow.setOpaque(false);
        JButton btnPay     = styledBtn("Confirm Payment", new Color(100, 100, 248));
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnPay    .addActionListener(e -> onConfirmPayment());
        btnRefresh.addActionListener(e -> refreshAll());
        btnRow.add(btnPay);
        btnRow.add(btnRefresh);
        pendingWrapper.add(btnRow, BorderLayout.SOUTH);

        // Payment history table
        historyModel = new DefaultTableModel(HISTORY_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(historyModel);
        historyTable.setRowHeight(24);
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        historyTable.getTableHeader().setBackground(new Color(245, 247, 250));
        // Step 8.5 — clicking a history row re-renders the receipt
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadHistorySelection();
        });

        JPanel histWrapper = new JPanel(new BorderLayout());
        histWrapper.setOpaque(false);
        histWrapper.setBorder(titledBorder("Payment History"));
        histWrapper.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JPanel stack = new JPanel(new GridLayout(2, 1, 0, 8));
        stack.setOpaque(false);
        stack.add(pendingWrapper);
        stack.add(histWrapper);
        left.add(stack, BorderLayout.CENTER);
        return left;
    }

    // ── Right panel ──────────────────────────────────────────────────────────

    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout(0, 10));
        right.setBackground(Color.WHITE);
        right.setBorder(titledBorder("Appointment Details & Receipt"));

        JPanel details = new JPanel(new GridBagLayout());
        details.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(4, 8, 4, 8);
        g.anchor  = GridBagConstraints.WEST;

        lblApptId   = detailLabel();
        lblCustomer = detailLabel();
        lblService  = detailLabel();
        lblAmount   = detailLabel();
        lblStatus   = detailLabel();

        addDetailRow(details, g, 0, "Appointment ID:", lblApptId);
        addDetailRow(details, g, 1, "Customer:",       lblCustomer);
        addDetailRow(details, g, 2, "Service:",        lblService);
        addDetailRow(details, g, 3, "Amount (RM):",    lblAmount);
        addDetailRow(details, g, 4, "Status:",         lblStatus);

        receiptArea = new JTextArea(12, 28);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        receiptArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        receiptArea.setText("Select a completed appointment\nto preview the receipt.");

        JButton btnCopy = styledBtn("Print / Copy Receipt", new Color(100, 100, 248));
        btnCopy.addActionListener(e -> copyReceiptToClipboard());

        right.add(details,                    BorderLayout.NORTH);
        right.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        right.add(btnCopy,                    BorderLayout.SOUTH);
        return right;
    }

    // ── Data population ──────────────────────────────────────────────────────

    public void refreshAll() {
        loadPendingTable();
        loadHistoryTable();
    }

    private void loadPendingTable() {
        pendingModel.setRowCount(0);
        for (Appointment a : AppointmentController.getAllAppointments()) {
            if (a.getStatus() == AppointmentStatus.COMPLETED) {
                pendingModel.addRow(new Object[]{
                    a.getAppointmentId(), a.getServiceType(),
                    a.getScheduledDate(), getCustomerName(a.getCustomerId()),
                    String.format("%.2f", a.getTotalAmount())
                });
            }
        }
    }

    private void loadHistoryTable() {
        historyModel.setRowCount(0);
        for (String line : FileHandler.readData(PAYMENT_FILE)) {
            String[] p = line.split("\\|");
            if (p.length >= 5) {
                historyModel.addRow(new Object[]{
                    p[0], p[1], p[2],
                    String.format("%.2f", Double.parseDouble(p[3])),
                    p[4]
                });
            }
        }
    }

    /** Pre-select a specific appointment in the pending table (called from ManageAppointmentPanel). */
    public void preloadAppointment(String appointmentId) {
        refreshAll();
        for (int i = 0; i < pendingModel.getRowCount(); i++) {
            if (pendingModel.getValueAt(i, 0).toString().equals(appointmentId)) {
                pendingTable.setRowSelectionInterval(i, i);
                pendingTable.scrollRectToVisible(pendingTable.getCellRect(i, 0, true));
                break;
            }
        }
    }

    // ── Preview / confirm ────────────────────────────────────────────────────

    private void previewSelected() {
        int row = pendingTable.getSelectedRow();
        if (row == -1) return;

        String apptId   = pendingModel.getValueAt(row, 0).toString();
        String service  = pendingModel.getValueAt(row, 1).toString();
        String customer = pendingModel.getValueAt(row, 3).toString();
        String amount   = pendingModel.getValueAt(row, 4).toString();

        lblApptId  .setText(apptId);
        lblCustomer.setText(customer);
        lblService .setText(service);
        lblAmount  .setText("RM " + amount);
        lblStatus  .setText("COMPLETED — Ready to collect payment");
        lblStatus  .setForeground(new Color(100, 100, 248));

        double amt;
        try { amt = Double.parseDouble(amount); } catch (NumberFormatException ex) { amt = 0; }
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        receiptArea.setText(buildReceiptText("[PREVIEW]", apptId, customer, service, amt, now));
    }

    private void onConfirmPayment() {
        int row = pendingTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select an appointment to pay."); return; }

        String apptId    = pendingModel.getValueAt(row, 0).toString();
        String service   = pendingModel.getValueAt(row, 1).toString();
        String custName  = pendingModel.getValueAt(row, 3).toString();
        String amountStr = pendingModel.getValueAt(row, 4).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm payment of RM " + amountStr + " for appointment " + apptId + "?",
                "Confirm Payment", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        double amount   = Double.parseDouble(amountStr);
        String payId    = generatePaymentId();
        String custId   = getCustomerIdForAppointment(apptId);

        PaymentController.processPayment(payId, apptId, custId, amount);

        Payment payment = new Payment(payId, apptId, custId, amount);
        ReceiptService.generateAutomatedReceipt(payment, custName, service);

        String datePaid = payment.getPaymentDate();
        receiptArea.setText(buildReceiptText(payId, apptId, custName, service, amount, datePaid));
        lblStatus.setText("PAID ✓");
        lblStatus.setForeground(new Color(100, 100, 248));

        JOptionPane.showMessageDialog(this,
                "Payment processed!\nReceipt ID: " + payId
                + "\nReceipt file saved to data/receipts/");

        refreshAll();
        dashboard.refreshAppointmentList();
    }

    // ── History row selection (re-view past receipt) ──────────────────────────

    private void loadHistorySelection() {
        int row = historyTable.getSelectedRow();
        if (row == -1) return;

        String payId    = historyModel.getValueAt(row, 0).toString();
        String apptId   = historyModel.getValueAt(row, 1).toString();
        String custId   = historyModel.getValueAt(row, 2).toString();
        String amtStr   = historyModel.getValueAt(row, 3).toString();
        String datePaid = historyModel.getValueAt(row, 4).toString();

        String custName = getCustomerName(custId);
        String service  = AppointmentController.getAllAppointments().stream()
                .filter(a -> a.getAppointmentId().equals(apptId))
                .map(Appointment::getServiceType)
                .findFirst().orElse("—");

        lblApptId  .setText(apptId);
        lblCustomer.setText(custName);
        lblService .setText(service);
        lblAmount  .setText("RM " + amtStr);
        lblStatus  .setText("PAID ✓");
        lblStatus  .setForeground(new Color(100, 100, 248));

        double amt;
        try { amt = Double.parseDouble(amtStr); } catch (NumberFormatException ex) { amt = 0; }
        receiptArea.setText(buildReceiptText(payId, apptId, custName, service, amt, datePaid));
    }

    // ── Receipt text (delegates to ReceiptService for single source of truth) ──

    private String buildReceiptText(String payId, String apptId, String customer,
                                    String service, double amount, String date) {
        return ReceiptService.formatReceiptText(payId, apptId, customer, service, amount, date);
    }

    // ── Utilities ────────────────────────────────────────────────────────────

    private String generatePaymentId() {
        int max = 0;
        for (String line : FileHandler.readData(PAYMENT_FILE)) {
            try {
                String pid = line.split("\\|")[0]; // e.g. P001
                int n = Integer.parseInt(pid.substring(1)); // strip "P"
                if (n > max) max = n;
            } catch (Exception ignored) {}
        }
        return String.format("P%03d", max + 1);
    }

    private String getCustomerIdForAppointment(String apptId) {
        return AppointmentController.getAllAppointments().stream()
                .filter(a -> a.getAppointmentId().equals(apptId))
                .map(Appointment::getCustomerId)
                .findFirst().orElse("");
    }

    private String getCustomerName(String customerId) {
        Customer c = CustomerController.findCustomerById(customerId);
        return c != null ? c.getName() : customerId;
    }

    private void copyReceiptToClipboard() {
        String text = receiptArea.getText();
        if (text.isBlank()) { JOptionPane.showMessageDialog(this, "No receipt to copy."); return; }
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new java.awt.datatransfer.StringSelection(text), null);
        JOptionPane.showMessageDialog(this, "Receipt copied to clipboard!");
    }

    // ── Layout helpers ────────────────────────────────────────────────────────

    private JLabel detailLabel() {
        JLabel l = new JLabel("—");
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        return l;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints g,
                              int row, String labelText, JLabel value) {
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        panel.add(value, g);
    }

    private JButton styledBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private TitledBorder titledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 222)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13), new Color(60, 60, 100));
    }
}
