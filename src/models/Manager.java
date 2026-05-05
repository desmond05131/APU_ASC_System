package models;

public class Manager extends User {
    
    public Manager(String id, String name, String password, String email, String contactNumber) {
        super(id, name, password, "Manager", email, contactNumber);
    }

    @Override
    public String getDashboardAccess() {
        return "Manager Dashboard: Accessing reports and staff management.";
    }

    @Override
    public String toString() {
        return getId() + "|" + getPassword() + "|" + getName() + "|Manager|"
                + getEmail() + "|" + getContactNumber();
    }
}