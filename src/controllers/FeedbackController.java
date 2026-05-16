package controllers;

import java.util.ArrayList;
import models.Feedback;
import services.FileHandler;

public class FeedbackController {
    private final ArrayList<Feedback> feedbackList = new ArrayList<>();

    public FeedbackController() { load(); }

    private void load() {
        feedbackList.clear();
        for (String line : FileHandler.readData("feedback.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 5) continue;
            try {
                Feedback f;
                if (isNumeric(p[1])) {
                    // Legacy 8-field format: feedbackId|rating|customerName|comment|date|serviceName|category|status
                    f = new Feedback(p[0], "", Integer.parseInt(p[1].trim()), p[2], p[3], p[4]);
                    if (p.length >= 7) { f.setServiceName(p[5]); f.setCategory(p[6]); }
                    if (p.length >= 8 && "DELETED".equals(p[7])) f.setDeleted(true);
                } else {
                    // New 9-field format: feedbackId|customerId|rating|customerName|comment|date|serviceName|category|status
                    if (p.length < 6) continue;
                    f = new Feedback(p[0], p[1], Integer.parseInt(p[2].trim()), p[3], p[4], p[5]);
                    if (p.length >= 8) { f.setServiceName(p[6]); f.setCategory(p[7]); }
                    if (p.length >= 9 && "DELETED".equals(p[8])) f.setDeleted(true);
                }
                feedbackList.add(f);
            } catch (NumberFormatException ignored) {}
        }
    }

    private static boolean isNumeric(String s) {
        try { Integer.parseInt(s.trim()); return true; }
        catch (NumberFormatException e) { return false; }
    }

    private void save() {
        ArrayList<String> lines = new ArrayList<>();
        for (Feedback f : feedbackList) lines.add(f.toString());
        FileHandler.writeData("feedback.txt", lines);
    }

    public ArrayList<Feedback> getAllFeedback() {
        load();
        ArrayList<Feedback> result = new ArrayList<>();
        for (Feedback f : feedbackList) if (!f.isDeleted()) result.add(f);
        return result;
    }

    public ArrayList<Feedback> getAllFeedbackIncludingDeleted() {
        load();
        return new ArrayList<>(feedbackList);
    }

    public void deleteFeedback(String id) {
        load();
        for (Feedback f : feedbackList) {
            if (f.getFeedbackId().equals(id)) { f.setDeleted(true); break; }
        }
        save();
    }

    public ArrayList<Feedback> search(String customerQ, String serviceQ,
                                      String ratingFilter, String catFilter, String dateQ) {
        load();
        ArrayList<Feedback> result = new ArrayList<>();
        for (Feedback f : feedbackList) {
            if (f.isDeleted()) continue;
            if (!customerQ.isEmpty() && !f.getCustomerName().toLowerCase().contains(customerQ.toLowerCase())) continue;
            if (!serviceQ.isEmpty()  && !f.getServiceName().toLowerCase().contains(serviceQ.toLowerCase())) continue;
            if (!"All".equals(ratingFilter) && !String.valueOf(f.getRating()).equals(ratingFilter)) continue;
            if (!"All".equals(catFilter)    && !f.getCategory().equalsIgnoreCase(catFilter)) continue;
            if (!dateQ.isEmpty() && !f.getDate().contains(dateQ)) continue;
            result.add(f);
        }
        return result;
    }

    public ArrayList<Feedback> search(String id, String ratingFilter) {
        return search("", "", ratingFilter, "All", "");
    }
}
