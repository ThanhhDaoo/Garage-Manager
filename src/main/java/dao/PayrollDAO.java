package dao;

import model.Payroll;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {
    
    public Payroll getPayroll(int employeeId, String payMonth) {
        String sql = "SELECT * FROM payroll WHERE employee_id = ? AND pay_month = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, payMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Payroll(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("pay_month"),
                        rs.getInt("total_days"),
                        rs.getDouble("actual_work_days"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("allowance_responsibility"),
                        rs.getDouble("allowance_other"),
                        rs.getDouble("commission_consulting"),
                        rs.getDouble("commission_service"),
                        rs.getDouble("overtime_pay"),
                        rs.getDouble("social_insurance"),
                        rs.getDouble("advance_payment"),
                        rs.getDouble("net_salary"),
                        rs.getString("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean savePayroll(Payroll pr) {
        String sqlCheck = "SELECT id FROM payroll WHERE employee_id = ? AND pay_month = ?";
        String sqlInsert = "INSERT INTO payroll (employee_id, pay_month, total_days, actual_work_days, basic_salary, " +
                           "allowance_responsibility, allowance_other, commission_consulting, commission_service, " +
                           "overtime_pay, social_insurance, advance_payment, net_salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE payroll SET total_days = ?, actual_work_days = ?, basic_salary = ?, " +
                           "allowance_responsibility = ?, allowance_other = ?, commission_consulting = ?, commission_service = ?, " +
                           "overtime_pay = ?, social_insurance = ?, advance_payment = ?, net_salary = ? WHERE employee_id = ? AND pay_month = ?";
        
        try (Connection conn = DatabaseManager.getConnection()) {
            boolean exists = false;
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setInt(1, pr.getEmployeeId());
                pstmtCheck.setString(2, pr.getPayMonth());
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            }
            
            if (exists) {
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setInt(1, pr.getTotalDays());
                    pstmtUpdate.setDouble(2, pr.getActualWorkDays());
                    pstmtUpdate.setDouble(3, pr.getBasicSalary());
                    pstmtUpdate.setDouble(4, pr.getAllowanceResponsibility());
                    pstmtUpdate.setDouble(5, pr.getAllowanceOther());
                    pstmtUpdate.setDouble(6, pr.getCommissionConsulting());
                    pstmtUpdate.setDouble(7, pr.getCommissionService());
                    pstmtUpdate.setDouble(8, pr.getOvertimePay());
                    pstmtUpdate.setDouble(9, pr.getSocialInsurance());
                    pstmtUpdate.setDouble(10, pr.getAdvancePayment());
                    pstmtUpdate.setDouble(11, pr.getNetSalary());
                    pstmtUpdate.setInt(12, pr.getEmployeeId());
                    pstmtUpdate.setString(13, pr.getPayMonth());
                    return pstmtUpdate.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                    pstmtInsert.setInt(1, pr.getEmployeeId());
                    pstmtInsert.setString(2, pr.getPayMonth());
                    pstmtInsert.setInt(3, pr.getTotalDays());
                    pstmtInsert.setDouble(4, pr.getActualWorkDays());
                    pstmtInsert.setDouble(5, pr.getBasicSalary());
                    pstmtInsert.setDouble(6, pr.getAllowanceResponsibility());
                    pstmtInsert.setDouble(7, pr.getAllowanceOther());
                    pstmtInsert.setDouble(8, pr.getCommissionConsulting());
                    pstmtInsert.setDouble(9, pr.getCommissionService());
                    pstmtInsert.setDouble(10, pr.getOvertimePay());
                    pstmtInsert.setDouble(11, pr.getSocialInsurance());
                    pstmtInsert.setDouble(12, pr.getAdvancePayment());
                    pstmtInsert.setDouble(13, pr.getNetSalary());
                    return pstmtInsert.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Payroll> getAllPayrollsByMonth(String payMonth) {
        List<Payroll> list = new ArrayList<>();
        String sql = "SELECT * FROM payroll WHERE pay_month = ? ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, payMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Payroll(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("pay_month"),
                        rs.getInt("total_days"),
                        rs.getDouble("actual_work_days"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("allowance_responsibility"),
                        rs.getDouble("allowance_other"),
                        rs.getDouble("commission_consulting"),
                        rs.getDouble("commission_service"),
                        rs.getDouble("overtime_pay"),
                        rs.getDouble("social_insurance"),
                        rs.getDouble("advance_payment"),
                        rs.getDouble("net_salary"),
                        rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean deletePayroll(int employeeId, String payMonth) {
        String sql = "DELETE FROM payroll WHERE employee_id = ? AND pay_month = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, payMonth);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
