package views.Manager;

import controllers.ReportController;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class AnalyzeReportPanel extends JPanel {

    private JTextField        txtStart, txtEnd, txtService, txtTechnician;
    private JComboBox<String> cmbCategory;
    private JLabel            lblTotal, lblRating, lblRevenue;
    private DefaultTableModel svcModel, techModel;
    private BarChartPanel     chartPanel;

    private final ReportController controller = new ReportController();

    public AnalyzeReportPanel() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 14, 20));
        setBackground(Color.WHITE);

        add(buildFilterSection(), BorderLayout.NORTH);
        add(buildCenterSection(), BorderLayout.CENTER);
        add(buildFooter(),        BorderLayout.SOUTH);

        generateReport();
    }

    // Filter section
    private JPanel buildFilterSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);

        JLabel title = new JLabel("Filtering");
        title.setFont(new Font("SansSerif", Font.PLAIN, 13));
        title.setForeground(new Color(80, 80, 100));
        wrapper.add(title, BorderLayout.NORTH);

        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 222)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 14, 10, 14);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Row 0 - Start Date | End Date | Service Name
        g.gridy = 0;

        g.gridx = 0; g.weightx = 0;     box.add(label("Start Date :"), g);
        g.gridx = 1; g.weightx = 0.22;  txtStart = field("yyyy-MM-dd"); box.add(txtStart, g);
        g.gridx = 2; g.weightx = 0;     box.add(label("End Date :"), g);
        g.gridx = 3; g.weightx = 0.22;  txtEnd = field("yyyy-MM-dd");   box.add(txtEnd, g);
        g.gridx = 4; g.weightx = 0;     box.add(label("Service Name :"), g);
        g.gridx = 5; g.weightx = 0.22;  txtService = field("");          box.add(txtService, g);

        // Row 1 - Technician | Category | Generate Report button
        g.gridy = 1;

        g.gridx = 0; g.weightx = 0;     box.add(label("Technician :"), g);
        g.gridx = 1; g.weightx = 0.22;  txtTechnician = field(""); box.add(txtTechnician, g);
        g.gridx = 2; g.weightx = 0;     box.add(label("Category :"), g);
        g.gridx = 3; g.weightx = 0.22;
        cmbCategory = new JComboBox<>(new String[]{"All", "Normal", "Major"});
        cmbCategory.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.add(cmbCategory, g);

        g.gridx = 4; g.gridwidth = 2; g.weightx = 0;
        g.fill = GridBagConstraints.NONE; g.anchor = GridBagConstraints.EAST;
        JButton btnGen = styledButton("Generate Report", new Color(100, 100, 248));
        btnGen.addActionListener(e -> generateReport());
        box.add(btnGen, g);
        g.gridwidth = 1; g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.CENTER;

        wrapper.add(box, BorderLayout.CENTER);
        return wrapper;
    }

    // Center (split)
    private JSplitPane buildCenterSection() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTablesPanel(), buildRightPanel());
        split.setResizeWeight(0.42);
        split.setBorder(null);
        split.setDividerSize(6);
        return split;
    }

    // Left pane - two stacked breakdown tables
    private JPanel buildTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 12));
        panel.setOpaque(false);

        svcModel = new DefaultTableModel(new String[]{"Revenue (RM)", "Service Name", "Count"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        panel.add(scrolled(makeTable(svcModel), "Service Breakdown"));

        techModel = new DefaultTableModel(new String[]{"Revenue (RM)", "Technician", "Count"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        panel.add(scrolled(makeTable(techModel), "Technician Breakdown"));

        return panel;
    }

    // Right pane - stat cards + bar chart
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statsRow.setOpaque(false);
        lblTotal   = statCard(statsRow, "Total Appointments", "0");
        lblRating  = statCard(statsRow, "Average Rating",     "0.0 / 5.0");
        lblRevenue = statCard(statsRow, "Total Revenue",      "RM 0.00");
        panel.add(statsRow, BorderLayout.NORTH);

        chartPanel = new BarChartPanel();
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 210, 222)),
                        "Monthly Revenue (RM)", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 12), new Color(60, 60, 100)),
                BorderFactory.createEmptyBorder(4, 6, 6, 6)));
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    // Footer
    private JPanel buildFooter() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        row.setOpaque(false);
        JButton btnExportCsv  = styledButton("Export to CSV",  new Color(100, 100, 248));
        JButton btnExportText = styledButton("Export to Text", new Color(100, 100, 248));
        btnExportCsv .addActionListener(e -> exportReportCsv());
        btnExportText.addActionListener(e -> exportReport());
        row.add(btnExportCsv);
        row.add(btnExportText);
        return row;
    }

    // Logic
    private void generateReport() {
        String start = txtStart      != null ? txtStart.getText().trim()      : "";
        String end   = txtEnd        != null ? txtEnd.getText().trim()        : "";
        String svc   = txtService    != null ? txtService.getText().trim()    : "";
        String tech  = txtTechnician != null ? txtTechnician.getText().trim() : "";
        String cat   = cmbCategory   != null ? (String) cmbCategory.getSelectedItem() : "All";

        Map<String, Double> stats = controller.getSummaryStats(start, end, svc, tech, cat);
        lblTotal.setText(String.valueOf(stats.get("Total").intValue()));
        lblRating.setText(String.format("%.1f / 5.0", controller.getAverageRating()));
        lblRevenue.setText(String.format("RM %.2f", stats.get("Revenue")));

        svcModel.setRowCount(0);
        for (String[] r : controller.getServiceBreakdown(start, end, svc, tech, cat))
            svcModel.addRow(new Object[]{r[0], r[1], r[2]});

        techModel.setRowCount(0);
        for (String[] r : controller.getTechnicianBreakdown(start, end, svc, tech, cat))
            techModel.addRow(new Object[]{r[0], r[1], r[2]});

        chartPanel.setData(controller.getMonthlyRevenue());
    }

    private void exportReport() {
        String start = txtStart      != null ? txtStart.getText().trim()      : "";
        String end   = txtEnd        != null ? txtEnd.getText().trim()        : "";
        String svc   = txtService    != null ? txtService.getText().trim()    : "";
        String tech  = txtTechnician != null ? txtTechnician.getText().trim() : "";
        String cat   = cmbCategory   != null ? (String) cmbCategory.getSelectedItem() : "All";
        try {
            String text = controller.buildReportText(start, end, svc, tech, cat);
            controller.exportToFile(text);
            JOptionPane.showMessageDialog(this,
                    "Report exported successfully to data/report_*.txt",
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Export failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReportCsv() {
        String start = txtStart      != null ? txtStart.getText().trim()      : "";
        String end   = txtEnd        != null ? txtEnd.getText().trim()        : "";
        String svc   = txtService    != null ? txtService.getText().trim()    : "";
        String tech  = txtTechnician != null ? txtTechnician.getText().trim() : "";
        String cat   = cmbCategory   != null ? (String) cmbCategory.getSelectedItem() : "All";
        try {
            String csv = controller.buildReportCsv(start, end, svc, tech, cat);
            controller.exportToCsvFile(csv);
            JOptionPane.showMessageDialog(this,
                    "CSV exported successfully to data/report_*.csv",
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Export failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helpers
    private JLabel statCard(JPanel parent, String title, String initial) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(new Color(245, 247, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 222)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JLabel lTitle = new JLabel(title, SwingConstants.CENTER);
        lTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lTitle.setForeground(new Color(80, 80, 100));
        card.add(lTitle, BorderLayout.NORTH);

        JLabel lVal = new JLabel(initial, SwingConstants.CENTER);
        lVal.setFont(new Font("SansSerif", Font.BOLD, 18));
        lVal.setForeground(new Color(60, 60, 100));
        card.add(lVal, BorderLayout.CENTER);

        parent.add(card);
        return lVal;
    }

    private JTable makeTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(28);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        t.getTableHeader().setBackground(new Color(245, 247, 250));
        t.setGridColor(new Color(220, 226, 234));
        t.setShowGrid(true);
        t.setSelectionBackground(new Color(220, 225, 255));
        return t;
    }

    private JScrollPane scrolled(JTable t, String title) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 222)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13), new Color(60, 60, 100)));
        return sp;
    }

    private JTextField field(String tooltip) {
        JTextField f = new JTextField(12);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        if (!tooltip.isEmpty()) f.setToolTipText(tooltip);
        return f;
    }

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
}
