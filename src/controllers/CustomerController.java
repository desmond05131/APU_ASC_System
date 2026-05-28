package controllers;

import java.util.ArrayList;
import models.Customer;
import services.FileHandler;
import utils.PasswordHasher;

public class CustomerController {
    private static final String USER_FILE = "users.txt";

    public static ArrayList<Customer> getAllCustomers() {
        var result = new ArrayList<Customer>();
        for (var c : loadAll()) if (!c.isDeleted()) result.add(c);
        return result;
    }

    public static ArrayList<Customer> getAllCustomersIncludingDeleted() {
        return loadAll();
    }

    public static Customer findCustomerById(String id) {
        for (var c : loadAll()) if (c.getId().equals(id)) return c;
        return null;
    }

    /** Adds a new customer, returns the created Customer object or null on failure. */
    public static Customer addCustomer(String name, String email, String contact, String password) {
        var allLines = FileHandler.readData(USER_FILE);
        String id   = generateId(allLines);
        String hash = PasswordHasher.hashPassword(password);
        allLines.add(String.join("|", id, hash, name, "Customer", email, contact, "ACTIVE"));
        FileHandler.writeData(USER_FILE, allLines);
        Customer c = new Customer(id, name, hash, email, contact);
        return c;
    }

    /** Updates customer fields; newPassword may be null/empty to keep existing hash. */
    public static boolean updateCustomer(String id, String name, String email,
                                         String contact, String newPassword) {
        var allLines = FileHandler.readData(USER_FILE);
        for (int i = 0; i < allLines.size(); i++) {
            String[] p = allLines.get(i).split("\\|");
            if (p.length >= 4 && p[0].equals(id) && "Customer".equals(p[3])) {
                String pw     = (newPassword != null && !newPassword.isEmpty())
                        ? PasswordHasher.hashPassword(newPassword) : (p.length > 1 ? p[1] : "");
                String status = p.length > 6 ? p[6] : "ACTIVE";
                allLines.set(i, String.join("|", id, pw, name, "Customer", email, contact, status));
                FileHandler.writeData(USER_FILE, allLines);
                return true;
            }
        }
        return false;
    }

    /** Soft delete - sets status to DELETED. */
    public static boolean deleteCustomer(String id) {
        var allLines = FileHandler.readData(USER_FILE);
        for (int i = 0; i < allLines.size(); i++) {
            String[] p = allLines.get(i).split("\\|");
            if (p.length >= 4 && p[0].equals(id) && "Customer".equals(p[3])
                    && !"DELETED".equals(p.length > 6 ? p[6] : "ACTIVE")) {
                allLines.set(i, String.join("|", p[0], p[1], p[2], p[3],
                        p.length > 4 ? p[4] : "",
                        p.length > 5 ? p[5] : "",
                        "DELETED"));
                FileHandler.writeData(USER_FILE, allLines);
                return true;
            }
        }
        return false;
    }

    /** Case-insensitive substring match on name or ID (active only). */
    public static ArrayList<Customer> searchCustomers(String keyword) {
        String q = keyword == null ? "" : keyword.toLowerCase();
        var result = new ArrayList<Customer>();
        for (var c : getAllCustomers()) {
            if (c.getName().toLowerCase().contains(q) || c.getId().toLowerCase().contains(q))
                result.add(c);
        }
        return result;
    }

    /** Two-field search used by the filter section (active only). */
    public static ArrayList<Customer> searchCustomers(String nameQ, String idQ) {
        var result = new ArrayList<Customer>();
        for (var c : getAllCustomers()) {
            boolean mn = nameQ.isEmpty() || c.getName().toLowerCase().contains(nameQ.toLowerCase());
            boolean mi = idQ.isEmpty()   || c.getId().toLowerCase().contains(idQ.toLowerCase());
            if (mn && mi) result.add(c);
        }
        return result;
    }

    private static ArrayList<Customer> loadAll() {
        var result = new ArrayList<Customer>();
        for (String line : FileHandler.readData(USER_FILE)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 4 || !"Customer".equals(p[3])) continue;
            String email   = p.length > 4 ? p[4] : "";
            String contact = p.length > 5 ? p[5] : "";
            boolean deleted = p.length > 6 && "DELETED".equals(p[6]);
            Customer c = new Customer(p[0], p[2], p[1], email, contact);
            c.setDeleted(deleted);
            result.add(c);
        }
        return result;
    }

    private static String generateId(ArrayList<String> allLines) {
        int max = 0;
        for (String line : allLines) {
            String[] p = line.split("\\|");
            if (p.length >= 4 && "Customer".equals(p[3]) && p[0].startsWith("CU")) {
                try { max = Math.max(max, Integer.parseInt(p[0].substring(2))); }
                catch (NumberFormatException ignored) {}
            }
        }
        return String.format("CU%03d", max + 1);
    }
}
