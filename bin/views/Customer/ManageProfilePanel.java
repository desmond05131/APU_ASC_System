package Customer;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ManageProfilePanel extends JPanel {

    private CustomerDashboard dashboard;
    private String userId;

    private JTextField txtUserId, txtName, txtEmail, txtPhone, txtRole;

    // ✅ PROFILE PICTURE
    private JLabel avatarLabel;
    private Image avatarImg = null;

    public ManageProfilePanel(CustomerDashboard dashboard, String userId) {

        this.dashboard = dashboard;
        this.userId = userId;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));

        add(createCard());

        loadProfile();
    }

    // ================= CARD =================
    private JPanel createCard() {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(540, 560));

        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(Color.WHITE);

        // ===== AVATAR =====
        avatarLabel = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight());

                if (avatarImg == null) {
                    g2.setColor(new Color(235, 238, 242));
                    g2.fillOval(0, 0, size, size);

                    g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                    g2.setColor(Color.GRAY);
                    g2.drawString("👤", size / 3, size / 2 + 5);
                } else {
                    g2.setClip(new java.awt.geom.Ellipse2D.Double(0, 0, size, size));
                    g2.drawImage(avatarImg, 0, 0, size, size, null);
                }

                g2.dispose();
            }
        };

        avatarLabel.setPreferredSize(new Dimension(60, 60));
        avatarLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        avatarLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                chooseImage();
            }
        });

        // ===== TITLE =====
        JPanel text = new JPanel(new GridLayout(2,1));
        text.setBackground(Color.WHITE);

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Click avatar to change profile picture");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);

        text.add(title);
        text.add(subtitle);

        header.add(avatarLabel, BorderLayout.WEST);
        header.add(text, BorderLayout.CENTER);

        // ================= FORM =================
        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 0, 20, 0));

        txtUserId = createField("User ID");
        txtName = createField("Name");
        txtEmail = createField("Email");
        txtPhone = createField("Phone");
        txtRole = createField("Role");

        form.add(wrap(txtUserId));
        form.add(wrap(txtName));
        form.add(wrap(txtEmail));
        form.add(wrap(txtPhone));
        form.add(wrap(txtRole));

        // ================= BUTTONS =================
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Color.WHITE);

        JButton refresh = new JButton("Refresh");
        styleButton(refresh);
        refresh.addActionListener(e -> loadProfile());

        JButton back = new JButton("Back");
        styleButton(back);
        back.setBackground(new Color(60, 63, 65));
        back.setForeground(Color.WHITE);
        back.addActionListener(e -> dashboard.switchContent("DASHBOARD"));

        buttons.add(refresh);
        buttons.add(back);

        // ================= BUILD =================
        card.add(header, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        return card;
    }

    // ================= FIELD =================
    private JTextField createField(String label) {

        JTextField f = new JTextField();
        f.setEditable(false);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBackground(new Color(250, 250, 250));

        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 10, 8, 10)
        ));

        f.putClientProperty("label", label);

        return f;
    }

    private JPanel wrap(JTextField field) {

        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);

        JLabel label = new JLabel((String) field.getClientProperty("label"));
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(Color.GRAY);

        p.add(label, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);

        return p;
    }

    // ================= BUTTON STYLE =================
    private void styleButton(JButton btn) {

        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ================= LOAD PROFILE =================
    public void loadProfile() {

        if (userId == null || userId.trim().isEmpty()) return;

        File file = new File("data/users.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {

                String[] d = line.split("\\|");

                if (d.length == 6 && d[0].equals(userId)) {

                    txtUserId.setText(d[0]);
                    txtName.setText(d[2]);
                    txtEmail.setText(d[4]);
                    txtPhone.setText(d[5]);
                    txtRole.setText(d[3]);

                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "User not found: " + userId);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load profile");
        }
    }

    // ================= IMAGE PICKER =================
    private void chooseImage() {

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Image", "jpg", "png", "jpeg"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            try {
                avatarImg = ImageIO.read(fc.getSelectedFile());
                avatarLabel.repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Image load failed");
            }
        }
    }
}