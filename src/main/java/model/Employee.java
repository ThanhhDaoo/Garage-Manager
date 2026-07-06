package model;

public class Employee {
    private int id;
    private String employeeCode;
    private String name;
    private String phone;
    private String address;
    private String dob;
    private String gender;
    private String startDate;
    private String position;
    private double basicSalary;
    private double allowanceResponsibility;
    private double allowanceOther;
    private double commissionConsulting;
    private double commissionService;
    private double overtimePay;
    private double socialInsurance;
    private double advancePayment;
    private double netSalary;

    public Employee() {}

    public Employee(int id, String employeeCode, String name, String phone, String address, String dob, String gender, String startDate, String position, double basicSalary) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
        this.startDate = startDate;
        this.position = position;
        this.basicSalary = basicSalary;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

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
}
