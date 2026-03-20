package views.Manager;

import controllers.StaffController;
import java.awt.*;
import javax.swing.*;
import models.User;
import utils.InputValidator;

public class UserDetailPanel extends JPanel {
    private final JTextField txtId, txtName, txtEmail, txtPhone;
    private final JPasswordField txtPass, txtNewPass, txtConfirmPass;
    private final JComboBox<String> roleCombo;
    private final User existingUser;
    private final ManagerDashboard dashboard;
    private final StaffController controller;

    public UserDetailPanel(ManagerDashboard dashboard, User user) {
        this.dashboard = dashboard;
        this.existingUser = user;
        this.controller = new StaffController();
        
        setLayout(new BorderLayout());
        setBackground(new Color(230, 240, 250)); // Light blue header area

        // Header
        JLabel header = new JLabel("  APU Automotive Service Centre", JLabel.LEFT);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(0, 60));
        add(header, BorderLayout.NORTH);

        // Main Form Area
        JPanel mainForm = new JPanel(new GridBagLayout());
        mainForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Left Column Labels
        String[] labels = {"User ID :", "Full Name :", "Email Address :", "Contact Number :", 
                           "Current Password :", "New Password :", "Confirm Password :"};
        
        // Initialize Fields
        txtId = new JTextField(user == null ? "AUTO" : user.getId(), 20);
        txtId.setEditable(false);
        txtName = new JTextField(user == null ? "" : user.getName(), 20);
        txtEmail = new JTextField(user == null ? "" : user.getEmail(), 20);
        txtPhone = new JTextField(user == null ? "" : user.getContactNumber(), 20);
        txtPass = new JPasswordField(20);
        txtNewPass = new JPasswordField(20);
        txtConfirmPass = new JPasswordField(20);

        JTextField[] fields = {txtId, txtName, txtEmail, txtPhone, txtPass, txtNewPass, txtConfirmPass};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            mainForm.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            mainForm.add(fields[i], gbc);
        }

        // Right Column: Role Selection (Matches your wireframe)
        gbc.gridx = 2; gbc.gridy = 0;
        mainForm.add(new JLabel("Role: "), gbc);
        gbc.gridx = 3;
        roleCombo = new JComboBox<>(new String[]{"Manager", "CounterStaff", "Technician"});
        if (user != null) roleCombo.setSelectedItem(user.getRole());
        mainForm.add(roleCombo, gbc);

        // Buttons (Matching wireframe colors)
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setBackground(Color.WHITE);
        
        JButton btnSave = new JButton("Save");
        btnSave.setBackground(new Color(102, 102, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> handleSave());

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBackground(new Color(102, 102, 255));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dashboard.switchContent("MANAGE_STAFF"));

        footer.add(btnSave);
        footer.add(btnCancel);

        add(mainForm, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void handleSave() {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String role = (String) roleCombo.getSelectedItem();
        String pass = new String(txtNewPass.getPassword());

        if (!InputValidator.isNotEmpty(name) || !InputValidator.isNotEmpty(email)) {
            JOptionPane.showMessageDialog(this, "Please fill in Name and Email.");
            return;
        }

        if (existingUser == null) {
            // Add Mode
            controller.addStaff(name, email, pass, role);
        } else {
            // Update Mode
            controller.updateStaff(existingUser.getId(), name, email, role);
        }

        JOptionPane.showMessageDialog(this, "Staff saved successfully!");
        dashboard.refreshStaffList();
        dashboard.switchContent("MANAGE_STAFF");
    }
}