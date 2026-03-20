package views.Manager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BarChartPanel extends JPanel {
    private Map<String, Double> data;

    public void setData(Map<String, Double> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int margin = 30;
        int barWidth = (width - 2 * margin) / data.size() - 10;
        double maxVal = data.values().stream().max(Double::compare).orElse(1.0);

        int i = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            int barHeight = (int) ((entry.getValue() / maxVal) * (height - 2 * margin));
            int x = margin + i * (barWidth + 10);
            int y = height - margin - barHeight;

            g2.setColor(new Color(70, 130, 180)); // Steel Blue
            g2.fillRect(x, y, barWidth, barHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth, barHeight);
            
            // Labels
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString(entry.getKey(), x, height - 10);
            i++;
        }
    }
}