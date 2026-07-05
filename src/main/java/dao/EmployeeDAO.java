package dao;

import model.Employee;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY id DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new Employee(
                    rs.getInt("id"),
                    rs.getString("employee_code"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("dob"),
                    rs.getString("gender"),
                    rs.getString("start_date"),
                    rs.getString("position"),
                    rs.getDouble("basic_salary")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getInt("id"),
                        rs.getString("employee_code"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("start_date"),
                        rs.getString("position"),
                        rs.getDouble("basic_salary")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Employee getEmployeeByCode(String code) {
        String sql = "SELECT * FROM employees WHERE employee_code = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getInt("id"),
                        rs.getString("employee_code"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("start_date"),
                        rs.getString("position"),
                        rs.getDouble("basic_salary")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addEmployee(Employee emp) {
        String sql = "INSERT INTO employees (employee_code, name, phone, address, dob, gender, start_date, position, basic_salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, emp.getEmployeeCode());
            pstmt.setString(2, emp.getName());
            pstmt.setString(3, emp.getPhone());
            pstmt.setString(4, emp.getAddress());
            pstmt.setString(5, emp.getDob());
            pstmt.setString(6, emp.getGender());
            pstmt.setString(7, emp.getStartDate());
            pstmt.setString(8, emp.getPosition());
            pstmt.setDouble(9, emp.getBasicSalary());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET employee_code = ?, name = ?, phone = ?, address = ?, dob = ?, gender = ?, start_date = ?, position = ?, basic_salary = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, emp.getEmployeeCode());
            pstmt.setString(2, emp.getName());
            pstmt.setString(3, emp.getPhone());
            pstmt.setString(4, emp.getAddress());
            pstmt.setString(5, emp.getDob());
            pstmt.setString(6, emp.getGender());
            pstmt.setString(7, emp.getStartDate());
            pstmt.setString(8, emp.getPosition());
            pstmt.setDouble(9, emp.getBasicSalary());
            pstmt.setInt(10, emp.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
