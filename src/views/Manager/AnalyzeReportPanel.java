package views.Manager;

import controllers.ReportController;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AnalyzeReportPanel extends JPanel {
    private final JTextField txtStart, txtEnd;
    private final JLabel lblRevenue, lblTotal, lblComp, lblCan;
    private final BarChartPanel chartPanel;
    private final JTable breakdownTable;
    private final DefaultTableModel tableModel;
    private final ReportController controller;

    public AnalyzeReportPanel() {
        controller = new ReportController();
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TITLE ---
        JLabel lblTitle = new JLabel("Analyze System Performance", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        add(lblTitle, BorderLayout.NORTH);

        // --- TOP: Summary Cards ---
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        lblRevenue = createStatCard("Total Revenue", "RM 0.00", summaryPanel);
        lblTotal = createStatCard("Total Appointments", "0", summaryPanel);
        lblComp = createStatCard("Completed", "0", summaryPanel);
        lblCan = createStatCard("Cancelled", "0", summaryPanel);

        // --- MIDDLE: Filters & Data ---
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        
        // Filter Bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtStart = new JTextField(10); txtEnd = new JTextField(10);
        JButton btnGen = new JButton("Generate Report");
        filterPanel.add(new JLabel("Start Date:")); filterPanel.add(txtStart);
        filterPanel.add(new JLabel("End Date:")); filterPanel.add(txtEnd);
        filterPanel.add(btnGen);

        // Chart and Table Split
        chartPanel = new BarChartPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("Monthly Revenue Analysis"));
        
        tableModel = new DefaultTableModel(new String[]{"Service", "Revenue", "Count"}, 0);
        breakdownTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(breakdownTable);
        scroll.setPreferredSize(new Dimension(300, 200));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chartPanel, scroll);
        splitPane.setDividerLocation(500);

        mainContent.add(filterPanel, BorderLayout.NORTH);
        mainContent.add(summaryPanel, BorderLayout.CENTER); // Summary cards inside main
        mainContent.add(splitPane, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);

        // --- BOTTOM: Actions ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Close");
        footer.add(new JButton("Export to Text"));
        footer.add(btnClose);
        add(footer, BorderLayout.SOUTH);

        // Events
        btnGen.addActionListener(e -> updateData());
    }

    private JLabel createStatCard(String title, String val, JPanel parent) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        p.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
        JLabel l = new JLabel(val, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(l, BorderLayout.CENTER);
        parent.add(p);
        return l;
    }

    private void updateData() {
        Map<String, Double> stats = controller.getSummaryStats(txtStart.getText(), txtEnd.getText());
        lblRevenue.setText(String.format("RM %.2f", stats.get("Revenue")));
        lblTotal.setText(String.valueOf(stats.get("Total").intValue()));
        lblComp.setText(String.valueOf(stats.get("Completed").intValue()));
        lblCan.setText(String.valueOf(stats.get("Cancelled").intValue()));

        chartPanel.setData(controller.getMonthlyRevenue());
    }
}