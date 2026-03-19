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
        setBackground(new Color(240, 248, 255)); // Light blue background from wireframe

        // 1. Header (Top bar)
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(214, 234, 248));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        JLabel titleLabel = new JLabel("APU Automotive Service Centre");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(titleLabel);
        add(header, BorderLayout.NORTH);

        // 2. Central Login Box
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        
        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setPreferredSize(new Dimension(400, 300));
        loginBox.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        loginBox.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID Input
        gbc.gridx = 0; gbc.gridy = 0;
        loginBox.add(new JLabel("Login :"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(15);
        loginBox.add(idField, gbc);

        // Password Input
        gbc.gridx = 0; gbc.gridy = 1;
        loginBox.add(new JLabel("Password :"), gbc);
        gbc.gridx = 1;
        passField = new JPasswordField(15);
        loginBox.add(passField, gbc);

        // Login Button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(189, 195, 199));
        loginBtn.addActionListener(e -> handleLogin());
        loginBox.add(loginBtn, gbc);

        centerWrapper.add(loginBox);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String id = idField.getText();
        String pass = new String(passField.getPassword());

        // Error Avoidance: Validation 
        if (!InputValidator.isNotEmpty(id) || !InputValidator.isNotEmpty(pass)) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] userData = AuthController.login(id, pass);

        if (userData != null) {
            // Factory Logic: Instantiate the correct child class based on role 
            User user = switch (userData[3]) {
                case "Manager" -> new Manager(userData[0], userData[2], userData[1], "");
                case "Technician" -> new Technician(userData[0], userData[2], userData[1], "");
                case "CounterStaff" -> new CounterStaff(userData[0], userData[2], userData[1], "");
                default -> new Customer(userData[0], userData[2], userData[1], "");
            };

            parent.setCurrentUser(user);
            JOptionPane.showMessageDialog(this, "Welcome, " + user.getName() + "!\n" + user.getDashboardAccess());
            
            // Proceed to relevant dashboard card
            // parent.showView(user.getRole().toUpperCase()); 
        } else {
            JOptionPane.showMessageDialog(this, "Invalid ID or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}