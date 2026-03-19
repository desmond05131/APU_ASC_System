package models;

public class Customer extends User {
    public Customer(String userId, String password, String name) {
        super(userId, password, name, "Customer");
    }

    @Override
    public void displayDashboard() {
        System.out.println("Opening Customer Dashboard for: " + getName());
        // Logic for Table 1.0: Access Histories, View Feedbacks, Provide Comments
    }

    @Override
    public String toString() {
        return getUserId() + "|" + getPassword() + "|" + getName() + "|Customer";
    }
}