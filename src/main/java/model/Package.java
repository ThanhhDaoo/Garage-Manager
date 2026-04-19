package model;

public class Package {
    private int id;
    private String name;
    private String description;
    private double price;
    private double savings;
    private String status;

    public Package() {}

    public Package(int id, String name, String description, double price, double savings, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.savings = savings;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getSavings() { return savings; }
    public void setSavings(double savings) { this.savings = savings; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
