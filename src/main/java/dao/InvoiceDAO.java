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
        boolean hasCustomDate = invoice.getCreatedAt() != null && !invoice.getCreatedAt().trim().isEmpty();
        String sql = hasCustomDate ?
            "INSERT INTO invoices (customer_name, phone, license_plate, vehicle_type, address, total_before_discount, discount, total_amount, notes, status, payment_method, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" :
            "INSERT INTO invoices (customer_name, phone, license_plate, vehicle_type, address, total_before_discount, discount, total_amount, notes, status, payment_method) " +
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
            if (hasCustomDate) {
                pstmt.setString(12, invoice.getCreatedAt().trim());
            }
            
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
        boolean hasCustomDate = invoice.getCreatedAt() != null && !invoice.getCreatedAt().trim().isEmpty();
        String sql = hasCustomDate ?
            "UPDATE invoices SET customer_name = ?, phone = ?, license_plate = ?, vehicle_type = ?, address = ?, " +
            "total_before_discount = ?, discount = ?, total_amount = ?, notes = ?, status = ?, payment_method = ?, created_at = ? WHERE id = ?" :
            "UPDATE invoices SET customer_name = ?, phone = ?, license_plate = ?, vehicle_type = ?, address = ?, " +
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
            if (hasCustomDate) {
                pstmt.setString(12, invoice.getCreatedAt().trim());
                pstmt.setInt(13, invoice.getId());
            } else {
                pstmt.setInt(12, invoice.getId());
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteInvoice(int id) {
        String selectItemsSql = "SELECT item_id, item_name, quantity FROM invoice_items WHERE invoice_id = ? AND item_type = 'product'";
        String restoreStockByIdSql = "UPDATE products SET stock = stock + ?, status = CASE WHEN (stock + ?) > 0 THEN 'Còn hàng' ELSE status END WHERE id = ?";
        String restoreStockByNameSql = "UPDATE products SET stock = stock + ?, status = CASE WHEN (stock + ?) > 0 THEN 'Còn hàng' ELSE status END WHERE name = ?";
        String deleteSql = "DELETE FROM invoices WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                // 1. Tự động hoàn trả tồn kho cho tất cả sản phẩm trong hóa đơn
                try (PreparedStatement selectStmt = conn.prepareStatement(selectItemsSql);
                     PreparedStatement restoreByIdStmt = conn.prepareStatement(restoreStockByIdSql);
                     PreparedStatement restoreByNameStmt = conn.prepareStatement(restoreStockByNameSql)) {
                    
                    selectStmt.setInt(1, id);
                    try (ResultSet rs = selectStmt.executeQuery()) {
                        while (rs.next()) {
                            int prId = rs.getInt("item_id");
                            boolean hasPrId = !rs.wasNull() && prId > 0;
                            String prName = rs.getString("item_name");
                            double qty = rs.getDouble("quantity");
                            
                            if (qty > 0) {
                                if (hasPrId) {
                                    restoreByIdStmt.setDouble(1, qty);
                                    restoreByIdStmt.setDouble(2, qty);
                                    restoreByIdStmt.setInt(3, prId);
                                    restoreByIdStmt.addBatch();
                                } else if (prName != null && !prName.trim().isEmpty()) {
                                    restoreByNameStmt.setDouble(1, qty);
                                    restoreByNameStmt.setDouble(2, qty);
                                    restoreByNameStmt.setString(3, prName.trim());
                                    restoreByNameStmt.addBatch();
                                }
                            }
                        }
                    }
                    restoreByIdStmt.executeBatch();
                    restoreByNameStmt.executeBatch();
                }

                // 2. Xóa hóa đơn (cascade sẽ tự động xóa invoice_items)
                boolean deleted = false;
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, id);
                    deleted = deleteStmt.executeUpdate() > 0;
                }

                if (deleted) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM invoices")) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name = 'invoices'");
                        }
                    } catch (SQLException ignored) {}
                }

                conn.commit();
                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Invoice getLatestInvoiceByLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) return null;
        String rawPlate = licensePlate.trim().toUpperCase();
        String cleanPlate = rawPlate.replaceAll("[^A-Za-z0-9]", "").toLowerCase();

        String sql = "SELECT * FROM invoices ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String dbPlate = rs.getString("license_plate");
                if (dbPlate != null) {
                    dbPlate = dbPlate.trim();
                    // 1. So sánh chính xác
                    if (dbPlate.equalsIgnoreCase(rawPlate)) {
                        return mapResultSetToInvoice(rs);
                    }
                    // 2. So sánh thông minh không dấu gạch
                    String dbClean = dbPlate.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    if (!cleanPlate.isEmpty() && dbClean.equals(cleanPlate)) {
                        return mapResultSetToInvoice(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
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
}
