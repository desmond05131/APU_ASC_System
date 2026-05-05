package controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import models.CounterStaff;
import models.Manager;
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
        allUsers = new ArrayList<>();
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 4) continue;
            String id       = p[0];
            String password = p[1];
            String name     = p[2];
            String role     = p[3];
            String email    = p.length > 4 ? p[4] : "";
            String contact  = p.length > 5 ? p[5] : "";
            switch (role) {
                case "Manager"      -> allUsers.add(new Manager(id, name, password, email, contact));
                case "Technician"   -> allUsers.add(new Technician(id, name, password, email, contact));
                case "CounterStaff" -> allUsers.add(new CounterStaff(id, name, password, email, contact));
            }
        }
    }

    /** All users across all roles. */
    public ArrayList<User> getAllUsers() {
        loadUsers();
        return new ArrayList<>(allUsers);
    }

    /** Non-manager staff only (Technician + CounterStaff). */
    public ArrayList<User> getStaffOnly() {
        return allUsers.stream()
            .filter(u -> u instanceof Technician || u instanceof CounterStaff)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean addStaff(String name, String email, String contact, String password, String role) {
        loadUsers();
        String id       = generateId(role);
        String hashedPw = password.isEmpty() ? "" : PasswordHasher.hashPassword(password);

        User newUser = switch (role) {
            case "Technician" -> new Technician(id, name, hashedPw, email, contact);
            case "Manager"    -> new Manager(id, name, hashedPw, email, contact);
            default           -> new CounterStaff(id, name, hashedPw, email, contact);
        };

        allUsers.add(newUser);
        FileHandler.writeData("users.txt", toFileLine(newUser));
        return true;
    }

    public boolean updateStaff(String id, String name, String email, String contact,
                               String role, String newPassword) {
        loadUsers();
        for (User u : allUsers) {
            if (u.getId().equals(id)) {
                u.setName(name);
                u.setEmail(email);
                u.setContactNumber(contact);
                if (newPassword != null && !newPassword.isEmpty()) {
                    u.setPassword(PasswordHasher.hashPassword(newPassword));
                }
                saveAllUsers();
                return true;
            }
        }
        return false;
    }

    public boolean deleteStaff(String id) {
        loadUsers();
        boolean removed = allUsers.removeIf(u -> u.getId().equals(id));
        if (removed) saveAllUsers();
        return removed;
    }

    private void saveAllUsers() {
        ArrayList<String> lines = new ArrayList<>();
        for (User u : allUsers) {
            lines.add(toFileLine(u));
        }
        FileHandler.writeData("users.txt", lines);
    }

    private String toFileLine(User u) {
        return String.join("|", u.getId(), u.getPassword(), u.getName(),
                           u.getRole(), u.getEmail(), u.getContactNumber());
    }

    private String generateId(String role) {
        String prefix = switch (role) {
            case "Technician" -> "T";
            case "Manager"    -> "M";
            default           -> "C";
        };
        loadUsers();
        int max = allUsers.stream()
            .filter(u -> u.getId().startsWith(prefix))
            .mapToInt(u -> {
                try { return Integer.parseInt(u.getId().substring(1)); }
                catch (NumberFormatException e) { return 0; }
            })
            .max().orElse(100);
        return prefix + String.format("%03d", max + 1);
    }
}
