package dao;

import model.Invoice;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Invoice invoice = new Invoice(
                    rs.getInt("id"),
                    rs.getString("customer_name"),
                    rs.getString("phone"),
                    rs.getString("license_plate"),
                    rs.getString("vehicle_type"),
                    rs.getString("address"),
                    rs.getDouble("total_before_discount"),
                    rs.getDouble("discount"),
                    rs.getDouble("total_amount"),
                    rs.getString("notes"),
                    rs.getString("status"),
                    rs.getString("created_at"),
                    rs.getString("payment_method")
                );
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }
    
    public Invoice getInvoiceById(int id) {
        String sql = "SELECT * FROM invoices WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Invoice(
                    rs.getInt("id"),
                    rs.getString("customer_name"),
                    rs.getString("phone"),
                    rs.getString("license_plate"),
                    rs.getString("vehicle_type"),
                    rs.getString("address"),
                    rs.getDouble("total_before_discount"),
                    rs.getDouble("discount"),
                    rs.getDouble("total_amount"),
                    rs.getString("notes"),
                    rs.getString("status"),
                    rs.getString("created_at"),
                    rs.getString("payment_method")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int addInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (customer_name, phone, license_plate, vehicle_type, address, total_before_discount, discount, total_amount, notes, status, payment_method) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, invoice.getCustomerName());
            pstmt.setString(2, invoice.getPhone());
            pstmt.setString(3, invoice.getLicensePlate());
            pstmt.setString(4, invoice.getVehicleType());
            pstmt.setString(5, invoice.getAddress());
            pstmt.setDouble(6, invoice.getTotalBeforeDiscount());
            pstmt.setDouble(7, invoice.getDiscount());
            pstmt.setDouble(8, invoice.getTotalAmount());
            pstmt.setString(9, invoice.getNotes());
            pstmt.setString(10, invoice.getStatus() != null ? invoice.getStatus() : "nhap");
            pstmt.setString(11, invoice.getPaymentMethod());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE invoices SET customer_name = ?, phone = ?, license_plate = ?, vehicle_type = ?, address = ?, " +
                     "total_before_discount = ?, discount = ?, total_amount = ?, notes = ?, status = ?, payment_method = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, invoice.getCustomerName());
            pstmt.setString(2, invoice.getPhone());
            pstmt.setString(3, invoice.getLicensePlate());
            pstmt.setString(4, invoice.getVehicleType());
            pstmt.setString(5, invoice.getAddress());
            pstmt.setDouble(6, invoice.getTotalBeforeDiscount());
            pstmt.setDouble(7, invoice.getDiscount());
            pstmt.setDouble(8, invoice.getTotalAmount());
            pstmt.setString(9, invoice.getNotes());
            pstmt.setString(10, invoice.getStatus());
            pstmt.setString(11, invoice.getPaymentMethod());
            pstmt.setInt(12, invoice.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteInvoice(int id) {
        String sql = "DELETE FROM invoices WHERE id = ?";
        
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
