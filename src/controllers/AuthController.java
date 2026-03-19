package controllers;

import java.util.ArrayList;
import services.FileHandler;
import utils.PasswordHasher;

public class AuthController {
    private static final String USER_FILE = "users.txt";

    /**
     * Validates credentials and returns all user details if successful.
     * Expects users.txt format: id|hashed_password|name|role|email|contact
     */
    public static String[] login(String userId, String password) {
        ArrayList<String> users = FileHandler.readData(USER_FILE);
        
        // Hash the incoming plain-text password to compare with the stored hash
        String hashedInput = PasswordHasher.hashPassword(password);

        for (String line : users) {
            String[] details = line.split("\\|");
            // Check if ID and Hashed Password match
            // details[0] = id, details[1] = hashed_password
            if (details.length >= 6 && details[0].equals(userId) && details[1].equals(hashedInput)) {
                return details; // Returns [id, pass_hash, name, role, email, contact]
            }
        }
        return null; // Login failed
    }
}