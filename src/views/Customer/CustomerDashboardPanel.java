package views.Customer;

import controllers.AppointmentController;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.Appointment;
import models.AppointmentStatus;
import services.FileHandler;

public class CustomerDashboardPanel extends JPanel {

    public CustomerDashboardPanel(CustomerDashboard dashboard) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        String name       = dashboard.getMainFrame().getCurrentUser().getName();
        String customerId = dashboard.getMainFrame().getCurrentUser().getId();

        // Welcome strip
        JPanel strip = new JPanel(new BorderLayout());
        strip.setBackground(new Color(100, 100, 248));
        strip.setBorder(BorderFactory.createEmptyBorder(22, 32, 22, 32));
        JLabel greet = new JLabel("Welcome back, " + name + "!");
        greet.setFont(new Font("SansSerif", Font.BOLD, 22));
        greet.setForeground(Color.WHITE);
        JLabel sub = new JLabel("APU Automotive Service Centre");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(200, 210, 255));
        strip.add(greet, BorderLayout.WEST);
        strip.add(sub,   BorderLayout.EAST);
        add(strip, BorderLayout.NORTH);

        // Compute stats
        List<Appointment> myAppts = new ArrayList<>();
        for (Appointment a : AppointmentController.getAllAppointments()) {
            if (a.getCustomerId().equals(customerId)) myAppts.add(a);
        }
        long completedCount = myAppts.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED
                          || a.getStatus() == AppointmentStatus.PAID)
                .count();

        String latestService = "—";
        String latestDate    = "";
        for (int i = myAppts.size() - 1; i >= 0; i--) {
            Appointment a = myAppts.get(i);
            if (a.getStatus() == AppointmentStatus.COMPLETED
                    || a.getStatus() == AppointmentStatus.PAID) {
                latestService = a.getServiceType();
                latestDate    = " on " + a.getScheduledDate();
                break;
            }
        }

        double totalSpent = 0;
        for (String line : FileHandler.readData("payments.txt")) {
            String[] p = line.split("\\|");
            if (p.length >= 4 && p[2].equals(customerId)) {
                try { totalSpent += Double.parseDouble(p[3]); }
                catch (NumberFormatException ignored) {}
            }
        }

        // Stat cards
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 18, 0));
        cardsRow.setBackground(Color.WHITE);
        cardsRow.setBorder(BorderFactory.createEmptyBorder(32, 40, 20, 40));
        cardsRow.add(statCard("Past Services",  String.valueOf(completedCount),
                              "Completed or paid"));
        cardsRow.add(statCard("Latest Service", latestService, latestDate));
        cardsRow.add(statCard("Total Spent",    String.format("RM %.2f", totalSpent),
                              "All payments"));

        // Quick-action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        actions.setBackground(Color.WHITE);
        actions.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        JButton btnHistory  = quickBtn("View Service History");
        JButton btnFeedback = quickBtn("My Feedback");
        btnHistory .addActionListener(e -> dashboard.switchContent("HISTORY"));
        btnFeedback.addActionListener(e -> dashboard.switchContent("FEEDBACK"));
        actions.add(btnHistory);
        actions.add(btnFeedback);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);
        center.add(cardsRow, BorderLayout.NORTH);
        center.add(actions,  BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JPanel statCard(String title, String value, String sub) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(247, 248, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLbl.setForeground(new Color(100, 110, 130));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLbl.setForeground(new Color(30, 35, 60));
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLbl.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));

        JLabel subLbl = new JLabel(sub.isEmpty() ? " " : sub);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLbl.setForeground(new Color(140, 150, 170));
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(valueLbl);
        card.add(subLbl);
        return card;
    }

    private JButton quickBtn(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(100, 100, 248));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
