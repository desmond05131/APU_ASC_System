package controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import models.Appointment;
import models.AppointmentStatus;

public class SearchController {

    /**
     * Filters appointments based on multiple optional criteria.
     */
    public static ArrayList<Appointment> filterAppointments(String customerId, AppointmentStatus status) {
        ArrayList<Appointment> all = AppointmentController.getAllAppointments();
        
        return all.stream()
            .filter(a -> (customerId == null || a.getCustomerId().equalsIgnoreCase(customerId)))
            .filter(a -> (status == null || a.getStatus() == status))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Simple keyword search across appointment IDs or service types.
     */
    public static ArrayList<Appointment> searchByKeyword(String keyword) {
        ArrayList<Appointment> all = AppointmentController.getAllAppointments();
        String k = keyword.toLowerCase();
        
        return all.stream()
            .filter(a -> a.getAppointmentId().toLowerCase().contains(k) || 
                         a.getServiceType().toLowerCase().contains(k))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}