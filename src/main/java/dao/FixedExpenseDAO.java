package dao;

import model.FixedExpense;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FixedExpenseDAO {

    private FixedExpense mapResultSetToExpense(ResultSet rs) throws SQLException {
        return new FixedExpense(
            rs.getInt("id"),
            rs.getString("expense_name"),
            rs.getString("category"),
            rs.getDouble("amount"),
            rs.getString("expense_month"),
            rs.getString("notes"),
            rs.getString("created_at")
        );
    }

    public List<FixedExpense> getAllExpensesByMonth(String expenseMonth) {
        List<FixedExpense> list = new ArrayList<>();
        String sql = "SELECT * FROM fixed_expenses WHERE expense_month = ? ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, expenseMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExpense(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public FixedExpense getExpenseById(int id) {
        String sql = "SELECT * FROM fixed_expenses WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExpense(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addExpense(FixedExpense exp) {
        String sql = "INSERT INTO fixed_expenses (expense_name, category, amount, expense_month, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, exp.getExpenseName());
            pstmt.setString(2, exp.getCategory());
            pstmt.setDouble(3, exp.getAmount());
            pstmt.setString(4, exp.getExpenseMonth());
            pstmt.setString(5, exp.getNotes());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateExpense(FixedExpense exp) {
        String sql = "UPDATE fixed_expenses SET expense_name = ?, category = ?, amount = ?, expense_month = ?, notes = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, exp.getExpenseName());
            pstmt.setString(2, exp.getCategory());
            pstmt.setDouble(3, exp.getAmount());
            pstmt.setString(4, exp.getExpenseMonth());
            pstmt.setString(5, exp.getNotes());
            pstmt.setInt(6, exp.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteExpense(int id) {
        String sql = "DELETE FROM fixed_expenses WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            boolean deleted = pstmt.executeUpdate() > 0;
            if (deleted) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM fixed_expenses")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name = 'fixed_expenses'");
                    }
                } catch (SQLException ignored) {}
            }
            return deleted;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteExpenseByReceiptCode(String receiptCode) {
        String sql = "DELETE FROM fixed_expenses WHERE notes LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + receiptCode + "%");
            boolean deleted = pstmt.executeUpdate() > 0;
            if (deleted) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM fixed_expenses")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name = 'fixed_expenses'");
                    }
                } catch (SQLException ignored) {}
            }
            return deleted;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật chi phí biến thiên tương ứng khi sửa phiếu nhập kho.
     * Tìm theo chuỗi "NK-XXXX" trong trường notes.
     */
    public boolean updateExpenseByReceiptCode(String receiptCode, String newExpenseName, double newAmount, String newMonth, String newNotes) {
        String sql = "UPDATE fixed_expenses SET expense_name = ?, amount = ?, expense_month = ?, notes = ? WHERE notes LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newExpenseName);
            pstmt.setDouble(2, newAmount);
            pstmt.setString(3, newMonth);
            pstmt.setString(4, newNotes);
            pstmt.setString(5, "%" + receiptCode + "%");
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
