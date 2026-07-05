package model;

public class Payroll {
    private int id;
    private int employeeId;
    private String payMonth;
    private int totalDays;
    private double actualWorkDays;
    private double basicSalary;
    private double allowanceResponsibility;
    private double allowanceOther;
    private double commissionConsulting;
    private double commissionService;
    private double overtimePay;
    private double socialInsurance;
    private double advancePayment;
    private double netSalary;
    private String createdAt;

    public Payroll() {}

    public Payroll(int id, int employeeId, String payMonth, int totalDays, double actualWorkDays, double basicSalary,
                   double allowanceResponsibility, double allowanceOther, double commissionConsulting, double commissionService,
                   double overtimePay, double socialInsurance, double advancePayment, double netSalary, String createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.payMonth = payMonth;
        this.totalDays = totalDays;
        this.actualWorkDays = actualWorkDays;
        this.basicSalary = basicSalary;
        this.allowanceResponsibility = allowanceResponsibility;
        this.allowanceOther = allowanceOther;
        this.commissionConsulting = commissionConsulting;
        this.commissionService = commissionService;
        this.overtimePay = overtimePay;
        this.socialInsurance = socialInsurance;
        this.advancePayment = advancePayment;
        this.netSalary = netSalary;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getPayMonth() { return payMonth; }
    public void setPayMonth(String payMonth) { this.payMonth = payMonth; }

    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

    public double getActualWorkDays() { return actualWorkDays; }
    public void setActualWorkDays(double actualWorkDays) { this.actualWorkDays = actualWorkDays; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getAllowanceResponsibility() { return allowanceResponsibility; }
    public void setAllowanceResponsibility(double allowanceResponsibility) { this.allowanceResponsibility = allowanceResponsibility; }

    public double getAllowanceOther() { return allowanceOther; }
    public void setAllowanceOther(double allowanceOther) { this.allowanceOther = allowanceOther; }

    public double getCommissionConsulting() { return commissionConsulting; }
    public void setCommissionConsulting(double commissionConsulting) { this.commissionConsulting = commissionConsulting; }

    public double getCommissionService() { return commissionService; }
    public void setCommissionService(double commissionService) { this.commissionService = commissionService; }

    public double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(double overtimePay) { this.overtimePay = overtimePay; }

    public double getSocialInsurance() { return socialInsurance; }
    public void setSocialInsurance(double socialInsurance) { this.socialInsurance = socialInsurance; }

    public double getAdvancePayment() { return advancePayment; }
    public void setAdvancePayment(double advancePayment) { this.advancePayment = advancePayment; }

    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
