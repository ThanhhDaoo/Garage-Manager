package dao;

import model.InventoryReceipt;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryReceiptDAO {

    public int addReceipt(InventoryReceipt receipt) {
        String sql = "INSERT INTO inventory_receipts (product_id, product_name, quantity, cost_price, total_price, receipt_date, provider, notes, operator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, receipt.getProductId());
            pstmt.setString(2, receipt.getProductName());
            pstmt.setDouble(3, receipt.getQuantity());
            pstmt.setDouble(4, receipt.getCostPrice());
            pstmt.setDouble(5, receipt.getTotalPrice());
            pstmt.setString(6, receipt.getReceiptDate());
            pstmt.setString(7, receipt.getProvider());
            pstmt.setString(8, receipt.getNotes());
            pstmt.setString(9, receipt.getOperator());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<InventoryReceipt> getReceiptsByMonth(String monthStr) {
        List<InventoryReceipt> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory_receipts WHERE receipt_date LIKE ? ORDER BY receipt_date DESC, id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, monthStr + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InventoryReceipt r = new InventoryReceipt(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("quantity"),
                        rs.getDouble("cost_price"),
                        rs.getDouble("total_price"),
                        rs.getString("receipt_date"),
                        rs.getString("provider"),
                        rs.getString("notes"),
                        rs.getString("operator"),
                        rs.getString("created_at")
                    );
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public java.util.Map<Integer, Double> getImportedQuantitiesByMonth(String monthStr) {
        java.util.Map<Integer, Double> map = new java.util.HashMap<>();
        String sql = "SELECT product_id, SUM(quantity) FROM inventory_receipts WHERE substr(receipt_date, 1, 7) = ? GROUP BY product_id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, monthStr);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt(1), rs.getDouble(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
    public boolean deleteReceipt(int id) {
        String sql = "DELETE FROM inventory_receipts WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReceipt(InventoryReceipt receipt) {
        String sql = "UPDATE inventory_receipts SET quantity=?, cost_price=?, total_price=?, receipt_date=?, notes=?, operator=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, receipt.getQuantity());
            pstmt.setDouble(2, receipt.getCostPrice());
            pstmt.setDouble(3, receipt.getTotalPrice());
            pstmt.setString(4, receipt.getReceiptDate());
            pstmt.setString(5, receipt.getNotes());
            pstmt.setString(6, receipt.getOperator());
            pstmt.setInt(7, receipt.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public InventoryReceipt getReceiptById(int id) {
        String sql = "SELECT * FROM inventory_receipts WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new InventoryReceipt(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("quantity"),
                        rs.getDouble("cost_price"),
                        rs.getDouble("total_price"),
                        rs.getString("receipt_date"),
                        rs.getString("provider"),
                        rs.getString("notes"),
                        rs.getString("operator"),
                        rs.getString("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void revertStock(int productId, double qty) {
        String sql = "UPDATE products SET stock = MAX(0, stock - ?) WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, qty);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
