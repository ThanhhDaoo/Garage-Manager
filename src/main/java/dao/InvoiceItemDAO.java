package dao;

import model.InvoiceItem;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO {
    
    public List<InvoiceItem> getItemsByInvoiceId(int invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT * FROM invoice_items WHERE invoice_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, invoiceId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int itemIdVal = rs.getInt("item_id");
                Integer itemId = rs.wasNull() ? null : itemIdVal;
                InvoiceItem item = new InvoiceItem(
                    rs.getInt("id"),
                    rs.getInt("invoice_id"),
                    rs.getString("item_type"),
                    rs.getString("item_name"),
                    rs.getDouble("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("total_price"),
                    itemId,
                    rs.getString("category"),
                    rs.getDouble("cost_price"),
                    rs.getInt("is_hidden")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public boolean addInvoiceItem(InvoiceItem item) {
        String sql = "INSERT INTO invoice_items (invoice_id, item_type, item_name, quantity, unit_price, total_price, item_id, category, cost_price, is_hidden) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, item.getInvoiceId());
            pstmt.setString(2, item.getItemType());
            pstmt.setString(3, item.getItemName());
            pstmt.setDouble(4, item.getQuantity());
            pstmt.setDouble(5, item.getUnitPrice());
            pstmt.setDouble(6, item.getTotalPrice());
            if (item.getItemId() != null && item.getItemId() > 0) {
                pstmt.setInt(7, item.getItemId());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }
            pstmt.setString(8, item.getCategory());
            pstmt.setDouble(9, item.getCostPrice());
            pstmt.setInt(10, item.getIsHidden());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteItemsByInvoiceId(int invoiceId) {
        String sql = "DELETE FROM invoice_items WHERE invoice_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
