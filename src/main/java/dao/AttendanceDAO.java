package dao;

import model.Attendance;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    
    public List<Attendance> getAttendanceByMonth(int employeeId, String workMonth) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE employee_id = ? AND work_month = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, workMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Attendance(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("work_month"),
                        rs.getString("work_date"),
                        rs.getString("attendance_val")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean saveAttendance(Attendance att) {
        String sqlCheck = "SELECT id FROM attendance WHERE employee_id = ? AND work_date = ?";
        String sqlInsert = "INSERT INTO attendance (employee_id, work_month, work_date, attendance_val) VALUES (?, ?, ?, ?)";
        String sqlUpdate = "UPDATE attendance SET attendance_val = ? WHERE employee_id = ? AND work_date = ?";
        
        try (Connection conn = DatabaseManager.getConnection()) {
            boolean exists = false;
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setInt(1, att.getEmployeeId());
                pstmtCheck.setString(2, att.getWorkDate());
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            }
            
            if (exists) {
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setString(1, att.getAttendanceVal());
                    pstmtUpdate.setInt(2, att.getEmployeeId());
                    pstmtUpdate.setString(3, att.getWorkDate());
                    return pstmtUpdate.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                    pstmtInsert.setInt(1, att.getEmployeeId());
                    pstmtInsert.setString(2, att.getWorkMonth());
                    pstmtInsert.setString(3, att.getWorkDate());
                    pstmtInsert.setString(4, att.getAttendanceVal());
                    return pstmtInsert.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public double getActualWorkDays(int employeeId, String workMonth) {
        String sql = "SELECT attendance_val FROM attendance WHERE employee_id = ? AND work_month = ?";
        double total = 0;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, workMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String val = rs.getString("attendance_val");
                    if ("1".equals(val)) {
                        total += 1.0;
                    } else if ("0.5".equals(val)) {
                        total += 0.5;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
