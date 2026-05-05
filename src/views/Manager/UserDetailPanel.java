package views.Manager;

import controllers.StaffController;
import java.awt.*;
import javax.swing.*;
import models.User;
import utils.InputValidator;

public class UserDetailPanel extends JPanel {
    private JTextField      txtId, txtName, txtEmail, txtPhone;
    private JPasswordField  txtPass, txtConfirmPass;
    private JComboBox<String> roleCombo;
    private final User             existingUser;
    private final ManagerDashboard dashboard;
    private final StaffController  controller;

    public UserDetailPanel(ManagerDashboard dashboard, User user) {
        this.dashboard    = dashboard;
        this.existingUser = user;
        this.controller   = new StaffController();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildForm(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------------ Form
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(40, 60, 20, 60));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(14, 12, 14, 12);
        g.fill   = GridBagConstraints.HORIZONTAL;

        boolean isAdd = (existingUser == null);

        // ---- Left column ----
        txtId = new JTextField(isAdd ? "AUTO" : existingUser.getId(), 22);
        txtId.setEditable(false);
        txtId.setBackground(new Color(238, 240, 244));
        txtId.setForeground(new Color(140, 145, 160));
        txtId.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtId.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 222)));

        txtName        = editField(isAdd ? "" : existingUser.getName());
        txtEmail       = editField(isAdd ? "" : existingUser.getEmail());
        txtPhone       = editField(isAdd ? "" : existingUser.getContactNumber());
        txtPass        = passField();
        txtConfirmPass = passField();

        addLeftRow(form, g, "User ID :",         txtId,          0);
        addLeftRow(form, g, "Full Name :",        txtName,        1);
        addLeftRow(form, g, "Email Address:",     txtEmail,       2);
        addLeftRow(form, g, "Contact Number :",   txtPhone,       3);
        addLeftRow(form, g, "Password:",          txtPass,        4);
        addLeftRow(form, g, "Confirm Password:",  txtConfirmPass, 5);

        // ---- Right column — Role ----
        g.gridx = 2; g.gridy = 0; g.weightx = 0;
        g.fill = GridBagConstraints.NONE; g.anchor = GridBagConstraints.EAST;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(roleLabel, g);

        g.gridx = 3; g.weightx = 0.4;
        g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.WEST;
        roleCombo = new JComboBox<>(new String[]{"Manager", "Counter Staff", "Technician"});
        roleCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        if (existingUser != null) {
            String displayRole = "CounterStaff".equals(existingUser.getRole())
                    ? "Counter Staff" : existingUser.getRole();
            roleCombo.setSelectedItem(displayRole);
        }
        form.add(roleCombo, g);

        return form;
    }

    private void addLeftRow(JPanel panel, GridBagConstraints g,
                            String labelText, JComponent field, int row) {
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        g.fill = GridBagConstraints.NONE; g.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(lbl, g);

        g.gridx = 1; g.weightx = 0.5;
        g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.WEST;
        panel.add(field, g);
    }

    private JTextField editField(String text) {
        JTextField f = new JTextField(text, 22);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return f;
    }

    private JPasswordField passField() {
        JPasswordField f = new JPasswordField(22);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return f;
    }

    // ------------------------------------------------------------------ Footer
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 18));
        footer.setBackground(Color.WHITE);

        JButton btnSave   = styledButton("Save",   new Color(100, 100, 248));
        JButton btnCancel = styledButton("Cancel", new Color(100, 100, 248));

        btnSave.addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> dashboard.switchContent("MANAGE_STAFF"));

        footer.add(btnSave);
        footer.add(btnCancel);
        return footer;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ------------------------------------------------------------------ Logic
    private void handleSave() {
        String name  = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String pass  = new String(txtPass.getPassword());
        String conf  = new String(txtConfirmPass.getPassword());

        String selectedRole = (String) roleCombo.getSelectedItem();
        String role = "Counter Staff".equals(selectedRole) ? "CounterStaff" : selectedRole;

        if (!InputValidator.isNotEmpty(name) || !InputValidator.isNotEmpty(email)) {
            JOptionPane.showMessageDialog(this, "Full Name and Email Address are required.");
            return;
        }
        if (!InputValidator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }
        if (!pass.isEmpty() && !pass.equals(conf)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (existingUser == null && pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A password is required for new staff.");
            return;
        }

        if (existingUser == null) {
            controller.addStaff(name, email, phone, pass, role);
        } else {
            controller.updateStaff(existingUser.getId(), name, email, phone, role,
                                   pass.isEmpty() ? null : pass);
        }

        JOptionPane.showMessageDialog(this, "Staff saved successfully!");
        dashboard.refreshStaffList();
        dashboard.switchContent("MANAGE_STAFF");
    }
}
