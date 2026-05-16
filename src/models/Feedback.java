package models;

public class Feedback {
    private final String feedbackId;
    private final String customerId;   // owner of the appointment this feedback is about
    private final int    rating;
    private final String customerName; // display name / author
    private final String comment;
    private final String date;
    private String  serviceName = "";
    private String  category    = "";
    private boolean deleted     = false;

    public Feedback(String feedbackId, String customerId, int rating,
                    String customerName, String comment, String date) {
        this.feedbackId   = feedbackId;
        this.customerId   = customerId;
        this.rating       = rating;
        this.customerName = customerName;
        this.comment      = comment;
        this.date         = date;
    }

    public String getFeedbackId()   { return feedbackId; }
    public String getCustomerId()   { return customerId; }
    public int    getRating()       { return rating; }
    public String getCustomerName() { return customerName; }
    public String getComment()      { return comment; }
    public String getDate()         { return date; }
    public String getServiceName()  { return serviceName; }
    public void   setServiceName(String s) { this.serviceName = s; }
    public String getCategory()     { return category; }
    public void   setCategory(String c)    { this.category = c; }
    public boolean isDeleted()      { return deleted; }
    public void   setDeleted(boolean d)    { this.deleted = d; }

    /** Format: feedbackId|customerId|rating|customerName|comment|date|serviceName|category|status */
    @Override
    public String toString() {
        return String.join("|", feedbackId, customerId, String.valueOf(rating),
                customerName, comment, date, serviceName, category,
                deleted ? "DELETED" : "ACTIVE");
    }
}
