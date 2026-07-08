package model;

public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private double costPrice;
    private double stock;
    private String unit;
    private String status;
    private int minStock;

    public Product() {}

    public Product(int id, String name, String category, double price, double costPrice, double stock, String unit, String status, int minStock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
        this.unit = unit;
        this.status = status;
        this.minStock = minStock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }

    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = stock; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMinStock() { return minStock; }
    public void setMinStock(int minStock) { this.minStock = minStock; }
}
