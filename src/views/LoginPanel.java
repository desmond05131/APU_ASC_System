package views;

import controllers.AuthController;
import java.awt.*;
import javax.swing.*;
import models.*;
import utils.InputValidator;

public class LoginPanel extends JPanel {
    private final JTextField idField;
    private final JPasswordField passField;
    private final MainFrame parent;

    public LoginPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. Header Bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(176, 196, 218));
        header.setPreferredSize(new Dimension(0, 52));
        header.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        JLabel titleLabel = new JLabel("APU Automotive Service Centre");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(30, 30, 50));
        header.add(titleLabel, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // 2. Central Login Box
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.WHITE);

        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setPreferredSize(new Dimension(560, 300));
        loginBox.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        loginBox.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 20, 18, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Login ID row
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel loginLabel = new JLabel("Login :");
        loginLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginBox.add(loginLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        idField = new JTextField(18);
        idField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginBox.add(idField, gbc);

        // Password row
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password :");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginBox.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passField = new JPasswordField(18);
        passField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginBox.add(passField, gbc);

        // Login button row
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(110, 34));
        loginBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginBtn.setBackground(new Color(189, 195, 199));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(true);
        loginBtn.addActionListener(e -> handleLogin());
        loginBox.add(loginBtn, gbc);

        centerWrapper.add(loginBox);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String id = idField.getText();
        String pass = new String(passField.getPassword());

        if (!InputValidator.isNotEmpty(id) || !InputValidator.isNotEmpty(pass)) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] userData = AuthController.login(id, pass);

        if (userData != null && userData.length >= 6) {
            // indices: 0:id, 1:pass(hash), 2:name, 3:role, 4:email, 5:contact
            User user = switch (userData[3]) {
                case "Manager"      -> new Manager(userData[0], userData[2], userData[1], userData[4], userData[5]);
                case "Technician"   -> new Technician(userData[0], userData[2], userData[1], userData[4], userData[5]);
                case "CounterStaff" -> new CounterStaff(userData[0], userData[2], userData[1], userData[4], userData[5]);
                default             -> new Customer(userData[0], userData[2], userData[1], userData[4], userData[5]);
            };

            parent.setCurrentUser(user);
            JOptionPane.showMessageDialog(this, "Welcome, " + user.getName() + "!\n" + user.getDashboardAccess());
            parent.showView(user.getRole().toUpperCase());
        } else {
            JOptionPane.showMessageDialog(this, "Invalid ID or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
