package dao;

import util.DatabaseManager;
import model.Attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttendanceDAO {

    public Attendance getAttendanceByMonth(int employeeId, String workMonth) {
        String sql = "SELECT * FROM attendance WHERE employee_id = ? AND work_month = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, workMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Attendance(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("work_month"),
                        rs.getString("attendance_data")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveAttendance(Attendance att) {
        String checkSql = "SELECT id FROM attendance WHERE employee_id = ? AND work_month = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, att.getEmployeeId());
            checkStmt.setString(2, att.getWorkMonth());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Update
                    String updateSql = "UPDATE attendance SET employee_name = ?, attendance_data = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, att.getEmployeeName());
                        updateStmt.setString(2, att.getAttendanceData());
                        updateStmt.setInt(3, rs.getInt("id"));
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // Insert
                    String insertSql = "INSERT INTO attendance (employee_id, employee_name, work_month, attendance_data) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, att.getEmployeeId());
                        insertStmt.setString(2, att.getEmployeeName());
                        insertStmt.setString(3, att.getWorkMonth());
                        insertStmt.setString(4, att.getAttendanceData());
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public double getActualWorkDays(int employeeId, String workMonth) {
        Attendance att = getAttendanceByMonth(employeeId, workMonth);
        if (att == null || att.getAttendanceData() == null || att.getAttendanceData().trim().isEmpty()) {
            // Mặc định: Nếu chưa lưu chấm công, coi như đi làm đủ số ngày trong tháng
            try {
                int year = Integer.parseInt(workMonth.substring(0, 4));
                int month = Integer.parseInt(workMonth.substring(5, 7));
                return java.time.YearMonth.of(year, month).lengthOfMonth();
            } catch (Exception e) {
                return 26;
            }
        }
        
        String[] days = att.getAttendanceData().split(",");
        double total = 0;
        for (String val : days) {
            if ("1".equals(val) || "X".equals(val)) {
                total += 1.0;
            } else if ("0.5".equals(val) || "1/2".equals(val)) {
                total += 0.5;
            }
        }
        return total;
    }
}
