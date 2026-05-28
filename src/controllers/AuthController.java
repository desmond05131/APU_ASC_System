package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import services.FileHandler;
import utils.PasswordHasher;

public class AuthController {
    private static final String USER_FILE    = "users.txt";
    private static final int    MAX_ATTEMPTS = 3;
    private static final long   LOCK_MS      = 30_000L;

    private static final Map<String, Integer> failures    = new HashMap<>();
    private static final Map<String, Long>    lockedUntil = new HashMap<>();

    /**
     * Returns user data array on success, a {"LOCKED", secondsRemaining} sentinel
     * if the account is temporarily locked, or null on bad credentials.
     */
    public static String[] login(String userId, String password) {
        // Check lockout
        Long until = lockedUntil.get(userId);
        if (until != null) {
            long remaining = (until - System.currentTimeMillis()) / 1000;
            if (remaining > 0) {
                return new String[]{"LOCKED", String.valueOf(remaining)};
            }
            // Lock expired - clear it
            lockedUntil.remove(userId);
            failures.remove(userId);
        }

        String hashedInput = PasswordHasher.hashPassword(password);
        ArrayList<String> users = FileHandler.readData(USER_FILE);

        for (String line : users) {
            String[] details = line.split("\\|");
            if (details.length >= 6
                    && details[0].equals(userId)
                    && details[1].equals(hashedInput)) {
                // Success - clear failure tracking
                failures.remove(userId);
                lockedUntil.remove(userId);
                return details;
            }
        }

        // Failed attempt
        int count = failures.getOrDefault(userId, 0) + 1;
        if (count >= MAX_ATTEMPTS) {
            lockedUntil.put(userId, System.currentTimeMillis() + LOCK_MS);
            failures.remove(userId);
        } else {
            failures.put(userId, count);
        }
        return null;
    }
}
