package controllers;

import java.util.ArrayList;
import models.Appointment;
import models.AppointmentStatus;
import services.FileHandler;

public class AppointmentController {
    private static final String APPOINTMENT_FILE = "appointments.txt";

    public static void createAppointment(Appointment appointment) {
        // Use the interface method for clean writing
        FileHandler.writeData(APPOINTMENT_FILE, appointment.toFileFormat());
    }

    public static ArrayList<Appointment> getAllAppointments() {
        ArrayList<String> lines = FileHandler.readData(APPOINTMENT_FILE);
        ArrayList<Appointment> appointments = new ArrayList<>();
        for (String line : lines) {
            String[] d = line.split("\\|");
            if (d.length >= 8) {
                appointments.add(new Appointment(
                    d[0], d[1], AppointmentStatus.valueOf(d[2]), d[3], 
                    Double.parseDouble(d[4]), d[5], d[6], d[7]
                ));
            }
        }
        return appointments;
    }

    public static void updateStatus(String appointmentId, AppointmentStatus newStatus) {
        ArrayList<Appointment> appointments = getAllAppointments();
        for (Appointment app : appointments) {
            if (app.getAppointmentId().equals(appointmentId)) {
                app.setStatus(newStatus);
                FileHandler.updateLine(APPOINTMENT_FILE, appointmentId, app.toFileFormat());
                break;
            }
        }
    }
}