package controllers;

import java.util.ArrayList;
import models.Service;
import services.FileHandler;

public class ServiceController {
    private final ArrayList<Service> services = new ArrayList<>();

    public ServiceController() { load(); }

    private void load() {
        services.clear();
        for (String line : FileHandler.readData("services.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 5) continue;
            try {
                Service s = new Service(p[0], p[1], p[2], p[3], Double.parseDouble(p[4]));
                if (p.length >= 6 && "DELETED".equals(p[5])) s.setDeleted(true);
                services.add(s);
            } catch (NumberFormatException ignored) {}
        }
    }

    private void save() {
        ArrayList<String> lines = new ArrayList<>();
        for (Service s : services)
            lines.add(String.join("|", s.getId(), s.getName(), s.getCategory(),
                    s.getDescription(), String.valueOf(s.getPrice()),
                    s.isDeleted() ? "DELETED" : "ACTIVE"));
        FileHandler.writeData("services.txt", lines);
    }

    /** Returns only non-deleted services (reloads from file first). */
    public ArrayList<Service> getAllServices() {
        load();
        ArrayList<Service> result = new ArrayList<>();
        for (Service s : services) if (!s.isDeleted()) result.add(s);
        return result;
    }

    /** Returns all services including soft-deleted (reloads from file first). */
    public ArrayList<Service> getAllServicesIncludingDeleted() {
        load();
        return new ArrayList<>(services);
    }

    public ArrayList<Service> searchServices(String query) {
        String q = query.toLowerCase();
        ArrayList<Service> result = new ArrayList<>();
        for (Service s : services)
            if (!s.isDeleted() && (s.getName().toLowerCase().contains(q)
                    || s.getCategory().toLowerCase().contains(q)
                    || s.getId().toLowerCase().contains(q)))
                result.add(s);
        return result;
    }

    public void addOrUpdateService(String id, String name, String cat, String desc, double price) {
        if (id == null || id.isEmpty()) {
            int maxId = 100;
            for (Service s : services) {
                try { maxId = Math.max(maxId, Integer.parseInt(s.getId().substring(1))); }
                catch (NumberFormatException ignored) {}
            }
            services.add(new Service("S" + (maxId + 1), name, cat, desc, price));
        } else {
            for (Service s : services) {
                if (s.getId().equals(id)) {
                    s.setName(name); s.setCategory(cat);
                    s.setDescription(desc); s.setPrice(price);
                    break;
                }
            }
        }
        save();
    }

    /** Soft delete - marks DELETED rather than removing from file. */
    public void deleteService(String id) {
        for (Service s : services) {
            if (s.getId().equals(id)) { s.setDeleted(true); break; }
        }
        save();
    }
}
