package controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import models.Service;
import services.FileHandler;

public class ServiceController {
    private final ArrayList<Service> services = new ArrayList<>();
    private final String FILE_PATH = "src/database/services.txt";

    public ServiceController() {
        load();
    }

    private void load() {
        ArrayList<String> data = FileHandler.readData(FILE_PATH);
        services.clear();
        for (String line : data) {
            String[] p = line.split("\\|");
            if (p.length == 4) {
                services.add(new Service(p[0], p[1], p[2], Double.parseDouble(p[3])));
            }
        }
    }

    public void save() {
        ArrayList<String> data = new ArrayList<>();
        for (Service s : services) {
            data.add(s.toString());
        }
        FileHandler.writeData(FILE_PATH, data);
    }

    public ArrayList<Service> getAllServices() {
        return services;
    }

    public void addService(String name, String category, double price) {
        // Auto-generate ID: S101, S102...
        int nextId = services.isEmpty() ? 101 : 
                     Integer.parseInt(services.get(services.size() - 1).getId().substring(1)) + 1;
        String id = "S" + nextId;
        services.add(new Service(id, name, category, price));
        save();
    }

    public void updateService(String id, String name, String category, double price) {
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getId().equals(id)) {
                services.set(i, new Service(id, name, category, price));
                break;
            }
        }
        save();
    }

    public void deleteService(String id) {
        services.removeIf(s -> s.getId().equals(id));
        save();
    }

    public ArrayList<Service> searchServices(String query) {
        return services.stream()
            .filter(s -> s.getName().toLowerCase().contains(query.toLowerCase()) || 
                         s.getCategory().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}