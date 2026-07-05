package model;

public class Attendance {
    private int id;
    private int employeeId;
    private String workMonth;
    private String workDate;
    private String attendanceVal;

    public Attendance() {}

    public Attendance(int id, int employeeId, String workMonth, String workDate, String attendanceVal) {
        this.id = id;
        this.employeeId = employeeId;
        this.workMonth = workMonth;
        this.workDate = workDate;
        this.attendanceVal = attendanceVal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getWorkMonth() { return workMonth; }
    public void setWorkMonth(String workMonth) { this.workMonth = workMonth; }

    public String getWorkDate() { return workDate; }
    public void setWorkDate(String workDate) { this.workDate = workDate; }

    public String getAttendanceVal() { return attendanceVal; }
    public void setAttendanceVal(String attendanceVal) { this.attendanceVal = attendanceVal; }
}
