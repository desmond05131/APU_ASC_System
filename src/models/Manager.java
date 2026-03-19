package models;

public class Manager extends User {
    
    public Manager(String id, String name, String password, String email) {
        super(id, name, password, "Manager", email);
    }

    @Override
    public String getDashboardAccess() {
        // Specific logic for Manager functionalities like "Analyzed reports"
        return "Manager Dashboard: Accessing reports and staff management.";
    }
}