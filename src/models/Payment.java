package models;

import services.Storable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Payment implements Storable {
    private final String paymentId;
    private final String appointmentId;
    private final String customerId;
    private final double amount;
    private final String paymentDate;

    public Payment(String paymentId, String appointmentId, String customerId, double amount) {
        this.paymentId = paymentId;
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.amount = amount;
        // Automatically set the current time for the receipt [cite: 24]
        this.paymentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toFileFormat() {
        return String.join("|", 
            paymentId, appointmentId, customerId, String.valueOf(amount), paymentDate
        );
    }

    // Getters for Receipt Generation
    public String getPaymentId() { return paymentId; }
    public String getAppointmentId() { return appointmentId; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
}