package models;

public class Feedback {
    private String feedbackId;
    private String appointmentId;
    private String userId; // The person who gave the feedback
    private String comment;
    private int rating; // 1-5

    public Feedback(String feedbackId, String appointmentId, String userId, String comment, int rating) {
        this.feedbackId = feedbackId;
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
    }

    // Getters
    public String getComment() { return comment; }
    public int getRating() { return rating; }

    @Override
    public String toString() {
        return feedbackId + "|" + appointmentId + "|" + userId + "|" + comment + "|" + rating;
    }
}