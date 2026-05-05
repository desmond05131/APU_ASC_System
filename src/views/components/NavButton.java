package views.components;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class NavButton extends JButton {
    private boolean isActive = false;
    private final ImageIcon defaultIcon;
    private final ImageIcon activeIcon;

    public NavButton(String text, String defaultIconPath, String activeIconPath) {
        super(text);
        this.defaultIcon  = loadIcon(defaultIconPath);
        this.activeIcon   = loadIcon(activeIconPath);

        setIcon(defaultIcon);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("SansSerif", Font.PLAIN, 12));
        setForeground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(110, 62));
    }

    /** Loads and scales the icon to 24×24; returns null icon on any failure. */
    private ImageIcon loadIcon(String filename) {
        try {
            File f = new File("src/assets/icons/" + filename);
            if (!f.exists()) return null;
            Image img = new ImageIcon(f.getAbsolutePath()).getImage()
                            .getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    public void setActive(boolean active) {
        this.isActive = active;
        setIcon(active && activeIcon != null ? activeIcon
                : (!active && defaultIcon != null ? defaultIcon : null));
        setForeground(active ? new Color(102, 51, 204) : Color.DARK_GRAY);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isActive) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(102, 51, 204));
            g2.fillRect(0, getHeight() - 3, getWidth(), 3);
        }
    }
}
