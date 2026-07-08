package model;

public class InvoiceItem {
    private int id;
    private int invoiceId;
    private String itemType;  // 'service', 'package', 'product'
    private String itemName;
    private double quantity;
    private double unitPrice;
    private double totalPrice;
    private Integer itemId;
    private String category;
    private double costPrice;

    public InvoiceItem() {}

    // Constructor with defaults
    public InvoiceItem(int id, int invoiceId, String itemType, String itemName, 
                       double quantity, double unitPrice, double totalPrice) {
        this(id, invoiceId, itemType, itemName, quantity, unitPrice, totalPrice, null, null, 0.0);
    }

    // Main constructor
    public InvoiceItem(int id, int invoiceId, String itemType, String itemName, 
                       double quantity, double unitPrice, double totalPrice, Integer itemId, String category, double costPrice) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.itemId = itemId;
        this.category = category;
        this.costPrice = costPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
}
