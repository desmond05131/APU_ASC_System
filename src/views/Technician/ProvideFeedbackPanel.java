package views.Technician;

import controllers.AppointmentController;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.*;
import models.Appointment;
import models.AppointmentStatus;
import models.Feedback;
import services.FileHandler;

public class ProvideFeedbackPanel extends JPanel {
    private final TechnicianDashboard dashboard;
    private JComboBox<String> cmbAppointment;
    private JComboBox<String> cmbRating;
    private JTextArea         txtComment;
    // Parallel list of appointment IDs matching combobox entries
    private final ArrayList<String> apptIds = new ArrayList<>();

    public ProvideFeedbackPanel(TechnicianDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel header = new JLabel("  Provide Technical Feedback");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(0, 50));
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        form.setBackground(Color.WHITE);

        form.add(new JLabel("Appointment:"));
        cmbAppointment = new JComboBox<>();
        form.add(cmbAppointment);

        form.add(new JLabel("Rating:"));
        cmbRating = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        form.add(cmbRating);

        form.add(new JLabel("Technical Comment:"));
        txtComment = new JTextArea(5, 20);
        txtComment.setLineWrap(true);
        txtComment.setWrapStyleWord(true);
        form.add(new JScrollPane(txtComment));

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnPanel.setBackground(Color.WHITE);
        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");
        submitBtn.addActionListener(e -> handleSubmit());
        cancelBtn.addActionListener(e -> dashboard.switchContent("APPOINTMENTS"));
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        loadCompletedAppointments();
    }

    // Called by TechnicianDashboard.switchContent("FEEDBACK") to refresh the list
    public void loadCompletedAppointments() {
        cmbAppointment.removeAllItems();
        apptIds.clear();
        String techId = dashboard.getMainFrame().getCurrentUser().getId();
        for (Appointment appt : AppointmentController.getAllAppointments()) {
            // Appointment has no getTechnicianId() — extract from file-format string at index 6
            String[] parts = appt.toFileFormat().split("\\|");
            if (parts.length < 7 || !parts[6].equals(techId)) continue;
            if (appt.getStatus() != AppointmentStatus.COMPLETED) continue;
            cmbAppointment.addItem(appt.getAppointmentId()
                    + " — " + appt.getServiceType()
                    + " (" + appt.getScheduledDate() + ")"
                    + " — " + appt.getCustomerId());
            apptIds.add(appt.getAppointmentId());
        }
    }

    private void handleSubmit() {
        if (cmbAppointment.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No completed appointments available to provide feedback for.");
            return;
        }
        String comment = txtComment.getText().trim();
        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a technical comment.");
            return;
        }

        int idx = cmbAppointment.getSelectedIndex();
        if (idx < 0 || idx >= apptIds.size()) return;
        String apptId = apptIds.get(idx);

        // Look up serviceType from the selected appointment
        String serviceName = "";
        for (Appointment appt : AppointmentController.getAllAppointments()) {
            if (appt.getAppointmentId().equals(apptId)) {
                serviceName = appt.getServiceType();
                break;
            }
        }

        // Generate next feedback ID — find max numeric suffix after "F"
        int maxId = 0;
        for (String line : FileHandler.readData("feedback.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length > 0 && p[0].length() > 1 && p[0].startsWith("F")) {
                try { maxId = Math.max(maxId, Integer.parseInt(p[0].substring(1))); }
                catch (NumberFormatException ignored) {}
            }
        }
        String newId = String.format("F%03d", maxId + 1);

        int rating = Integer.parseInt((String) cmbRating.getSelectedItem());
        // customerName field stores the feedback author; here it is the technician.
        // Manager's Review Feedback panel labels this "Customer Name" — known semantic compromise.
        String authorName = dashboard.getMainFrame().getCurrentUser().getName();
        String date = LocalDate.now().toString();

        Feedback fb = new Feedback(newId, rating, authorName, comment, date);
        fb.setServiceName(serviceName);
        fb.setCategory("Technician"); // Allows Manager to filter technician feedback distinctly

        FileHandler.writeData("feedback.txt", fb.toString());

        JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
        txtComment.setText("");
        dashboard.switchContent("APPOINTMENTS");
    }
}
