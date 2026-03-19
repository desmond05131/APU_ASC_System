package models;

/**
 * Concrete class for Manager role. 
 * Illustrates Inheritance.
 */
public class Manager extends User {
    public Manager(String userId, String password, String name) {
        super(userId, password, name, "Manager");
    }

    @Override
    public void displayDashboard() {
        // This will eventually call your ManagerDashboard GUI panel
        System.out.println("Launching Manager Dashboard for: " + getName());
    }
}