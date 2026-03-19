package models;

import java.io.Serializable;

/**
 * Abstract class representing a system user. 
 * Implements Encapsulation and Abstraction.
 */
public abstract class User implements Serializable {
    private String userId;
    private String password;
    private String name;
    private String role;

    public User(String userId, String password, String name, String role) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // Encapsulation: Getter and Setter Methods
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Abstract method: Each role will display its unique dashboard UI
    public abstract void displayDashboard();
    
    @Override
    public String toString() {
        // Uses pipe delimiter for easy text file parsing
        return userId + "|" + password + "|" + name + "|" + role;
    }
}