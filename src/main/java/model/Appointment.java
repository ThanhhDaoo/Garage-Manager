package model;

public class Appointment {
    private int id;
    private String customerName;
    private String phone;
    private String address;
    private String licensePlate;
    private String vehicleType;
    private String serviceName;
    private String appointmentDate; // YYYY-MM-DD
    private String appointmentTime; // HH:MM
    private String expectedCompletion; // Duration or specific end time (e.g., "2 giờ" or "15:30")
    private String notes;
    private String status; // 'Chờ', 'Đang thực hiện', 'Đã hoàn thành', 'Đã hủy'
    private int reminded; // 0: False, 1: True
    private String createdAt;

    public Appointment() {}

    public Appointment(int id, String customerName, String phone, String address, String licensePlate,
                       String vehicleType, String serviceName, String appointmentDate, String appointmentTime,
                       String expectedCompletion, String notes, String status, int reminded, String createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.serviceName = serviceName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.expectedCompletion = expectedCompletion;
        this.notes = notes;
        this.status = status;
        this.reminded = reminded;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getExpectedCompletion() { return expectedCompletion; }
    public void setExpectedCompletion(String expectedCompletion) { this.expectedCompletion = expectedCompletion; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getReminded() { return reminded; }
    public void setReminded(int reminded) { this.reminded = reminded; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
