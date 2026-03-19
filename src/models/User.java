package models;

import java.io.Serializable;

public abstract class User implements Serializable {
    private final String userId; // 'final' because the ID shouldn't change
    private String password;
    private String name;
    private final String role; // 'final' because a user's role is fixed upon creation

    public User(String userId, String password, String name, String role) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // Encapsulation: Getters and Setters
    public String getUserId() { return userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }

    // Abstract method to be overridden by each specific role (Polymorphism)
    public abstract void displayDashboard();
}