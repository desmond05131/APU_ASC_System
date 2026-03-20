package controllers;

import java.util.*;
import services.FileHandler;

public class ReportController {
    private final String PAYMENT_FILE = "src/database/payments.txt";
    private final String APPOINTMENT_FILE = "src/database/appointments.txt";

    // Returns summary metrics: [Total Revenue, Total Apps, Completed, Cancelled]
    public Map<String, Double> getSummaryStats(String start, String end) {
        ArrayList<String> paymentData = FileHandler.readData(PAYMENT_FILE);
        ArrayList<String> appData = FileHandler.readData(APPOINTMENT_FILE);
        
        double revenue = 0;
        int totalApps = 0, completed = 0, cancelled = 0;

        // Process Revenue
        for (String line : paymentData) {
            String[] p = line.split("\\|");
            if (p.length >= 5 && isWithinRange(p[4], start, end)) {
                revenue += Double.parseDouble(p[3]);
            }
        }

        // Process Appointment Statuses
        for (String line : appData) {
            String[] p = line.split("\\|");
            if (p.length >= 7 && isWithinRange(p[4], start, end)) {
                totalApps++;
                String status = p[6].toUpperCase();
                if (status.equals("COMPLETED")) completed++;
                else if (status.equals("CANCELLED")) cancelled++;
            }
        }

        Map<String, Double> stats = new HashMap<>();
        stats.put("Revenue", revenue);
        stats.put("Total", (double)totalApps);
        stats.put("Completed", (double)completed);
        stats.put("Cancelled", (double)cancelled);
        return stats;
    }

    // Monthly data for the Bar Chart: Map<"Jan", 1500.0>
    public Map<String, Double> getMonthlyRevenue() {
        ArrayList<String> data = FileHandler.readData(PAYMENT_FILE);
        Map<String, Double> monthly = new LinkedHashMap<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String m : months) monthly.put(m, 0.0);

        for (String line : data) {
            String[] p = line.split("\\|");
            if (p.length >= 5) {
                int monthIdx = Integer.parseInt(p[4], 5, 7, 10) - 1;
                String monthName = months[monthIdx];
                monthly.put(monthName, monthly.get(monthName) + Double.parseDouble(p[3]));
            }
        }
        return monthly;
    }

    private boolean isWithinRange(String date, String start, String end) {
        if (start.isEmpty() || end.isEmpty()) return true;
        return date.compareTo(start) >= 0 && date.compareTo(end) <= 0;
    }
}