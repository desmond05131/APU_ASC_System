package views.CounterStaff;

import controllers.CustomerController;
import java.awt.*;
import javax.swing.*;
import models.Customer;
import utils.InputValidator;

/**
 * Add / Edit panel for a single Customer.
 * Patterned after views.Manager.UserDetailPanel but without a role dropdown
 * (all accounts created here have role = Customer).
 */
public class CustomerDetailPanel extends JPanel {

    private JTextField     txtName, txtEmail, txtPhone;
    private JPasswordField txtPass, txtConfirm;
    private final Customer       existingCustomer;
    private final StaffDashboard dashboard;

    public CustomerDetailPanel(StaffDashboard dashboard, Customer customer) {
        this.dashboard        = dashboard;
        this.existingCustomer = customer;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(buildForm(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        boolean isAdd = (customer == null);
        txtName .setText(isAdd ? "" : customer.getName());
        txtEmail.setText(isAdd ? "" : customer.getEmail());
        txtPhone.setText(isAdd ? "" : customer.getContactNumber());
    }

    // ------------------------------------------------------------------ Form
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(40, 60, 20, 60));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(14, 12, 14, 12);
        g.fill   = GridBagConstraints.HORIZONTAL;

        boolean isAdd = (existingCustomer == null);

        // ID row (read-only)
        JTextField txtId = new JTextField(isAdd ? "AUTO" : existingCustomer.getId(), 22);
        txtId.setEditable(false);
        txtId.setBackground(new Color(238, 240, 244));
        txtId.setForeground(new Color(140, 145, 160));
        txtId.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtId.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 222)));

        txtName    = field("");
        txtEmail   = field("");
        txtPhone   = field("");
        txtPass    = passField();
        txtConfirm = passField();

        addRow(form, g, "Customer ID :",    txtId,      0);
        addRow(form, g, "Full Name *:",     txtName,    1);
        addRow(form, g, "Email Address *:", txtEmail,   2);
        addRow(form, g, "Contact Number :", txtPhone,   3);
        addRow(form, g, "Password" + (isAdd ? " *:" : " :"), txtPass,    4);
        addRow(form, g, "Confirm Password:", txtConfirm, 5);

        if (!isAdd) {
            g.gridx = 0; g.gridy = 6; g.gridwidth = 2;
            JLabel hint = new JLabel("Leave password blank to keep the existing password.");
            hint.setForeground(Color.GRAY);
            hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
            form.add(hint, g);
            g.gridwidth = 1;
        }

        return form;
    }

    private void addRow(JPanel panel, GridBagConstraints g, String labelText,
                        JComponent field, int row) {
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        g.fill = GridBagConstraints.NONE; g.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(lbl, g);

        g.gridx = 1; g.weightx = 0.5;
        g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.WEST;
        panel.add(field, g);
    }

    private JTextField field(String text) {
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

        btnSave  .addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> dashboard.switchContent("MANAGE_CUSTOMERS"));

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
        String name    = txtName.getText().trim();
        String email   = txtEmail.getText().trim();
        String phone   = txtPhone.getText().trim();
        String pass    = new String(txtPass.getPassword());
        String confirm = new String(txtConfirm.getPassword());
        boolean isAdd  = (existingCustomer == null);

        if (!InputValidator.isNotEmpty(name)) {
            showMsg("Full Name is required."); return;
        }
        if (!InputValidator.isValidEmail(email)) {
            showMsg("Please enter a valid email address."); return;
        }
        if (!pass.isEmpty() && !pass.equals(confirm)) {
            showMsg("Passwords do not match."); return;
        }
        if (isAdd && pass.isEmpty()) {
            showMsg("A password is required for new customers."); return;
        }
        if (isAdd && pass.length() < 6) {
            showMsg("Password must be at least 6 characters."); return;
        }

        if (isAdd) {
            Customer created = CustomerController.addCustomer(name, email, phone, pass);
            if (created != null) {
                JOptionPane.showMessageDialog(this,
                        "Customer added successfully!\nID: " + created.getId());
            } else {
                showMsg("Failed to add customer. Please try again."); return;
            }
        } else {
            boolean ok = CustomerController.updateCustomer(
                    existingCustomer.getId(), name, email, phone,
                    pass.isEmpty() ? null : pass);
            if (!ok) { showMsg("Update failed."); return; }
            JOptionPane.showMessageDialog(this, "Customer updated successfully.");
        }

        dashboard.refreshCustomerList();
        dashboard.switchContent("MANAGE_CUSTOMERS");
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
