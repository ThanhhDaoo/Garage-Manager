package service;

import dao.AttendanceDAO;
import model.Attendance;
import java.util.List;

public class AttendanceService {
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    
    public List<Attendance> getAttendanceByMonth(int employeeId, String workMonth) {
        return attendanceDAO.getAttendanceByMonth(employeeId, workMonth);
    }
    
    public boolean saveAttendance(Attendance att) {
        return attendanceDAO.saveAttendance(att);
    }
    
    public double getActualWorkDays(int employeeId, String workMonth) {
        return attendanceDAO.getActualWorkDays(employeeId, workMonth);
    }
}
