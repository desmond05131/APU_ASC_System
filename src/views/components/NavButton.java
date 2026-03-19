package views.components;

import java.awt.*;
import javax.swing.*;

public class NavButton extends JButton {
    private boolean isActive = false;
    private final ImageIcon defaultIcon;
    private final ImageIcon activeIcon;

    public NavButton(String text, String defaultIconPath, String activeIconPath) {
        super(text);
        
        // Load and scale icons to 24x24
        this.defaultIcon = new ImageIcon(new ImageIcon("src/assets/icons/" + defaultIconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        this.activeIcon = new ImageIcon(new ImageIcon("src/assets/icons/" + activeIconPath)
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        setIcon(defaultIcon);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
        
        // Design styling to match wireframe
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Arial", Font.PLAIN, 12));
        setForeground(Color.DARK_GRAY);
    }

    public void setActive(boolean active) {
        this.isActive = active;
        setIcon(active ? activeIcon : defaultIcon);
        setForeground(active ? new Color(102, 51, 204) : Color.DARK_GRAY); // Purple text when active
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isActive) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(102, 51, 204));
            // Draws the purple underline from the wireframe
            g2d.fillRect(0, getHeight() - 3, getWidth(), 3);
        }
    }
}