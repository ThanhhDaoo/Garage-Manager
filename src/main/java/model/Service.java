package model;

public class Service {
    private int id;
    private String name;
    private String description;
    private double priceSmall;
    private double priceLarge;

    public Service() {}

    public Service(int id, String name, String description, double priceSmall, double priceLarge) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceSmall = priceSmall;
        this.priceLarge = priceLarge;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPriceSmall() { return priceSmall; }
    public void setPriceSmall(double priceSmall) { this.priceSmall = priceSmall; }

    public double getPriceLarge() { return priceLarge; }
    public void setPriceLarge(double priceLarge) { this.priceLarge = priceLarge; }
}
