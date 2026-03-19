package models;

public class Customer extends User {
    public Customer(String id, String name, String password, String email) {
        super(id, name, password, "Customer", email);
    }

    @Override
    public String getDashboardAccess() {
        return "Customer Dashboard: Accessing service histories, feedbacks, and comments.";
    }

    @Override
    public String toString() {
        return getId() + "|" + getPassword() + "|" + getName() + "|Customer";
    }
}