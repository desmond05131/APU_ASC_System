package models;

public class CounterStaff extends User {
    public CounterStaff(String id, String name, String password, String email, String contactNumber) {
        super(id, name, password, "CounterStaff", email, contactNumber);
    }

    @Override
    public String getDashboardAccess() {
        return "CounterStaff Dashboard: Managing customers, appointments, and payments.";
    }

    @Override
    public String toString() {
        return getId() + "|" + getPassword() + "|" + getName() + "|CounterStaff";
    }
}