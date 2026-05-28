package services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import models.Payment;

public class ReceiptService {
    private static final String RECEIPT_DIR = "data/receipts/";

    /** Single source of truth for receipt text layout. */
    public static String formatReceiptText(String payId, String apptId,
                                           String customerName, String serviceName,
                                           double amount, String datePaid) {
        return String.format(
            "========================================%n"
          + "    APU AUTOMOTIVE SERVICE CENTRE       %n"
          + "========================================%n"
          + "Receipt ID  : %s%n"
          + "Appointment : %s%n"
          + "Customer    : %s%n"
          + "Service     : %s%n"
          + "----------------------------------------%n"
          + "Total Amount: RM %.2f%n"
          + "Date Paid   : %s%n"
          + "----------------------------------------%n"
          + "Status      : PAID - THANK YOU!         %n"
          + "========================================%n",
            payId, apptId,
            customerName.isEmpty() ? "(unknown)" : customerName,
            serviceName.isEmpty()  ? "(unknown)" : serviceName,
            amount, datePaid);
    }

    /** Generates a receipt file with customer name and service name included. */
    public static void generateAutomatedReceipt(Payment payment,
                                                String customerName,
                                                String serviceName) {
        new File(RECEIPT_DIR).mkdirs();
        String fileName = RECEIPT_DIR + "Receipt_" + payment.getPaymentId() + ".txt";
        String text = formatReceiptText(payment.getPaymentId(), payment.getAppointmentId(),
                customerName, serviceName, payment.getAmount(), payment.getPaymentDate());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(text);
            System.out.println("Receipt generated: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to generate receipt: " + e.getMessage());
        }
    }

    /** Backward-compatible overload - uses customer ID as customer name placeholder. */
    public static void generateAutomatedReceipt(Payment payment) {
        generateAutomatedReceipt(payment, payment.getPaymentId(), "");
    }
}
