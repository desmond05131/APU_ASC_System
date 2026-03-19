package services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import models.Payment;

public class ReceiptService {
    private static final String RECEIPT_DIR = "data/receipts/";

    /**
     * Generates a formatted text-based receipt saved as a .txt file.
     * To generate a true PDF, you would include the 'iText' library in your /lib folder.
     */
    public static void generateAutomatedReceipt(Payment payment) {
        String fileName = RECEIPT_DIR + "Receipt_" + payment.getPaymentId() + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("========================================"); writer.newLine();
            writer.write("       APU AUTOMOTIVE SERVICE CENTRE    "); writer.newLine();
            writer.write("========================================"); writer.newLine();
            writer.write("Receipt ID:    " + payment.getPaymentId()); writer.newLine();
            writer.write("Appointment:   " + payment.getAppointmentId()); writer.newLine();
            writer.write("Date:          " + payment.getPaymentDate()); writer.newLine();
            writer.write("----------------------------------------"); writer.newLine();
            writer.write("Total Amount:  RM " + String.format("%.2f", payment.getAmount())); writer.newLine();
            writer.write("----------------------------------------"); writer.newLine();
            writer.write("Status:        PAID - THANK YOU!"); writer.newLine();
            writer.write("========================================"); writer.newLine();
            
            System.out.println("Receipt generated: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to generate receipt: " + e.getMessage());
        }
    }
}