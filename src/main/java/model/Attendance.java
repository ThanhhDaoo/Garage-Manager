package model;

public class Attendance {
    private int id;
    private int employeeId;
    private String employeeName;
    private String workMonth;
    private String attendanceData; // Dạng chuỗi: "1,1,0.5,N,..."

    public Attendance() {}

    public Attendance(int id, int employeeId, String employeeName, String workMonth, String attendanceData) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.workMonth = workMonth;
        this.attendanceData = attendanceData;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getWorkMonth() { return workMonth; }
    public void setWorkMonth(String workMonth) { this.workMonth = workMonth; }

    public String getAttendanceData() { return attendanceData; }
    public void setAttendanceData(String attendanceData) { this.attendanceData = attendanceData; }
}
