package views.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {

    private JPanel contentArea;

    // Panels
    private CustomerDashboardPanel dashboardPanel;
    private ServicePaymentHistoryPanel historyPanel;
    private FeedbackPanel feedbackPanel;
    private AddFeedbackPanel addFeedbackPanel;
    private ManageProfilePanel profilePanel;

    public CustomerDashboard() {
        setTitle("Customer Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        // ✅ AUTO LOAD DASHBOARD FIRST
        switchContent("DASHBOARD");
    }

    // ================= HEADER =================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(225, 235, 245));
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("APU Automotive Service Centre - Customer Portal");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel userDisplay = new JLabel("Logged in as: Customer");
        userDisplay.setFont(new Font("SansSerif", Font.ITALIC, 12));

        header.add(title, BorderLayout.WEST);
        header.add(userDisplay, BorderLayout.EAST);

        return header;
    }

    // ================= MAIN =================
    private JPanel createMainContent() {

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);

        // ================= NAVBAR (ORDER FIXED) =================
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        navBar.setBackground(Color.WHITE);
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // 🔥 DASHBOARD FIRST (IMPORTANT FIX)
        navBar.add(createNavButton("Dashboard", "🏠", "DASHBOARD"));
        navBar.add(createNavButton("Profile", "👤", "PROFILE"));
        navBar.add(createNavButton("History", "📋", "HISTORY"));
        navBar.add(createNavButton("Feedback", "💬", "FEEDBACK"));

        // ================= LOGOUT =================
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightNav.setOpaque(false);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> dispose());

        rightNav.add(logoutBtn);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBackground(Color.WHITE);
        topWrapper.add(navBar, BorderLayout.CENTER);
        topWrapper.add(rightNav, BorderLayout.EAST);

        // ================= CONTENT =================
        contentArea = new JPanel(new BorderLayout(0, 0));
        contentArea.setBackground(Color.WHITE);

        mainPanel.add(topWrapper, BorderLayout.NORTH);
        mainPanel.add(contentArea, BorderLayout.CENTER);

        return mainPanel;
    }

    // ================= NAV BUTTON =================
    private JButton createNavButton(String text, String icon, String view) {

        JButton btn = new JButton(icon + "  " + text);

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addActionListener(e -> switchContent(view));

        return btn;
    }

    // ================= SWITCH CONTENT =================
    public void switchContent(String view) {

        contentArea.removeAll();

        switch (view) {

            case "DASHBOARD":
                if (dashboardPanel == null)
                    dashboardPanel = new CustomerDashboardPanel(this);
                contentArea.add(dashboardPanel, BorderLayout.CENTER);
                break;

            case "PROFILE":
            profilePanel = new ManageProfilePanel(this, "M001");
            contentArea.add(profilePanel, BorderLayout.CENTER);
            break;

            case "HISTORY":
                if (historyPanel == null)
                    historyPanel = new ServicePaymentHistoryPanel(this);
                contentArea.add(historyPanel, BorderLayout.CENTER);
                break;

            case "FEEDBACK":
                if (feedbackPanel == null)
                    feedbackPanel = new FeedbackPanel(this);

                feedbackPanel.loadFeedback();
                contentArea.add(feedbackPanel, BorderLayout.CENTER);
                break;

            case "ADD_FEEDBACK":
                if (addFeedbackPanel == null)
                    addFeedbackPanel = new AddFeedbackPanel(this);

                contentArea.add(addFeedbackPanel, BorderLayout.CENTER);
                break;
        }

        contentArea.revalidate();
        contentArea.repaint();
    }

    // ================= ADD FEEDBACK =================
    public void openAddFeedback() {
        switchContent("ADD_FEEDBACK");
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerDashboard().setVisible(true));
    }
}