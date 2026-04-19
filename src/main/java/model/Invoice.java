package model;

public class Invoice {
    private int id;
    private String customerName;
    private String phone;
    private String licensePlate;
    private String vehicleType;
    private double totalBeforeDiscount;
    private double discount;
    private double totalAmount;
    private String notes;
    private String status;
    private String createdAt;

    public Invoice() {}

    public Invoice(int id, String customerName, String phone, String licensePlate, String vehicleType,
                   double totalBeforeDiscount, double discount, double totalAmount, String notes, String status, String createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.phone = phone;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.totalBeforeDiscount = totalBeforeDiscount;
        this.discount = discount;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public double getTotalBeforeDiscount() { return totalBeforeDiscount; }
    public void setTotalBeforeDiscount(double totalBeforeDiscount) { this.totalBeforeDiscount = totalBeforeDiscount; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
