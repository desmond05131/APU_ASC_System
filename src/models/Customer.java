package models;

public class Customer extends User {
    public Customer(String id, String name, String password, String email, String contactNumber) {
        super(id, name, password, "Customer", email, contactNumber);
    }

    @Override
    public String getDashboardAccess() {
        return "Customer Dashboard: Accessing service histories, feedbacks, and comments.";
    }

    @Override
    public String toString() {
        return String.join("|",
            getId(), getPassword(), getName(), "Customer",
            getEmail(), getContactNumber(),
            isDeleted() ? "DELETED" : "ACTIVE");
    }
}