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
}
