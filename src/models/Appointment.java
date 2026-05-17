package models;

import services.Storable;

/**
 * Appointment
 * Represents a booked service appointment.
 * Implements Storable (interface) so it can be persisted via FileHandler.
 * OOP Concepts: Encapsulation, Interface implementation (Storable),
 *               Composition (uses AppointmentStatus enum).
 */
public class Appointment implements Storable {

    // Encapsulation: all fields private; accessed only via getters/setters
    private final String appointmentId;
    private final String serviceType;
    private final String scheduledDate;
    private final double totalAmount;
    private final String customerId;
    private final String technicianId;
    private final String staffId;
    private AppointmentStatus status;   // Only status is mutable (can be updated)

    public Appointment(String appointmentId, String serviceType, AppointmentStatus status,
                       String scheduledDate, double totalAmount, String customerId,
                       String technicianId, String staffId) {
        this.appointmentId = appointmentId;
        this.serviceType   = serviceType;
        this.status        = status;
        this.scheduledDate = scheduledDate;
        this.totalAmount   = totalAmount;
        this.customerId    = customerId;
        this.technicianId  = technicianId;
        this.staffId       = staffId;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getAppointmentId() { return appointmentId; }
    public String getServiceType()   { return serviceType;   }
    public String getScheduledDate() { return scheduledDate; }
    public double getTotalAmount()   { return totalAmount;   }
    public String getCustomerId()    { return customerId;    }
    public String getTechnicianId()  { return technicianId;  }   // <-- was missing
    public String getStaffId()       { return staffId;       }

    public AppointmentStatus getStatus()              { return status; }
    public void setStatus(AppointmentStatus status)   { this.status = status; }

    // ── Storable interface ────────────────────────────────────────────────────

    @Override
    public String toFileFormat() {
        return String.join("|",
            appointmentId, serviceType, status.name(), scheduledDate,
            String.valueOf(totalAmount), customerId, technicianId, staffId
        );
    }

    @Override
    public String toString() { return toFileFormat(); }
}
