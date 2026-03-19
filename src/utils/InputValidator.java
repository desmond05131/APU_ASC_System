package utils;

import java.util.regex.Pattern;

public class InputValidator {
    // Regex patterns for validation
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_PATTERN = "^[0-9]{3}-[0-9]{3,4} [0-9]{4}$"; // Matches 012-345 6789

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return Pattern.compile(PHONE_PATTERN).matcher(phone).matches();
    }

    public static boolean isNumeric(String input) {
        if (input == null) return false;
        return input.matches("-?\\d+(\\.\\d+)?");
    }
}