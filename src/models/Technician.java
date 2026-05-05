package models;

public class Technician extends User {
    public Technician(String id, String name, String password, String email, String contactNumber) {
        super(id, name, password, "Technician", email, contactNumber);
    }
        
    @Override
    public String getDashboardAccess() {
        return "Technician Dashboard: Checking assigned jobs, updating status, and providing feedback.";
    }

    @Override
    public String toString() {
        return getId() + "|" + getPassword() + "|" + getName() + "|Technician|"
                + getEmail() + "|" + getContactNumber();
    }
}