package views.Manager;

import controllers.ServiceController;
import java.awt.*;
import javax.swing.*;
import models.Service;

public class ServiceDetailPanel extends JPanel {
    private final ManagerDashboard parent;
    private final ServiceController controller;
    private final Service currentService;

    private final JTextField txtId, txtName, txtPrice;
    private final JComboBox<String> cbCategory;
    private final JTextArea txtDescription;

    public ServiceDetailPanel(ManagerDashboard parent, Service service) {
        this.parent = parent;
        this.controller = new ServiceController();
        this.currentService = service;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // --- Header ---
        JLabel lblTitle = new JLabel("Service Details", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        add(lblTitle, BorderLayout.NORTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        // ID (Disabled as it's auto-generated)
        g.gridx = 0; g.gridy = 0; formPanel.add(new JLabel("Service ID:"), g);
        txtId = new JTextField(20); txtId.setEditable(false);
        g.gridx = 1; formPanel.add(txtId, g);

        // Name
        g.gridx = 0; g.gridy = 1; formPanel.add(new JLabel("Service Name:"), g);
        txtName = new JTextField(20);
        g.gridx = 1; formPanel.add(txtName, g);

        // Category
        g.gridx = 0; g.gridy = 2; formPanel.add(new JLabel("Service Category:"), g);
        cbCategory = new JComboBox<>(new String[]{
            "Electronic Repair", "Hardware Replacement", "Software Setup", "Cleaning"
        });
        g.gridx = 1; formPanel.add(cbCategory, g);

        // Description
        g.gridx = 0; g.gridy = 3; formPanel.add(new JLabel("Description:"), g);
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        g.gridx = 1; formPanel.add(new JScrollPane(txtDescription), g);

        // Price
        g.gridx = 0; g.gridy = 4; formPanel.add(new JLabel("Price (MYR):"), g);
        txtPrice = new JTextField(10);
        g.gridx = 1; formPanel.add(txtPrice, g);

        add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        
        // Styling buttons
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnCancel.setPreferredSize(new Dimension(100, 35));

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        // --- Logic ---
        if (currentService != null) {
            txtId.setText(currentService.getId());
            txtName.setText(currentService.getName());
            cbCategory.setSelectedItem(currentService.getCategory());
            txtDescription.setText(currentService.getDescription());
            txtPrice.setText(String.valueOf(currentService.getPrice()));
        }

        btnSave.addActionListener(e -> saveAction());
        btnCancel.addActionListener(e -> parent.showServiceManagement()); // Navigate back
    }

    private void saveAction() {
        try {
            String name = txtName.getText().trim();
            String cat = cbCategory.getSelectedItem().toString();
            String desc = txtDescription.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());

            if (name.isEmpty()) throw new Exception("Name cannot be empty");

            controller.addOrUpdateService(txtId.getText(), name, cat, desc, price);
            JOptionPane.showMessageDialog(this, "Service Saved Successfully!");
            parent.showServiceManagement(); // Go back to list
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}