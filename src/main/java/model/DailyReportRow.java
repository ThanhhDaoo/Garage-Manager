package model;

public class DailyReportRow {
    private int stt;
    private String date;
    private String licensePlate;
    private String services;
    private double revenueWash;
    private double revenueCare;
    private double revenueAccessory;
    private double revenuePaint;
    private double totalRevenue;
    private double vat;
    private String paymentMethod;
    private double costWash;
    private double costCare;
    private double costAccessory;
    private double costPaint;
    private double profitWash;
    private double profitCare;
    private double profitAccessory;
    private double profitPaint;
    private String notes;

    public DailyReportRow() {}

    public DailyReportRow(int stt, String date, String licensePlate, String services, 
                          double revenueWash, double revenueCare, double revenueAccessory, double revenuePaint,
                          double totalRevenue, double vat, String paymentMethod, 
                          double costWash, double costCare, double costAccessory, double costPaint,
                          double profitWash, double profitCare, double profitAccessory, double profitPaint, String notes) {
        this.stt = stt;
        this.date = date;
        this.licensePlate = licensePlate;
        this.services = services;
        this.revenueWash = revenueWash;
        this.revenueCare = revenueCare;
        this.revenueAccessory = revenueAccessory;
        this.revenuePaint = revenuePaint;
        this.totalRevenue = totalRevenue;
        this.vat = vat;
        this.paymentMethod = paymentMethod;
        this.costWash = costWash;
        this.costCare = costCare;
        this.costAccessory = costAccessory;
        this.costPaint = costPaint;
        this.profitWash = profitWash;
        this.profitCare = profitCare;
        this.profitAccessory = profitAccessory;
        this.profitPaint = profitPaint;
        this.notes = notes;
    }

    public int getStt() { return stt; }
    public void setStt(int stt) { this.stt = stt; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }

    public double getRevenueWash() { return revenueWash; }
    public void setRevenueWash(double revenueWash) { this.revenueWash = revenueWash; }

    public double getRevenueCare() { return revenueCare; }
    public void setRevenueCare(double revenueCare) { this.revenueCare = revenueCare; }

    public double getRevenueAccessory() { return revenueAccessory; }
    public void setRevenueAccessory(double revenueAccessory) { this.revenueAccessory = revenueAccessory; }

    public double getRevenuePaint() { return revenuePaint; }
    public void setRevenuePaint(double revenuePaint) { this.revenuePaint = revenuePaint; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getVat() { return vat; }
    public void setVat(double vat) { this.vat = vat; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getCostWash() { return costWash; }
    public void setCostWash(double costWash) { this.costWash = costWash; }

    public double getCostCare() { return costCare; }
    public void setCostCare(double costCare) { this.costCare = costCare; }

    public double getCostAccessory() { return costAccessory; }
    public void setCostAccessory(double costAccessory) { this.costAccessory = costAccessory; }

    public double getCostPaint() { return costPaint; }
    public void setCostPaint(double costPaint) { this.costPaint = costPaint; }

    public double getProfitWash() { return profitWash; }
    public void setProfitWash(double profitWash) { this.profitWash = profitWash; }

    public double getProfitCare() { return profitCare; }
    public void setProfitCare(double profitCare) { this.profitCare = profitCare; }

    public double getProfitAccessory() { return profitAccessory; }
    public void setProfitAccessory(double profitAccessory) { this.profitAccessory = profitAccessory; }

    public double getProfitPaint() { return profitPaint; }
    public void setProfitPaint(double profitPaint) { this.profitPaint = profitPaint; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
