package controllers;

import java.util.*;
import models.Appointment;
import services.FileHandler;

public class ReportController {
    private final String PAYMENT_FILE = "payments.txt";

    public double calculateTotalRevenue(String startDate, String endDate) {
        ArrayList<String> lines = FileHandler.readData(PAYMENT_FILE);
        double total = 0;
        for (String line : lines) {
            String[] d = line.split("\\|");
            if (d.length >= 5) {
                String paymentDate = d[4]; // Date is index 4
                if (isWithinRange(paymentDate, startDate, endDate)) {
                    total += Double.parseDouble(d[3]);
                }
            }
        }
        return total;
    }

    public Map<String, Integer> getServiceFrequency(String startDate, String endDate) {
        ArrayList<Appointment> apps = AppointmentController.getAllAppointments(); //
        Map<String, Integer> freq = new HashMap<>();
        for (Appointment a : apps) {
            if (isWithinRange(a.getScheduledDate(), startDate, endDate)) {
                freq.put(a.getServiceType(), freq.getOrDefault(a.getServiceType(), 0) + 1);
            }
        }
        return freq;
    }

    private boolean isWithinRange(String date, String start, String end) {
        if (start.isEmpty() || end.isEmpty()) return true; // Show all if no filter
        return date.compareTo(start) >= 0 && date.compareTo(end) <= 0;
    }
}