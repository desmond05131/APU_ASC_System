package views.Customer;

import controllers.AppointmentController;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.*;
import models.Appointment;
import models.AppointmentStatus;
import models.Feedback;
import services.FileHandler;

public class AddFeedbackPanel extends JPanel {

    private final CustomerDashboard dashboard;
    private JComboBox<String> cmbAppointment;
    private JComboBox<String> cmbSubject;
    private JComboBox<String> ratingBox;
    private JTextArea txtComment;
    private final ArrayList<String> apptIds = new ArrayList<>();

    // Maps UI subject label → category value stored in feedback.txt
    private static final String[] SUBJECT_LABELS    = {"Counter Staff", "Technician", "Service Overall"};
    private static final String[] SUBJECT_CATEGORIES = {"Customer-CounterStaff", "Customer-Technician", "Customer-Overall"};

    public AddFeedbackPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel header = new JLabel("  Add Feedback");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(0, 50));
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        form.setBackground(Color.WHITE);

        form.add(new JLabel("Appointment:"));
        cmbAppointment = new JComboBox<>();
        form.add(cmbAppointment);

        form.add(new JLabel("Feedback Subject:"));
        cmbSubject = new JComboBox<>(SUBJECT_LABELS);
        form.add(cmbSubject);

        form.add(new JLabel("Rating:"));
        ratingBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        form.add(ratingBox);

        form.add(new JLabel("Comment:"));
        txtComment = new JTextArea(3, 20);
        txtComment.setLineWrap(true);
        txtComment.setWrapStyleWord(true);
        form.add(new JScrollPane(txtComment));

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnPanel.setBackground(Color.WHITE);
        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");
        submitBtn.addActionListener(e -> saveFeedback());
        cancelBtn.addActionListener(e -> dashboard.switchContent("FEEDBACK"));
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    /** Refreshed by CustomerDashboard.switchContent("ADD_FEEDBACK"). */
    public void loadCompletedAppointments() {
        cmbAppointment.removeAllItems();
        apptIds.clear();
        String customerId = dashboard.getMainFrame().getCurrentUser().getId();
        for (Appointment appt : AppointmentController.getAllAppointments()) {
            if (!appt.getCustomerId().equals(customerId)) continue;
            if (appt.getStatus() != AppointmentStatus.COMPLETED
                    && appt.getStatus() != AppointmentStatus.PAID) continue;
            cmbAppointment.addItem(appt.getAppointmentId()
                    + " — " + appt.getServiceType()
                    + " (" + appt.getScheduledDate() + ")");
            apptIds.add(appt.getAppointmentId());
        }
    }

    private void saveFeedback() {
        if (cmbAppointment.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No completed appointments available to give feedback for.");
            return;
        }
        String comment = txtComment.getText().trim();
        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a comment!");
            return;
        }

        int idx = cmbAppointment.getSelectedIndex();
        if (idx < 0 || idx >= apptIds.size()) return;
        String apptId = apptIds.get(idx);

        String serviceName = "";
        for (Appointment appt : AppointmentController.getAllAppointments()) {
            if (appt.getAppointmentId().equals(apptId)) {
                serviceName = appt.getServiceType();
                break;
            }
        }

        int max = 0;
        for (String line : FileHandler.readData("feedback.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length > 0 && p[0].startsWith("F")) {
                try { max = Math.max(max, Integer.parseInt(p[0].substring(1))); }
                catch (NumberFormatException ignored) {}
            }
        }
        String newId = String.format("F%03d", max + 1);

        int    rating       = Integer.parseInt((String) ratingBox.getSelectedItem());
        String customerId   = dashboard.getMainFrame().getCurrentUser().getId();
        String customerName = dashboard.getMainFrame().getCurrentUser().getName();
        String date         = LocalDate.now().toString();
        String category     = SUBJECT_CATEGORIES[cmbSubject.getSelectedIndex()];

        Feedback fb = new Feedback(newId, customerId, rating, customerName, comment, date);
        fb.setServiceName(serviceName);
        fb.setCategory(category);
        FileHandler.writeData("feedback.txt", fb.toString());

        JOptionPane.showMessageDialog(this, "Feedback submitted!");
        txtComment.setText("");
        dashboard.switchContent("FEEDBACK");
    }
}
