package controllers;

import java.util.ArrayList;
import services.FileHandler;

public class AuthController {
    private static final String USER_FILE = "users.txt";

    public static String[] login(String userId, String password) {
        ArrayList<String> users = FileHandler.readData(USER_FILE);
        for (String line : users) {
            String[] details = line.split("\\|");
            // Check if ID and Password match
            if (details[0].equals(userId) && details[1].equals(password)) {
                return details; // Returns [id, pass, name, role]
            }
        }
        return null; // Login failed
    }
}