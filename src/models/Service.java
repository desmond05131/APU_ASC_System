package models;

public class Service {
    private final String id;
    private final String name;
    private final String category;
    private final double price;

    public Service(String id, String name, String category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return String.join("|", id, name, category, String.valueOf(price));
    }
}