package model;

public class Package {
    private int id;
    private String name;
    private String description;
    private double priceMini;
    private double priceSedan;
    private double priceCuv;
    private double priceSuv;
    private double priceMpv;
    private double pricePickup;
    private double savings;
    private String status;
    
    // Keep old field for backward compatibility
    @Deprecated
    private double price;

    public Package() {}

    // New constructor with 6 vehicle types
    public Package(int id, String name, String description, double priceMini, double priceSedan,
                   double priceCuv, double priceSuv, double priceMpv, double pricePickup, double savings, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceMini = priceMini;
        this.priceSedan = priceSedan;
        this.priceCuv = priceCuv;
        this.priceSuv = priceSuv;
        this.priceMpv = priceMpv;
        this.pricePickup = pricePickup;
        this.savings = savings;
        this.status = status;
    }

    // Old constructor for backward compatibility
    @Deprecated
    public Package(int id, String name, String description, double price, double savings, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.savings = savings;
        this.status = status;
        // Map old value to new structure
        this.priceMini = price * 0.8;
        this.priceSedan = price;
        this.priceCuv = price * 1.5;
        this.priceSuv = price * 2;
        this.priceMpv = price * 2.1;
        this.pricePickup = price * 2.2;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPriceMini() { return priceMini; }
    public void setPriceMini(double priceMini) { this.priceMini = priceMini; }

    public double getPriceSedan() { return priceSedan; }
    public void setPriceSedan(double priceSedan) { this.priceSedan = priceSedan; }

    public double getPriceCuv() { return priceCuv; }
    public void setPriceCuv(double priceCuv) { this.priceCuv = priceCuv; }

    public double getPriceSuv() { return priceSuv; }
    public void setPriceSuv(double priceSuv) { this.priceSuv = priceSuv; }

    public double getPriceMpv() { return priceMpv; }
    public void setPriceMpv(double priceMpv) { this.priceMpv = priceMpv; }

    public double getPricePickup() { return pricePickup; }
    public void setPricePickup(double pricePickup) { this.pricePickup = pricePickup; }

    public double getSavings() { return savings; }
    public void setSavings(double savings) { this.savings = savings; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Old getter for backward compatibility
    @Deprecated
    public double getPrice() { return priceSedan; }
    @Deprecated
    public void setPrice(double price) { this.priceSedan = price; }
    
    // Helper method to get price by vehicle type
    public double getPriceByVehicleType(String vehicleType) {
        if (vehicleType == null) return priceSedan;
        switch (vehicleType.toUpperCase()) {
            case "MINI": return priceMini;
            case "SEDAN": return priceSedan;
            case "CUV": return priceCuv;
            case "SUV": return priceSuv;
            case "MPV": return priceMpv;
            case "PICKUP": return pricePickup;
            default: return priceSedan;
        }
    }
}
