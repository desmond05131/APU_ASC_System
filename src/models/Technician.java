package models;

public class Technician extends User {
    public Technician(String userId, String password, String name) {
        super(userId, password, name, "Technician");
    }

    @Override
    public void displayDashboard() {
        System.out.println("Opening Technician Dashboard for: " + getName());
        // Logic for Table 1.0: Check Assigned Jobs, Update Status, Provide Feedback
    }

    @Override
    public String toString() {
        return getUserId() + "|" + getPassword() + "|" + getName() + "|Technician";
    }
}