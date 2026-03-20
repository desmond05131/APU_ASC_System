package models;

public class Feedback {
    private final String feedbackId;
    private final int rating;
    private final String customerName;
    private final String comment;
    private final String date;

    public Feedback(String feedbackId, int rating, String customerName, String comment, String date) {
        this.feedbackId = feedbackId;
        this.rating = rating;
        this.customerName = customerName;
        this.comment = comment;
        this.date = date;
    }

    // Getters for the Table Model
    public String getFeedbackId() { return feedbackId; }
    public int getRating() { return rating; }
    public String getCustomerName() { return customerName; }
    public String getComment() { return comment; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return feedbackId + "|" + rating + "|" + customerName + "|" + comment + "|" + date;
    }
}