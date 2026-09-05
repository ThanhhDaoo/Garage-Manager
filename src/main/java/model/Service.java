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
    private double costPriceMini;
    private double costPriceSedan;
    private double costPriceCuv;
    private double costPriceSuv;
    private double costPriceMpv;
    private double costPricePickup;
    private Integer linkedProductId;
    private Double linkedProductQty;
    
    // Keep old fields for backward compatibility
    @Deprecated
    private double priceSmall;
    @Deprecated
    private double priceLarge;

    public Service() {}

    // Constructor with 6 vehicle types
    public Service(int id, String name, String description, double priceMini, double priceSedan, 
                   double priceCuv, double priceSuv, double priceMpv, double pricePickup) {
        this(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, "rửa xe", 0.0, null, 0.0);
    }

    // Constructor with 6 vehicle types + category + costPrice
    public Service(int id, String name, String description, double priceMini, double priceSedan, 
                   double priceCuv, double priceSuv, double priceMpv, double pricePickup, String category, double costPrice) {
        this(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, category, costPrice, null, 0.0);
    }

    // Constructor with costPrice per vehicle type
    public Service(int id, String name, String description, double priceMini, double priceSedan, 
                   double priceCuv, double priceSuv, double priceMpv, double pricePickup, 
                   double costPriceMini, double costPriceSedan, double costPriceCuv, double costPriceSuv, double costPriceMpv, double costPricePickup,
                   String category, double costPrice, Integer linkedProductId, Double linkedProductQty) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceMini = priceMini;
        this.priceSedan = priceSedan;
        this.priceCuv = priceCuv;
        this.priceSuv = priceSuv;
        this.priceMpv = priceMpv;
        this.pricePickup = pricePickup;
        this.costPriceMini = costPriceMini;
        this.costPriceSedan = costPriceSedan;
        this.costPriceCuv = costPriceCuv;
        this.costPriceSuv = costPriceSuv;
        this.costPriceMpv = costPriceMpv;
        this.costPricePickup = costPricePickup;
        this.category = category;
        this.costPrice = costPrice;
        this.linkedProductId = linkedProductId;
        this.linkedProductQty = linkedProductQty != null ? linkedProductQty : 0.0;
    }

    // Constructor with single costPrice (defaults costPrice<Type> to costPrice)
    public Service(int id, String name, String description, double priceMini, double priceSedan, 
                   double priceCuv, double priceSuv, double priceMpv, double pricePickup, String category, double costPrice, Integer linkedProductId, Double linkedProductQty) {
        this(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup,
             costPrice, costPrice, costPrice, costPrice, costPrice, costPrice,
             category, costPrice, linkedProductId, linkedProductQty);
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

    public double getCostPriceMini() { return costPriceMini; }
    public void setCostPriceMini(double costPriceMini) { this.costPriceMini = costPriceMini; }

    public double getCostPriceSedan() { return costPriceSedan; }
    public void setCostPriceSedan(double costPriceSedan) { this.costPriceSedan = costPriceSedan; }

    public double getCostPriceCuv() { return costPriceCuv; }
    public void setCostPriceCuv(double costPriceCuv) { this.costPriceCuv = costPriceCuv; }

    public double getCostPriceSuv() { return costPriceSuv; }
    public void setCostPriceSuv(double costPriceSuv) { this.costPriceSuv = costPriceSuv; }

    public double getCostPriceMpv() { return costPriceMpv; }
    public void setCostPriceMpv(double costPriceMpv) { this.costPriceMpv = costPriceMpv; }

    public double getCostPricePickup() { return costPricePickup; }
    public void setCostPricePickup(double costPricePickup) { this.costPricePickup = costPricePickup; }

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

    // Helper method to get cost price by vehicle type
    public double getCostPriceByVehicleType(String vehicleType) {
        boolean hasAnyVehicleCost = costPriceMini > 0 || costPriceSedan > 0 || costPriceCuv > 0 || costPriceSuv > 0 || costPriceMpv > 0 || costPricePickup > 0;
        if (vehicleType == null) return costPriceSedan > 0 ? costPriceSedan : (hasAnyVehicleCost ? 0.0 : costPrice);
        switch (vehicleType.toUpperCase()) {
            case "MINI": return costPriceMini > 0 ? costPriceMini : (hasAnyVehicleCost ? 0.0 : costPrice);
            case "SEDAN": return costPriceSedan > 0 ? costPriceSedan : (hasAnyVehicleCost ? 0.0 : costPrice);
            case "CUV": return costPriceCuv > 0 ? costPriceCuv : (hasAnyVehicleCost ? 0.0 : costPrice);
            case "SUV": return costPriceSuv > 0 ? costPriceSuv : (hasAnyVehicleCost ? 0.0 : costPrice);
            case "MPV": return costPriceMpv > 0 ? costPriceMpv : (hasAnyVehicleCost ? 0.0 : costPrice);
            case "PICKUP": return costPricePickup > 0 ? costPricePickup : (hasAnyVehicleCost ? 0.0 : costPrice);
            default: return costPriceSedan > 0 ? costPriceSedan : (hasAnyVehicleCost ? 0.0 : costPrice);
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

    public Double getLinkedProductQty() {
        return linkedProductQty;
    }
    public void setLinkedProductQty(Double linkedProductQty) {
        this.linkedProductQty = linkedProductQty;
    }
}
