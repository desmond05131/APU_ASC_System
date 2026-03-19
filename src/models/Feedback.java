package models;

public class Feedback {
    private final String feedbackId;
    private final String appointmentId;
    private final String userId; 
    private final String comment;
    private final int rating;

    public Feedback(String feedbackId, String appointmentId, String userId, String comment, int rating) {
        this.feedbackId = feedbackId;
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
    }

    public String getComment() { return comment; }
    public int getRating() { return rating; }

    @Override
    public String toString() {
        return feedbackId + "|" + appointmentId + "|" + userId + "|" + comment + "|" + rating;
    }
}