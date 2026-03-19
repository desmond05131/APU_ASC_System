package models;

public class Manager extends User {
    public Manager(String userId, String password, String name) {
        super(userId, password, name, "Manager");
    }

    @Override
    public void displayDashboard() {
        System.out.println("Opening Manager Dashboard for: " + getName());
        // Logic for Table 1.0: CRUD staff, Set Prices, Review Feedback, Analyzed Reports
    }

    @Override
    public String toString() {
        return getUserId() + "|" + getPassword() + "|" + getName() + "|Manager";
    }
}