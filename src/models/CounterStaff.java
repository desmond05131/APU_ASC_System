package models;

public class CounterStaff extends User {
    public CounterStaff(String userId, String password, String name) {
        super(userId, password, name, "CounterStaff");
    }

    @Override
    public void displayDashboard() {
        System.out.println("Opening Staff Dashboard for: " + getName());
        // Logic for Table 1.0: CRUD customers, Manage Appointments, Collect Payment
    }

    @Override
    public String toString() {
        return getUserId() + "|" + getPassword() + "|" + getName() + "|CounterStaff";
    }
}