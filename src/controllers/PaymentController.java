package controllers;

import java.util.ArrayList;
import models.AppointmentStatus;
import models.Payment;
import services.FileHandler;

public class PaymentController {
    private static final String PAYMENT_FILE = "payments.txt";

    /**
     * Records a new payment and updates the corresponding appointment status to PAID. 
     */
    public static void processPayment(String paymentId, String appointmentId, String customerId, double amount) {
        Payment newPayment = new Payment(paymentId, appointmentId, customerId, amount);
        
        // 1. Save payment record to payments.txt [cite: 44]
        FileHandler.writeData(PAYMENT_FILE, newPayment.toFileFormat());
        
        // 2. Update the appointment status to PAID using the existing controller 
        AppointmentController.updateStatus(appointmentId, AppointmentStatus.PAID);
    }

    /**
     * Formats a professional receipt string for the GUI display. [cite: 24, 62]
     */
    public static String generateReceipt(String paymentId) {
        ArrayList<String> lines = FileHandler.readData(PAYMENT_FILE);
        for (String line : lines) {
            String[] d = line.split("\\|");
            if (d[0].equals(paymentId)) {
                return String.format("""
                    --- APU-ASC OFFICIAL RECEIPT ---
                    Receipt ID: %s
                    Appointment: %s
                    Customer ID: %s
                    Amount Paid: RM %.2f
                    Date: %s
                    --------------------------------""",
                    d[0], d[1], d[2], Double.parseDouble(d[3]), d[4]
                );
            }
        }
        return "Receipt not found.";
    }
}