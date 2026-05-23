package model;

public class Service {
    private int id;
    private String name;
    private String description;
    private double priceMini;
    private double priceSedan;
    private double priceCuv;
    private double priceSuv;
    private double pricePickup;
    
    // Keep old fields for backward compatibility
    @Deprecated
    private double priceSmall;
    @Deprecated
    private double priceLarge;

    public Service() {}

    // New constructor with 5 vehicle types
    public Service(int id, String name, String description, double priceMini, double priceSedan, 
                   double priceCuv, double priceSuv, double pricePickup) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceMini = priceMini;
        this.priceSedan = priceSedan;
        this.priceCuv = priceCuv;
        this.priceSuv = priceSuv;
        this.pricePickup = pricePickup;
    }

    // Old constructor for backward compatibility
    @Deprecated
    public Service(int id, String name, String description, double priceSmall, double priceLarge) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceSmall = priceSmall;
        this.priceLarge = priceLarge;
        // Map old values to new structure
        this.priceMini = priceSmall * 0.8;
        this.priceSedan = priceSmall;
        this.priceCuv = (priceSmall + priceLarge) / 2;
        this.priceSuv = priceLarge;
        this.pricePickup = priceLarge * 1.1;
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

    public double getPricePickup() { return pricePickup; }
    public void setPricePickup(double pricePickup) { this.pricePickup = pricePickup; }

    // Old getters for backward compatibility
    @Deprecated
    public double getPriceSmall() { return priceSedan; }
    @Deprecated
    public void setPriceSmall(double priceSmall) { this.priceSedan = priceSmall; }

    @Deprecated
    public double getPriceLarge() { return priceSuv; }
    @Deprecated
    public void setPriceLarge(double priceLarge) { this.priceSuv = priceLarge; }
    
    // Helper method to get price by vehicle type
    public double getPriceByVehicleType(String vehicleType) {
        if (vehicleType == null) return priceSedan;
        switch (vehicleType.toUpperCase()) {
            case "MINI": return priceMini;
            case "SEDAN": return priceSedan;
            case "CUV": return priceCuv;
            case "SUV": return priceSuv;
            case "PICKUP": return pricePickup;
            default: return priceSedan;
        }
    }
}
