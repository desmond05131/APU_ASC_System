package views.CounterStaff;

import controllers.AppointmentController;
import controllers.CustomerController;
import controllers.ServiceController;
import controllers.StaffController;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Appointment;
import models.AppointmentStatus;
import models.Customer;
import models.Service;
import models.Technician;

/**
 * ManageAppointmentPanel — Counter Staff view.
 * View / filter all appointments, book new ones (with real service catalogue
 * and date-level technician availability), and hand off to Payment panel.
 *
 * OOP: Encapsulation, Abstraction, Composition.
 */
public class ManageAppointmentPanel extends JPanel {

    private JTable            apptTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbStatusFilter;
    private final StaffDashboard dashboard;

    private static final String[] COLUMNS = {
        "Appt ID", "Service Type", "Status", "Scheduled Date",
        "Amount (RM)", "Customer ID", "Technician ID"
    };

    public ManageAppointmentPanel(StaffDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 14, 20));
        setBackground(Color.WHITE);

        add(buildFilterSection(), BorderLayout.NORTH);
        add(buildTable(),         BorderLayout.CENTER);
        add(buildBottomPanel(),   BorderLayout.SOUTH);

        refreshTable();
    }

    // ── Filter section ───────────────────────────────────────────────────────

    private JPanel buildFilterSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);

        JLabel title = new JLabel("Filtering");
        title.setFont(new Font("SansSerif", Font.PLAIN, 13));
        title.setForeground(new Color(80, 80, 100));
        wrapper.add(title, BorderLayout.NORTH);

        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 222)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 14, 10, 14);
        g.fill   = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        box.add(label("Status :"), g);

        g.gridx = 1; g.weightx = 0.25;
        cmbStatusFilter = new JComboBox<>(
                new String[]{"All", "PENDING", "IN_PROGRESS", "COMPLETED", "PAID"});
        cmbStatusFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbStatusFilter.addActionListener(e -> refreshTable());
        box.add(cmbStatusFilter, g);

        g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        JButton refresh = styledBtn("Refresh", new Color(100, 100, 248));
        refresh.addActionListener(e -> refreshTable());
        box.add(refresh, g);

        wrapper.add(box, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Table ────────────────────────────────────────────────────────────────

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        apptTable = new JTable(tableModel);
        apptTable.setRowHeight(28);
        apptTable.setGridColor(new Color(220, 226, 234));
        apptTable.setShowGrid(true);
        apptTable.setSelectionBackground(new Color(220, 225, 255));
        apptTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        apptTable.getTableHeader().setBackground(new Color(245, 247, 250));
        apptTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        apptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(apptTable);
    }

    // ── Button row ───────────────────────────────────────────────────────────

    private JPanel buildBottomPanel() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        row.setOpaque(false);

        JButton btnNew = styledBtn("New Appointment", new Color(100, 100, 248));
        JButton btnPay = styledBtn("Collect Payment", new Color(100, 100, 248));
        btnNew.addActionListener(e -> openNewAppointmentDialog());
        btnPay.addActionListener(e -> onCollectPayment());

        row.add(btnNew);
        row.add(btnPay);
        return row;
    }

    // ── Table data ───────────────────────────────────────────────────────────

    public void refreshTable() {
        tableModel.setRowCount(0);
        String filter = (String) cmbStatusFilter.getSelectedItem();
        for (Appointment a : AppointmentController.getAllAppointments()) {
            if ("All".equals(filter) || a.getStatus().name().equals(filter)) {
                tableModel.addRow(new Object[]{
                    a.getAppointmentId(), a.getServiceType(),
                    a.getStatus().name(), a.getScheduledDate(),
                    String.format("%.2f", a.getTotalAmount()),
                    a.getCustomerId(), a.getTechnicianId()
                });
            }
        }
    }

    // ── Collect Payment ──────────────────────────────────────────────────────

    private void onCollectPayment() {
        int row = apptTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select an appointment first."); return; }
        String status = tableModel.getValueAt(row, 2).toString();
        if (!AppointmentStatus.COMPLETED.name().equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "Only COMPLETED appointments can be paid.\nCurrent status: " + status,
                    "Cannot Pay", JOptionPane.WARNING_MESSAGE);
            return;
        }
        dashboard.openPaymentForAppointment(tableModel.getValueAt(row, 0).toString());
    }

    // ── New Appointment Dialog ────────────────────────────────────────────────

    private void openNewAppointmentDialog() {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Create New Appointment", true);
        dialog.setSize(540, 410);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(7, 7, 7, 7);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        // ── Customer selector ────────────────────────────────────────────────
        ArrayList<Customer> customers = CustomerController.getAllCustomers();
        String[] custItems = customers.stream()
                .map(c -> c.getId() + " – " + c.getName())
                .toArray(String[]::new);
        JComboBox<String> cmbCustomer = new JComboBox<>(custItems);
        cmbCustomer.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // ── Service selector (real catalogue) ────────────────────────────────
        ArrayList<Service> services = new ServiceController().getAllServices();
        String[] svcItems = services.stream()
                .map(s -> {
                    int h = "Normal".equalsIgnoreCase(s.getCategory()) ? 1 : 3;
                    return String.format("%s — %s (%dh) — RM %.2f",
                            s.getName(), s.getCategory(), h, s.getPrice());
                })
                .toArray(String[]::new);
        JComboBox<String> cmbService = new JComboBox<>(svcItems);
        cmbService.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // ── Price (auto-filled from selected service) ─────────────────────────
        JTextField fPrice = new JTextField("0.00", 10);
        fPrice.setEditable(false);
        fPrice.setBackground(new Color(245, 245, 245));
        fPrice.setFont(new Font("SansSerif", Font.PLAIN, 13));

        Runnable updatePrice = () -> {
            int idx = cmbService.getSelectedIndex();
            if (idx >= 0 && idx < services.size())
                fPrice.setText(String.format("%.2f", services.get(idx).getPrice()));
        };
        cmbService.addActionListener(e -> updatePrice.run());
        updatePrice.run();

        // ── Date field (date-only yyyy-MM-dd) ─────────────────────────────────
        JTextField fDate = new JTextField(LocalDate.now().plusDays(1).toString(), 16);
        fDate.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JLabel lblDateHint = new JLabel("Format: yyyy-MM-dd");
        lblDateHint.setForeground(Color.GRAY);
        lblDateHint.setFont(new Font("SansSerif", Font.ITALIC, 10));

        // ── Technician selector (populated on demand) ─────────────────────────
        JComboBox<String> cmbTech = new JComboBox<>();
        cmbTech.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton btnLoadTech = styledBtn("Load Available Technicians", new Color(100, 100, 248));
        btnLoadTech.addActionListener(e -> {
            cmbTech.removeAllItems();
            ArrayList<Technician> available = getAvailableTechnicians(fDate.getText().trim());
            if (available.isEmpty()) {
                cmbTech.addItem("No technicians available on this date");
            } else {
                for (Technician t : available)
                    cmbTech.addItem(t.getId() + " – " + t.getName());
            }
        });

        // ── Layout rows ──────────────────────────────────────────────────────
        addFormRow(form, g, 0, "Customer *",   cmbCustomer);
        addFormRow(form, g, 1, "Service *",    cmbService);
        addFormRow(form, g, 2, "Price (RM)",   fPrice);
        addFormRow(form, g, 3, "Date *",       fDate);
        g.gridx = 1; g.gridy = 4; form.add(lblDateHint, g);
        addFormRow(form, g, 5, "",             btnLoadTech);
        addFormRow(form, g, 6, "Technician *", cmbTech);

        dialog.add(form, BorderLayout.CENTER);

        // ── Action buttons ───────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(Color.WHITE);
        JButton btnBook   = styledBtn("Book Appointment", new Color(100, 100, 248));
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnCancel.addActionListener(e -> dialog.dispose());

        btnBook.addActionListener(e -> {
            if (customers.isEmpty()) { showError(dialog, "No customers found. Add a customer first."); return; }
            String dateStr = fDate.getText().trim();
            if (!isValidDate(dateStr)) { showError(dialog, "Invalid date. Use format: yyyy-MM-dd"); return; }
            if (cmbTech.getItemCount() == 0
                    || cmbTech.getSelectedItem().toString().startsWith("No tech")) {
                showError(dialog, "No available technician selected."); return;
            }

            String customerId = ((String) cmbCustomer.getSelectedItem()).split(" – ")[0];
            int    svcIdx     = cmbService.getSelectedIndex();
            String svcType    = (svcIdx >= 0 && svcIdx < services.size())
                    ? services.get(svcIdx).getName() : "";
            String techId     = ((String) cmbTech.getSelectedItem()).split(" – ")[0];
            double price;
            try { price = Double.parseDouble(fPrice.getText()); }
            catch (NumberFormatException ex) { price = 0.0; }

            String apptId  = generateAppointmentId();
            String staffId = dashboard.getParentFrame().getCurrentUser().getId();

            AppointmentController.createAppointment(
                    new Appointment(apptId, svcType, AppointmentStatus.PENDING,
                                    dateStr, price, customerId, techId, staffId));

            JOptionPane.showMessageDialog(dialog,
                    "Appointment booked!\nID: " + apptId
                    + "\nTechnician: " + techId + "\nDate: " + dateStr);
            dialog.dispose();
            refreshTable();
        });

        btnRow.add(btnBook);
        btnRow.add(btnCancel);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ── Availability (date-level blocking) ───────────────────────────────────

    /**
     * A technician is unavailable on a date if they have any non-PAID appointment
     * on that exact calendar date.
     */
    private ArrayList<Technician> getAvailableTechnicians(String requestedDateStr) {
        if (!isValidDate(requestedDateStr)) return new ArrayList<>();
        LocalDate requested = LocalDate.parse(requestedDateStr);

        Set<String> busy = new HashSet<>();
        for (Appointment a : AppointmentController.getAllAppointments()) {
            if (a.getStatus() == AppointmentStatus.PAID) continue;
            try {
                String raw = a.getScheduledDate();
                LocalDate d = LocalDate.parse(raw.length() >= 10 ? raw.substring(0, 10) : raw);
                if (d.equals(requested)) busy.add(a.getTechnicianId());
            } catch (DateTimeParseException ignored) {}
        }

        return new StaffController().getStaffOnly().stream()
                .filter(u -> u instanceof Technician)
                .map(u -> (Technician) u)
                .filter(t -> !busy.contains(t.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ── ID generation ────────────────────────────────────────────────────────

    private String generateAppointmentId() {
        int max = 0;
        for (Appointment a : AppointmentController.getAllAppointments()) {
            try {
                int n = Integer.parseInt(a.getAppointmentId().substring(1)); // strip "A"
                if (n > max) max = n;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("A%03d", max + 1);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isValidDate(String s) {
        try { LocalDate.parse(s); return true; }
        catch (DateTimeParseException e) { return false; }
    }

    private void addFormRow(JPanel form, GridBagConstraints g, int row,
                            String labelText, JComponent field) {
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        form.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        form.add(field, g);
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

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    private void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
