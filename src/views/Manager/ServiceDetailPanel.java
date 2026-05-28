package views.Manager;

import controllers.ServiceController;
import java.awt.*;
import javax.swing.*;
import models.Service;

public class ServiceDetailPanel extends JPanel {
    private final ManagerDashboard  parent;
    private final ServiceController controller;
    private final Service           currentService; // null = add mode

    private JTextField    txtId, txtName, txtPrice;
    private JComboBox<String> cbCategory;
    private JTextArea     txtDescription;

    public ServiceDetailPanel(ManagerDashboard parent, Service service) {
        this.parent         = parent;
        this.controller     = new ServiceController();
        this.currentService = service;

        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 14, 30));
        setBackground(Color.WHITE);

        add(buildForm(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        if (currentService != null) populateFields();
    }

    // Form
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(10, 14, 10, 14);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.anchor  = GridBagConstraints.NORTHWEST;

        // Row 0: Service ID (left) | Category (right)
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0;
        form.add(label("Service ID :"), g);

        g.gridx = 1; g.weightx = 0.4;
        txtId = new JTextField(20);
        txtId.setEditable(false);
        txtId.setText(currentService == null ? "(auto)" : currentService.getId());
        txtId.setBackground(new Color(238, 240, 244));
        txtId.setFont(new Font("SansSerif", Font.PLAIN, 13));
        form.add(txtId, g);

        g.gridx = 2; g.weightx = 0;
        form.add(label("Category :"), g);

        g.gridx = 3; g.weightx = 0.4;
        cbCategory = new JComboBox<>(new String[]{"Normal", "Major"});
        cbCategory.setFont(new Font("SansSerif", Font.PLAIN, 13));
        form.add(cbCategory, g);

        // Row 1: Service Name (left) | Category note (right, below combo)
        g.gridy = 1;
        g.gridx = 0; g.weightx = 0;
        form.add(label("Service Name :"), g);

        g.gridx = 1; g.weightx = 0.4;
        txtName = new JTextField(20);
        txtName.setFont(new Font("SansSerif", Font.PLAIN, 13));
        form.add(txtName, g);

        g.gridx = 3; g.weightx = 0.4; g.insets = new Insets(0, 14, 10, 14);
        JLabel noteLabel = new JLabel("Normal = 1 hour service.  Major = 3 hour service.");
        noteLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        noteLabel.setForeground(new Color(100, 110, 130));
        form.add(noteLabel, g);
        g.insets = new Insets(10, 14, 10, 14);

        // Row 2: Price (left)
        g.gridy = 2;
        g.gridx = 0; g.weightx = 0;
        form.add(label("Price (RM) :"), g);

        g.gridx = 1; g.weightx = 0.4;
        txtPrice = new JTextField(10);
        txtPrice.setFont(new Font("SansSerif", Font.PLAIN, 13));
        form.add(txtPrice, g);

        // Row 3: Description (left, tall - spans 3 rows)
        g.gridy = 3;
        g.gridx = 0; g.weightx = 0; g.anchor = GridBagConstraints.NORTHWEST;
        form.add(label("Description :"), g);

        g.gridx = 1; g.weightx = 0.4; g.weighty = 1.0;
        g.fill = GridBagConstraints.BOTH; g.gridheight = 3;
        txtDescription = new JTextArea(7, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 222)));
        form.add(descScroll, g);
        g.gridheight = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.weighty = 0;    g.anchor = GridBagConstraints.NORTHWEST;

        return form;
    }

    // Footer
    private JPanel buildFooter() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        row.setOpaque(false);

        JButton btnSave   = styledButton("Save",   new Color(100, 100, 248));
        JButton btnCancel = styledButton("Cancel", new Color(100, 100, 248));

        btnSave.addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> parent.showServiceManagement());

        row.add(btnSave);
        row.add(btnCancel);
        return row;
    }

    // Logic
    private void populateFields() {
        txtName.setText(currentService.getName());
        txtPrice.setText(String.format("%.2f", currentService.getPrice()));
        txtDescription.setText(currentService.getDescription());
        cbCategory.setSelectedItem(currentService.getCategory());
    }

    private void handleSave() {
        String name  = txtName.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String cat   = (String) cbCategory.getSelectedItem();
        String desc  = txtDescription.getText().trim();

        if (name.isEmpty()) {
            showError("Service name cannot be empty.");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Price must be a valid non-negative number.");
            return;
        }

        String id = (currentService == null) ? null : currentService.getId();
        controller.addOrUpdateService(id, name, cat, desc, price);

        JOptionPane.showMessageDialog(this,
                currentService == null ? "Service added successfully." : "Service updated successfully.");
        parent.showServiceManagement();
    }

    // Helpers
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
