package utils;

public class InputValidator {
    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Checks if a string is numeric. 
     * Uses regex to avoid "Unnecessary temporary" warnings from unused parse results.
     */
    public static boolean isNumeric(String input) {
        if (input == null) return false;
        // Matches integers or decimals (optional leading sign)
        return input.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}