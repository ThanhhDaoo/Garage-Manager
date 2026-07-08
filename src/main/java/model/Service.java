package model;

public class Service {
    private int id;
    private String name;
    private String description;
    private double priceMini;
    private double priceSedan;
    private double priceCuv;
    private double priceSuv;
    private double priceMpv;
    private double pricePickup;
    private String category;
    private double costPrice;
    private Integer linkedProductId;
    
    // Keep old fields for backward compatibility
    @Deprecated
    private double priceSmall;
    @Deprecated
    private double priceLarge;

    public Service() {}

    // Constructor with 6 vehicle types
    public Service(int id, String name, String description, double priceMini, double priceSedan, 
                   double priceCuv, double priceSuv, double priceMpv, double pricePickup) {
        this(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, "rửa xe", 0.0, null);
    }

    // Constructor with 6 vehicle types + category + costPrice
    public Service(int id, String name, String description, double priceMini, double priceSedan, 
                   double priceCuv, double priceSuv, double priceMpv, double pricePickup, String category, double costPrice) {
        this(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, category, costPrice, null);
    }

    // Main constructor with all fields
    public Service(int id, String name, String description, double priceMini, double priceSedan, 
                   double priceCuv, double priceSuv, double priceMpv, double pricePickup, String category, double costPrice, Integer linkedProductId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceMini = priceMini;
        this.priceSedan = priceSedan;
        this.priceCuv = priceCuv;
        this.priceSuv = priceSuv;
        this.priceMpv = priceMpv;
        this.pricePickup = pricePickup;
        this.category = category;
        this.costPrice = costPrice;
        this.linkedProductId = linkedProductId;
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
        this.priceMpv = priceLarge * 1.05;
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

    public double getPriceMpv() { return priceMpv; }
    public void setPriceMpv(double priceMpv) { this.priceMpv = priceMpv; }

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
            case "MPV": return priceMpv;
            case "PICKUP": return pricePickup;
            default: return priceSedan;
        }
    }

    public String getCategory() {
        return category != null ? category : "rửa xe";
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public double getCostPrice() {
        return costPrice;
    }
    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getLinkedProductId() {
        return linkedProductId;
    }
    public void setLinkedProductId(Integer linkedProductId) {
        this.linkedProductId = linkedProductId;
    }
}
