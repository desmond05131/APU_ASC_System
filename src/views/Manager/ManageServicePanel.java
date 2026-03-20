package views.Manager;

import controllers.ServiceController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Service;

public class ManageServicePanel extends JPanel {
    private ServiceController controller;
    private JTable table;
    private final DefaultTableModel tableModel;
    private JTextField txtName, txtPrice, txtSearch;
    private JComboBox<String> cbCategory;
    private String selectedId = null;

    public ManageServicePanel() {
        controller = new ServiceController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TOP PANEL: Title & Search ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Manage Services");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Search");
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        
        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL: Table ---
        String[] columns = {"Service ID", "Service Name", "Category", "Price (MYR)"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- SOUTH PANEL: Form & Buttons ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form Fields
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Service Name:"), gbc);
        gbc.gridx = 1; txtName = new JTextField(20); formPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Category:"), gbc);
        cbCategory = new JComboBox<>(new String[]{"Electronic Repair", "Hardware Replacement", "Software Setup", "Cleaning"});
        gbc.gridx = 1; formPanel.add(cbCategory, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Price:"), gbc);
        txtPrice = new JTextField(10); gbc.gridx = 1; formPanel.add(txtPrice, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAdd = new JButton("Add Service");
        JButton btnUpdate = new JButton("Update Service");
        JButton btnDelete = new JButton("Delete Service");
        JButton btnClear = new JButton("Clear");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.CENTER);
        southContainer.add(btnPanel, BorderLayout.SOUTH);
        add(southContainer, BorderLayout.SOUTH);

        // --- Event Listeners ---
        refreshTable(controller.getAllServices());

        btnAdd.addActionListener(e -> {
            try {
                controller.addService(txtName.getText(), cbCategory.getSelectedItem().toString(), Double.parseDouble(txtPrice.getText()));
                refreshTable(controller.getAllServices());
                clearFields();
            } catch (NumberFormatException | NullPointerException ex) { JOptionPane.showMessageDialog(this, "Invalid Input!"); }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedId != null) {
                controller.updateService(selectedId, txtName.getText(), cbCategory.getSelectedItem().toString(), Double.parseDouble(txtPrice.getText()));
                refreshTable(controller.getAllServices());
                clearFields();
            } else { JOptionPane.showMessageDialog(this, "Select a service to update."); }
        });

        btnDelete.addActionListener(e -> {
            if (selectedId != null) {
                controller.deleteService(selectedId);
                refreshTable(controller.getAllServices());
                clearFields();
            }
        });

        btnClear.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> refreshTable(controller.searchServices(txtSearch.getText())));

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = table.getValueAt(row, 0).toString();
                txtName.setText(table.getValueAt(row, 1).toString());
                cbCategory.setSelectedItem(table.getValueAt(row, 2).toString());
                txtPrice.setText(table.getValueAt(row, 3).toString());
            }
        });
    }

    private void refreshTable(ArrayList<Service> services) {
        tableModel.setRowCount(0);
        for (Service s : services) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getCategory(), String.format("%.2f", s.getPrice())});
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtPrice.setText("");
        cbCategory.setSelectedIndex(0);
        selectedId = null;
        table.clearSelection();
    }
}