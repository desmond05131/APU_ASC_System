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
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // --- Form Fields ---
        JTextField idField = new JTextField(user.getId(), 25);
        addLabelAndField("User ID :", idField, 0, false);
        JTextField roleField = new JTextField(user.getRole(), 25);
        addLabelAndField("System Role :", roleField, 1, false);
        addLabelAndField("Full Name :", nameField = new JTextField(user.getName(), 25), 2, true);
        addLabelAndField("Email Address :", emailField = new JTextField(user.getEmail(), 25), 3, true);
        addLabelAndField("Contact Number :", phoneField = new JTextField(user.getContactNumber(), 25), 4, true);
        
        addLabelAndField("Current Password :", currPassField = new JPasswordField(25), 5, true);
        addLabelAndField("New Password :", newPassField = new JPasswordField(25), 6, true);
        addLabelAndField("Confirm Password :", confirmPassField = new JPasswordField(25), 7, true);

        // --- Button Panel ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);
        
        JButton saveBtn = createStyledButton("Save Changes", new Color(102, 102, 255));
        JButton passBtn = createStyledButton("Change Password", new Color(102, 102, 255));
        JButton cancelBtn = createStyledButton("Cancel", new Color(102, 102, 255));

        saveBtn.addActionListener(e -> handleSaveChanges());
        passBtn.addActionListener(e -> handleChangePassword());

        btnPanel.add(saveBtn);
        btnPanel.add(passBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 1; gbc.gridy = 8;
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        add(btnPanel, gbc);
    }

    private void addLabelAndField(String labelText, JTextField field, int row, boolean editable) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        field.setEditable(editable);
        if (!editable) field.setBackground(new Color(245, 245, 245));
        add(field, gbc);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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

        // Update logic: Assuming a toFileFormat method exists in User model
        FileHandler.updateLine("users.txt", user.getId(), 
            String.join("|", user.getId(), user.getPassword(), user.getName(), 
                        user.getRole(), user.getEmail(), user.getContactNumber()));
        
        JOptionPane.showMessageDialog(this, "Profile updated successfully!");
    }

    private void handleChangePassword() {
        String curr = new String(currPassField.getPassword());
        String nPass = new String(newPassField.getPassword());
        String cPass = new String(confirmPassField.getPassword());

        // Verify current password (hashed check)
        if (!PasswordHasher.hashPassword(curr).equals(user.getPassword())) {
            JOptionPane.showMessageDialog(this, "Incorrect current password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nPass.length() < 6 || !nPass.equals(cPass)) {
            JOptionPane.showMessageDialog(this, "Passwords must match and be at least 6 characters.");
            return;
        }

        user.setPassword(PasswordHasher.hashPassword(nPass));
        handleSaveChanges(); // Save the new hashed password to the file
    }
}