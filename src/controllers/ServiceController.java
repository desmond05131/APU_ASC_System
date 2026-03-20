package controllers;

import java.util.ArrayList;
import models.Service;
import services.FileHandler;

public class ServiceController {
    private final ArrayList<Service> services = new ArrayList<>();
    private final String FILE_PATH = "src/database/services.txt";

    public ServiceController() { load(); }

    private void load() {
        ArrayList<String> data = FileHandler.readData(FILE_PATH);
        services.clear();
        for (String line : data) {
            String[] p = line.split("\\|");
            if (p.length == 5) { // Updated for 5 fields
                services.add(new Service(p[0], p[1], p[2], p[3], Double.parseDouble(p[4])));
            }
        }
    }

    public void save() {
        ArrayList<String> data = new ArrayList<>();
        for (Service s : services) data.add(s.toString());
        FileHandler.writeData(FILE_PATH, data);
    }

    public ArrayList<Service> getAll() { return services; }

    public ArrayList<Service> getAllServices() { return services; }

    public void addService(String name, String category, double price) {
        addOrUpdateService(null, name, category, "", price);
    }

    public void updateService(String id, String name, String category, double price) {
        addOrUpdateService(id, name, category, "", price);
    }

    public ArrayList<Service> searchServices(String query) {
        ArrayList<Service> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Service s : services) {
            if (s.getName().toLowerCase().contains(lowerQuery) || 
                s.getCategory().toLowerCase().contains(lowerQuery) ||
                s.getId().toLowerCase().contains(lowerQuery)) {
                results.add(s);
            }
        }
        return results;
    }

    public void addOrUpdateService(String id, String name, String cat, String desc, double price) {
        if (id == null || id.isEmpty()) {
            // Add Logic: Auto-generate ID
            int nextNum = services.isEmpty() ? 101 : 
                          Integer.parseInt(services.get(services.size() - 1).getId().substring(1)) + 1;
            services.add(new Service("S" + nextNum, name, cat, desc, price));
        } else {
            // Update Logic
            for (Service s : services) {
                if (s.getId().equals(id)) {
                    s.setName(name);
                    s.setCategory(cat);
                    s.setDescription(desc);
                    s.setPrice(price);
                    break;
                }
            }
        }
        save();
    }

    public void deleteService(String id) {
        services.removeIf(s -> s.getId().equals(id));
        save();
    }
}