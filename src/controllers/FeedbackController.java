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
                Feedback f = new Feedback(p[0], Integer.parseInt(p[1].trim()), p[2], p[3], p[4]);
                if (p.length >= 7) { f.setServiceName(p[5]); f.setCategory(p[6]); }
                if (p.length >= 8 && "DELETED".equals(p[7])) f.setDeleted(true);
                feedbackList.add(f);
            } catch (NumberFormatException ignored) {}
        }
    }

    private void save() {
        ArrayList<String> lines = new ArrayList<>();
        for (Feedback f : feedbackList) lines.add(f.toString());
        FileHandler.writeData("feedback.txt", lines);
    }

    /** Returns all non-deleted feedback (reloads from file). */
    public ArrayList<Feedback> getAllFeedback() {
        load();
        ArrayList<Feedback> result = new ArrayList<>();
        for (Feedback f : feedbackList) if (!f.isDeleted()) result.add(f);
        return result;
    }

    /** Returns all feedback including soft-deleted (reloads from file). */
    public ArrayList<Feedback> getAllFeedbackIncludingDeleted() {
        load();
        return new ArrayList<>(feedbackList);
    }

    /** Soft-deletes a feedback entry by ID. */
    public void deleteFeedback(String id) {
        for (Feedback f : feedbackList) {
            if (f.getFeedbackId().equals(id)) { f.setDeleted(true); break; }
        }
        save();
    }

    /**
     * Multi-field search. Pass empty string / "All" to skip a filter.
     * ratingFilter: "All" or "1"–"5".
     * catFilter:    "All" or category name.
     */
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

    /** Legacy overload kept so existing callers still compile. */
    public ArrayList<Feedback> search(String id, String ratingFilter) {
        return search("", "", ratingFilter, "All", "");
    }
}
