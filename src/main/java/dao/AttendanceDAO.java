package dao;

import util.DatabaseManager;
import model.Attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                    Attendance att = new Attendance(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("work_month"),
                        rs.getString("work_date"),
                        rs.getString("attendance_val")
                    );
                    list.add(att);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean saveAttendance(Attendance att) {
        String checkSql = "SELECT id FROM attendance WHERE employee_id = ? AND work_date = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, att.getEmployeeId());
            checkStmt.setString(2, att.getWorkDate());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Update
                    String updateSql = "UPDATE attendance SET attendance_val = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, att.getAttendanceVal());
                        updateStmt.setInt(2, rs.getInt("id"));
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // Insert
                    String insertSql = "INSERT INTO attendance (employee_id, work_month, work_date, attendance_val) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, att.getEmployeeId());
                        insertStmt.setString(2, att.getWorkMonth());
                        insertStmt.setString(3, att.getWorkDate());
                        insertStmt.setString(4, att.getAttendanceVal());
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
        String sql = "SELECT attendance_val FROM attendance WHERE employee_id = ? AND work_month = ?";
        double total = 0;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, workMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String val = rs.getString("attendance_val");
                    if ("1".equals(val) || "X".equals(val)) {
                        total += 1.0;
                    } else if ("0.5".equals(val) || "1/2".equals(val)) {
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
