package controllers;

import services.FileHandler;

public class PaymentController {
    private static final String PAYMENT_FILE = "payments.txt";

    public static void processPayment(String appointmentId, double amount, String customerId) {
        String paymentRecord = appointmentId + "|" + customerId + "|" + amount + "|Paid";
        FileHandler.writeData(PAYMENT_FILE, paymentRecord);
        
        // After payment, update the appointment status in the other file
        AppointmentController.updateStatus(appointmentId, "Completed");
    }

    public static String generateReceipt(String appointmentId) {
        // Logic to format a receipt string for the GUI
        return "--- APU-ASC RECEIPT ---\nAppointment ID: " + appointmentId;
    }
}