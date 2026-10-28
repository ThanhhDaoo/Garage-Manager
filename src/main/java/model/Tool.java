package model;

public class Tool {
    private int id;
    private String toolCode;
    private String name;
    private String category;
    private String unit;
    private double totalQty;
    private double damagedQty;
    private double usableQty;
    private double minQty;
    private double costPrice;
    private String status;
    private String notes;
    private String createdAt;

    public Tool() {}

    public Tool(int id, String toolCode, String name, String category, String unit,
                double totalQty, double damagedQty, double usableQty, double minQty,
                double costPrice, String status, String notes, String createdAt) {
        this.id = id;
        this.toolCode = toolCode;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.totalQty = totalQty;
        this.damagedQty = damagedQty;
        this.usableQty = usableQty;
        this.minQty = minQty;
        this.costPrice = costPrice;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getToolCode() { return toolCode; }
    public void setToolCode(String toolCode) { this.toolCode = toolCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getTotalQty() { return totalQty; }
    public void setTotalQty(double totalQty) { this.totalQty = totalQty; }

    public double getDamagedQty() { return damagedQty; }
    public void setDamagedQty(double damagedQty) { this.damagedQty = damagedQty; }

    public double getUsableQty() { return usableQty; }
    public void setUsableQty(double usableQty) { this.usableQty = usableQty; }

    public double getMinQty() { return minQty; }
    public void setMinQty(double minQty) { this.minQty = minQty; }

    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isLowStock() {
        return this.usableQty <= this.minQty;
    }
}

