package models;

public class Appointment {
    private String appointmentId;
    private String serviceType; // Normal (1h) or Major (3h)
    private String status;      // Pending, Completed
    private String scheduledDate;
    private double totalAmount;
    private String customerId;
    private String technicianId;
    private String staffId;

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

    // Getters and Setters for all fields
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