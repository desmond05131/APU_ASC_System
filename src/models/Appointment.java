package models;

import services.Storable;

public class Appointment implements Storable {
    private final String appointmentId;
    private final String serviceType; 
    private final String scheduledDate;
    private final double totalAmount;
    private final String customerId;
    private final String technicianId;
    private final String staffId;
    private AppointmentStatus status; // Using Enum instead of String

    public Appointment(String appointmentId, String serviceType, AppointmentStatus status, 
                       String scheduledDate, double totalAmount, String customerId, 
                       String technicianId, String staffId) {
        this.appointmentId = appointmentId;
        this.serviceType = serviceType;
        this.status = status;
        this.scheduledDate = scheduledDate;
        this.totalAmount = totalAmount;
        this.customerId = customerId;
        this.technicianId = technicianId;
        this.staffId = staffId;
    }

    // Getters
    public String getAppointmentId() { return appointmentId; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public String getServiceType() { return serviceType; }
    public String getScheduledDate() { return scheduledDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getCustomerId() { return customerId; }

    @Override
    public String toFileFormat() {
        // Standardized format for all text files [cite: 44]
        return String.join("|", 
            appointmentId, serviceType, status.name(), scheduledDate, 
            String.valueOf(totalAmount), customerId, technicianId, staffId
        );
    }

    @Override
    public String toString() {
        return toFileFormat();
    }
}