package models;

public class Appointment {
    // These fields are set once and do not change
    private final String appointmentId;
    private final String serviceType; // Normal (1h) or Major (3h)
    private final String scheduledDate;
    private final double totalAmount;
    private final String customerId;
    private final String technicianId;
    private final String staffId;
    
    // Status can change, so it is NOT marked as final
    private String status; // Pending, Completed

    public Appointment(String appointmentId, String serviceType, String status, 
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

    // Getters for all fields
    public String getAppointmentId() { return appointmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getServiceType() { return serviceType; }
    public double getTotalAmount() { return totalAmount; }
    public String getCustomerId() { return customerId; }
    public String getTechnicianId() { return technicianId; }

    @Override
    public String toString() {
        return appointmentId + "|" + serviceType + "|" + status + "|" + scheduledDate + 
               "|" + totalAmount + "|" + customerId + "|" + technicianId + "|" + staffId;
    }
}