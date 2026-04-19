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
                    rs.getDouble("total_before_discount"),
                    rs.getDouble("discount"),
                    rs.getDouble("total_amount"),
                    rs.getString("notes"),
                    rs.getString("status"),
                    rs.getString("created_at")
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
                    rs.getDouble("total_before_discount"),
                    rs.getDouble("discount"),
                    rs.getDouble("total_amount"),
                    rs.getString("notes"),
                    rs.getString("status"),
                    rs.getString("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (customer_name, phone, license_plate, vehicle_type, total_before_discount, discount, total_amount, notes, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, invoice.getCustomerName());
            pstmt.setString(2, invoice.getPhone());
            pstmt.setString(3, invoice.getLicensePlate());
            pstmt.setString(4, invoice.getVehicleType());
            pstmt.setDouble(5, invoice.getTotalBeforeDiscount());
            pstmt.setDouble(6, invoice.getDiscount());
            pstmt.setDouble(7, invoice.getTotalAmount());
            pstmt.setString(8, invoice.getNotes());
            pstmt.setString(9, "nhap");
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE invoices SET customer_name = ?, phone = ?, license_plate = ?, vehicle_type = ?, " +
                     "total_before_discount = ?, discount = ?, total_amount = ?, notes = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, invoice.getCustomerName());
            pstmt.setString(2, invoice.getPhone());
            pstmt.setString(3, invoice.getLicensePlate());
            pstmt.setString(4, invoice.getVehicleType());
            pstmt.setDouble(5, invoice.getTotalBeforeDiscount());
            pstmt.setDouble(6, invoice.getDiscount());
            pstmt.setDouble(7, invoice.getTotalAmount());
            pstmt.setString(8, invoice.getNotes());
            pstmt.setString(9, invoice.getStatus());
            pstmt.setInt(10, invoice.getId());
            
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
