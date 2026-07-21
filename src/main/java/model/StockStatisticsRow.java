package model;

public class StockStatisticsRow {
    private int productId;
    private String productCode;
    private String productName;
    private String unit;
    private double costPrice;
    private double price;
    private double importedQty;
    private double importedValue;
    private double currentStock;
    private double currentStockValue;

    public StockStatisticsRow(int productId, String productCode, String productName, String unit,
                              double costPrice, double price, double importedQty, double currentStock) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
        this.costPrice = costPrice;
        this.price = price;
        this.importedQty = importedQty;
        this.importedValue = importedQty * costPrice;
        this.currentStock = currentStock;
        this.currentStockValue = currentStock * costPrice;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getImportedQty() { return importedQty; }
    public void setImportedQty(double importedQty) {
        this.importedQty = importedQty;
        this.importedValue = importedQty * this.costPrice;
    }

    public double getImportedValue() { return importedValue; }

    public double getCurrentStock() { return currentStock; }
    public void setCurrentStock(double currentStock) {
        this.currentStock = currentStock;
        this.currentStockValue = currentStock * this.costPrice;
    }

    public double getCurrentStockValue() { return currentStockValue; }
}
