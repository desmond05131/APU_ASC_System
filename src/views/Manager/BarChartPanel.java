package views.Manager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BarChartPanel extends JPanel {
    private Map<String, Double> data;

    public BarChartPanel() {
        setBackground(Color.WHITE);
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (data == null || data.isEmpty()) {
            g2.setColor(new Color(150, 150, 170));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            String msg = "No data available";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
            return;
        }

        int mL = 58, mB = 36, mT = 26, mR = 16;
        int chartW = w - mL - mR;
        int chartH = h - mT - mB;

        double maxVal = data.values().stream().max(Double::compare).orElse(1.0);
        if (maxVal == 0) maxVal = 1.0;

        // Y-axis gridlines + labels (5 levels)
        int numLines = 5;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        for (int i = 0; i <= numLines; i++) {
            double val = maxVal * i / numLines;
            int    y   = mT + chartH - (int)(chartH * i / numLines);
            g2.setColor(new Color(220, 220, 232));
            g2.drawLine(mL, y, mL + chartW, y);
            g2.setColor(new Color(100, 100, 110));
            String lbl = String.format("%.0f", val);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, mL - fm.stringWidth(lbl) - 5, y + fm.getAscent() / 2 - 1);
        }

        // X-axis line
        g2.setColor(new Color(180, 180, 200));
        g2.drawLine(mL, mT + chartH, mL + chartW, mT + chartH);

        // Bars
        int n      = data.size();
        int gap    = 12;
        int barW   = Math.max(6, (chartW - gap * (n + 1)) / n);

        int i = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double val  = entry.getValue();
            int    barH = (int)(chartH * val / maxVal);
            int    x    = mL + gap + i * (barW + gap);
            int    y    = mT + chartH - barH;

            // Purple bar - rounded top only
            g2.setColor(new Color(100, 100, 248));
            if (barH > 0) {
                g2.fillRoundRect(x, y, barW, barH, 8, 8);
                if (barH > 8) g2.fillRect(x, y + 8, barW, barH - 8); // square bottom
            }

            // Value label above bar
            g2.setColor(new Color(60, 60, 80));
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            String valStr = String.format("%.0f", val);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(valStr, x + (barW - fm.stringWidth(valStr)) / 2, Math.max(mT + fm.getAscent(), y - 3));

            // X-axis label
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(80, 80, 90));
            fm = g2.getFontMetrics();
            String key = entry.getKey();
            g2.drawString(key, x + (barW - fm.stringWidth(key)) / 2, mT + chartH + 16);

            i++;
        }
    }
}
