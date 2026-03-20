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
            if (p.length == 4) services.add(new Service(p[0], p[1], p[2], Double.parseDouble(p[3])));
        }
    }

    public void save() {
        ArrayList<String> data = new ArrayList<>();
        for (Service s : services) data.add(s.toString());
        FileHandler.writeData(FILE_PATH, data);
    }

    public ArrayList<Service> getAll() { return services; }

    public void addService(String name, String cat, double price) {
        String id = "S" + (services.size() + 101);
        services.add(new Service(id, name, cat, price));
        save();
    }

    public void updateService(String id, String name, String cat, double price) {
        services.stream().filter(s -> s.getId().equals(id)).findFirst()
                .ifPresent(s -> {
                    services.set(services.indexOf(s), new Service(id, name, cat, price));
                    save();
                });
    }

    public void deleteService(String id) {
        services.removeIf(s -> s.getId().equals(id));
        save();
    }
}