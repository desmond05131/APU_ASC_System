package views.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class ServicePaymentHistoryPanel extends JPanel {

    private CustomerDashboard dashboard;
    private DefaultTableModel model;

    public ServicePaymentHistoryPanel(CustomerDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // =========================
        // HEADER
        // =========================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        headerPanel.setPreferredSize(new Dimension(0, 50));

        JLabel header = new JLabel("  Service & Payment History");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));

        headerPanel.add(header, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // =========================
        // TABLE
        // =========================
        String[] columns = {
                "Payment ID", "Service ID", "Service Type",
                "Date", "Amount", "Method", "Status"
        };

        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        // =========================
        // FOOTER
        // =========================
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(Color.WHITE);

        JButton btnBack = new JButton("← Back");
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> dashboard.switchContent("DASHBOARD"));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadPaymentData());

        footer.add(btnBack);
        footer.add(btnRefresh);

        add(footer, BorderLayout.SOUTH);

        // LOAD DATA
        loadPaymentData();
    }

    // =========================
    // LOAD DATA FROM FILE
    // =========================
    private void loadPaymentData() {
        String filePath = "data/servicepayment.txt";

        model.setRowCount(0); // clear table before reload

        File file = new File(filePath);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this,
                    "File not found:\n" + filePath,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");

                // Ensure correct format (7 columns)
                if (row.length == 7) {
                    model.addRow(row);
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}