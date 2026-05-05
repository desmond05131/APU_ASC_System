package views;

import java.awt.*;
import javax.swing.*;
import models.User;
import services.FileHandler;
import utils.InputValidator;
import utils.PasswordHasher;

public class ManageProfilePanel extends JPanel {
    private final JTextField nameField, emailField, phoneField;
    private final JPasswordField currPassField, newPassField, confirmPassField;
    private final User user;

    public ManageProfilePanel(MainFrame parent) {
        this.user = parent.getCurrentUser();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Form area ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 10, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);

        // Read-only fields
        JTextField idField = new JTextField(user.getId(), 28);
        addRow(formPanel, gbc, "User ID :", idField, 0, false);

        JTextField roleField = new JTextField(user.getRole(), 28);
        addRow(formPanel, gbc, "Role:", roleField, 1, false);

        // Editable fields
        nameField = new JTextField(user.getName(), 28);
        addRow(formPanel, gbc, "Full Name :", nameField, 2, true);

        emailField = new JTextField(user.getEmail(), 28);
        addRow(formPanel, gbc, "Email Address:", emailField, 3, true);

        phoneField = new JTextField(user.getContactNumber(), 28);
        addRow(formPanel, gbc, "Contact Number :", phoneField, 4, true);

        // Password fields
        currPassField = new JPasswordField(28);
        addRow(formPanel, gbc, "Current Password:", currPassField, 5, true);

        newPassField = new JPasswordField(28);
        addRow(formPanel, gbc, "New Password:", newPassField, 6, true);

        confirmPassField = new JPasswordField(28);
        addRow(formPanel, gbc, "Confirm Password:", confirmPassField, 7, true);

        add(formPanel, BorderLayout.CENTER);

        // --- Button row ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 18));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 10, 60));

        JButton saveBtn   = styledButton("Save Changes");
        JButton passBtn   = styledButton("Change Password");
        JButton cancelBtn = styledButton("Cancel");

        saveBtn.addActionListener(e -> handleSaveChanges());
        passBtn.addActionListener(e -> handleChangePassword());

        btnPanel.add(saveBtn);
        btnPanel.add(passBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc,
                        String labelText, JTextField field, int row, boolean editable) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        field.setEditable(editable);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        if (!editable) {
            field.setBackground(new Color(238, 240, 244));
            field.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 222)));
        }
        panel.add(field, gbc);
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(100, 100, 248));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    private void handleSaveChanges() {
        if (!InputValidator.isNotEmpty(nameField.getText()) ||
            !InputValidator.isValidEmail(emailField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Name and Email.");
            return;
        }
        user.setName(nameField.getText());
        user.setEmail(emailField.getText());
        user.setContactNumber(phoneField.getText());

        FileHandler.updateLine("users.txt", user.getId(),
            String.join("|", user.getId(), user.getPassword(), user.getName(),
                        user.getRole(), user.getEmail(), user.getContactNumber()));

        JOptionPane.showMessageDialog(this, "Profile updated successfully!");
    }

    private void handleChangePassword() {
        String curr  = new String(currPassField.getPassword());
        String nPass = new String(newPassField.getPassword());
        String cPass = new String(confirmPassField.getPassword());

        if (!PasswordHasher.hashPassword(curr).equals(user.getPassword())) {
            JOptionPane.showMessageDialog(this, "Incorrect current password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nPass.length() < 6 || !nPass.equals(cPass)) {
            JOptionPane.showMessageDialog(this, "Passwords must match and be at least 6 characters.");
            return;
        }
        user.setPassword(PasswordHasher.hashPassword(nPass));
        handleSaveChanges();
    }
}
