package views.components;

import javax.swing.*;
import java.awt.*;

public class NavButton extends JButton {
    private boolean isActive = false;

    public NavButton(String text, String iconPath) {
        super(text);
        // Load and scale icon (assuming PNG for standard Swing, 
        // if using SVGs you'd need a library like FlatLaf)
        ImageIcon icon = new ImageIcon("assets/icons/" + iconPath);
        setIcon(new ImageIcon(icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        
        // Layout settings
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Arial", Font.PLAIN, 12));
    }

    public void setActive(boolean active) {
        this.isActive = active;
        setForeground(active ? new Color(102, 51, 204) : Color.DARK_GRAY);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isActive) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(102, 51, 204));
            // Draw the purple underline seen in your wireframe
            g2d.fillRect(0, getHeight() - 3, getWidth(), 3);
        }
    }
}