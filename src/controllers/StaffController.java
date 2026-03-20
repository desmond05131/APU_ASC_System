package controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import models.CounterStaff;
import models.Technician;
import models.User;
import services.FileHandler;
import utils.PasswordHasher;

public class StaffController {
    private ArrayList<User> allUsers;

    public StaffController() {
        loadUsers();
    }

    private void loadUsers() {
        ArrayList<String> lines = FileHandler.readData("users.txt");
        this.allUsers = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 4) {
                String id = parts[0];
                String password = parts[1];
                String name = parts[2];
                String role = parts[3];
                String email = parts.length > 4 ? parts[4] : "";
                String contactNumber = parts.length > 5 ? parts[5] : "";
                
                if ("Technician".equals(role)) {
                    allUsers.add(new Technician(id, name, password, email, contactNumber));
                } else if ("CounterStaff".equals(role)) {
                    allUsers.add(new CounterStaff(id, name, password, email, contactNumber));
                }
            }
        }
    }

    public ArrayList<User> getStaffOnly() {
        return allUsers.stream()
            .filter(u -> u instanceof Technician || u instanceof CounterStaff)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean addStaff(String name, String username, String password, String role) {
        String id = generateId(role);
        String hashedPw = PasswordHasher.hashPassword(password);
        
        User newUser;
        if (role.equals("Technician")) {
            newUser = new Technician(id, name, hashedPw, username, "");
        } else {
            newUser = new CounterStaff(id, name, hashedPw, username, "");
        }

        allUsers.add(newUser);
        FileHandler.writeData("users.txt", newUser.toString());
        return true;
    }

    public boolean updateStaff(String id, String name, String username, String role) {
        for (User u : allUsers) {
            if (u.getId().equals(id)) {
                u.setName(name);
                u.setEmail(username);
                // Rebuild file with updated data
                ArrayList<String> lines = new ArrayList<>();
                for (User user : allUsers) {
                    lines.add(user.toString());
                }
                // Clear and rewrite
                try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter("data/users.txt"))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                } catch (java.io.IOException e) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public boolean deleteStaff(String id) {
        boolean removed = allUsers.removeIf(u -> u.getId().equals(id));
        if (removed) {
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter("data/users.txt"))) {
                for (User user : allUsers) {
                    writer.write(user.toString());
                    writer.newLine();
                }
            } catch (java.io.IOException e) {
                return false;
            }
        }
        return removed;
    }

    private String generateId(String role) {
        String prefix = role.equals("Technician") ? "T" : "C";
        return prefix + (getStaffOnly().size() + 101); // Simple ID generation
    }
}