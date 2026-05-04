package views.Customer;

import java.util.UUID;

public class Feedback {

    private String feedbackId;
    private String topic;
    private int rating;
    private String comment;

    // =========================
    // NEW FEEDBACK (USED BY ADD FEEDBACK)
    // =========================
    public Feedback(String topic, int rating, String comment) {
        this.feedbackId = UUID.randomUUID().toString();
        this.topic = topic;
        this.rating = rating;
        this.comment = comment;
    }

    // =========================
    // LOAD FROM FILE
    // =========================
    public Feedback(String feedbackId, String topic, int rating, String comment) {
        this.feedbackId = feedbackId;
        this.topic = topic;
        this.rating = rating;
        this.comment = comment;
    }

    // =========================
    // FILE FORMAT
    // =========================
    public String toFileString() {
        return feedbackId + "|" + topic + "|" + rating + "|" + comment;
    }

    // =========================
    // SAFE PARSER (FIXED CRASH ISSUE)
    // =========================
    public static Feedback fromFileString(String line) {

        if (line == null || !line.contains("|")) {
            return new Feedback("UNKNOWN", "Invalid Data", 1, "Skipped corrupted row");
        }

        String[] p = line.split("\\|");

        // safety check for missing columns
        if (p.length < 4) {
            return new Feedback("UNKNOWN", "Invalid Data", 1, "Incomplete row");
        }

        String id = p[0];
        String topic = p[1];

        int rating = 1;
        try {
            rating = Integer.parseInt(p[2].replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            rating = 1; // fallback safe value
        }

        String comment = p[3];

        return new Feedback(id, topic, rating, comment);
    }

    // =========================
    // GETTERS
    // =========================
    public String getFeedbackId() {
        return feedbackId;
    }

    public String getTopic() {
        return topic;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    // =========================
    // SETTERS
    // =========================
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}