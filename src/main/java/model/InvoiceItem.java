package model;

public class InvoiceItem {
    private int id;
    private int invoiceId;
    private String itemType;  // 'service', 'package', 'product'
    private String itemName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public InvoiceItem() {}

    public InvoiceItem(int id, int invoiceId, String itemType, String itemName, 
                       int quantity, double unitPrice, double totalPrice) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
