package models;

public class Feedback {
    private final String feedbackId;
    private final String customerId;
    private final String serviceId;
    private final int rating;
    private final String comment;
    private final String date;

    public Feedback(String feedbackId, String customerId, String serviceId, int rating, String comment, String date) {
        this.feedbackId = feedbackId;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    // Getters
    public String getFeedbackId() { return feedbackId; }
    public String getCustomerId() { return customerId; }
    public String getServiceId() { return serviceId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return String.join("|", feedbackId, customerId, serviceId, String.valueOf(rating), comment, date);
    }
}