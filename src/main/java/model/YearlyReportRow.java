package model;

public class YearlyReportRow {
    private String month; // "Tháng 1", "Tháng 2", ... or "TỔNG CỘNG"
    private double revenueWash;
    private double revenueCare;
    private double revenueAccessory;
    private double revenuePaint;
    private double totalRevenue;
    private double profitCare;
    private double profitAccessory;
    private double profitPaint;
    private double variableCost;
    private double fixedCost;
    private double totalNetProfit;

    public YearlyReportRow() {}

    public YearlyReportRow(String month, double revenueWash, double revenueCare, double revenueAccessory, double revenuePaint,
                           double totalRevenue, double profitCare, double profitAccessory, double profitPaint,
                           double variableCost, double fixedCost, double totalNetProfit) {
        this.month = month;
        this.revenueWash = revenueWash;
        this.revenueCare = revenueCare;
        this.revenueAccessory = revenueAccessory;
        this.revenuePaint = revenuePaint;
        this.totalRevenue = totalRevenue;
        this.profitCare = profitCare;
        this.profitAccessory = profitAccessory;
        this.profitPaint = profitPaint;
        this.variableCost = variableCost;
        this.fixedCost = fixedCost;
        this.totalNetProfit = totalNetProfit;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

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

    public double getProfitCare() { return profitCare; }
    public void setProfitCare(double profitCare) { this.profitCare = profitCare; }

    public double getProfitAccessory() { return profitAccessory; }
    public void setProfitAccessory(double profitAccessory) { this.profitAccessory = profitAccessory; }

    public double getProfitPaint() { return profitPaint; }
    public void setProfitPaint(double profitPaint) { this.profitPaint = profitPaint; }

    public double getVariableCost() { return variableCost; }
    public void setVariableCost(double variableCost) { this.variableCost = variableCost; }

    public double getFixedCost() { return fixedCost; }
    public void setFixedCost(double fixedCost) { this.fixedCost = fixedCost; }

    public double getTotalNetProfit() { return totalNetProfit; }
    public void setTotalNetProfit(double totalNetProfit) { this.totalNetProfit = totalNetProfit; }
}
