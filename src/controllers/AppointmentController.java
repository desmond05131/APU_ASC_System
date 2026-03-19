package controllers;

import java.util.ArrayList;
import models.Appointment;
import services.FileHandler;

public class AppointmentController {
    private static final String APPOINTMENT_FILE = "appointments.txt";

    public static void createAppointment(Appointment appointment) {
        FileHandler.writeData(APPOINTMENT_FILE, appointment.toString());
    }

    public static ArrayList<Appointment> getAllAppointments() {
        ArrayList<String> lines = FileHandler.readData(APPOINTMENT_FILE);
        ArrayList<Appointment> appointments = new ArrayList<>();
        for (String line : lines) {
            String[] d = line.split("\\|");
            appointments.add(new Appointment(d[0], d[1], d[2], d[3], 
                        Double.parseDouble(d[4]), d[5], d[6], d[7]));
        }
        return appointments;
    }

    public static void updateStatus(String appointmentId, String newStatus) {
        ArrayList<Appointment> appointments = getAllAppointments();
        for (Appointment app : appointments) {
            if (app.getAppointmentId().equals(appointmentId)) {
                app.setStatus(newStatus);
                FileHandler.updateLine(APPOINTMENT_FILE, appointmentId, app.toString());
                break;
            }
        }
    }
}