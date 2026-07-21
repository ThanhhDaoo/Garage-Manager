package model;

public class InventoryReceipt {
    private int id;
    private int productId;
    private String productName;
    private double quantity;
    private double costPrice;
    private double totalPrice;
    private String receiptDate;
    private String provider;
    private String notes;
    private String operator;
    private String createdAt;

    public InventoryReceipt() {}

    public InventoryReceipt(int id, int productId, String productName, double quantity, double costPrice, 
                            double totalPrice, String receiptDate, String provider, String notes, 
                            String operator, String createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.totalPrice = totalPrice;
        this.receiptDate = receiptDate;
        this.provider = provider;
        this.notes = notes;
        this.operator = operator;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getReceiptDate() { return receiptDate; }
    public void setReceiptDate(String receiptDate) { this.receiptDate = receiptDate; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
