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
        String sql = "SELECT * FROM fixed_expenses WHERE expense_month = ? ORDER BY created_at DESC, id DESC";
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
        boolean hasCreatedAt = exp.getCreatedAt() != null && !exp.getCreatedAt().trim().isEmpty();
        String sql = hasCreatedAt
            ? "INSERT INTO fixed_expenses (expense_name, category, amount, expense_month, notes, created_at) VALUES (?, ?, ?, ?, ?, ?)"
            : "INSERT INTO fixed_expenses (expense_name, category, amount, expense_month, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, exp.getExpenseName());
            pstmt.setString(2, exp.getCategory());
            pstmt.setDouble(3, exp.getAmount());
            pstmt.setString(4, exp.getExpenseMonth());
            pstmt.setString(5, exp.getNotes());
            if (hasCreatedAt) {
                pstmt.setString(6, exp.getCreatedAt().trim());
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateExpense(FixedExpense exp) {
        boolean hasCreatedAt = exp.getCreatedAt() != null && !exp.getCreatedAt().trim().isEmpty();
        String sql = hasCreatedAt
            ? "UPDATE fixed_expenses SET expense_name = ?, category = ?, amount = ?, expense_month = ?, notes = ?, created_at = ? WHERE id = ?"
            : "UPDATE fixed_expenses SET expense_name = ?, category = ?, amount = ?, expense_month = ?, notes = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, exp.getExpenseName());
            pstmt.setString(2, exp.getCategory());
            pstmt.setDouble(3, exp.getAmount());
            pstmt.setString(4, exp.getExpenseMonth());
            pstmt.setString(5, exp.getNotes());
            if (hasCreatedAt) {
                pstmt.setString(6, exp.getCreatedAt().trim());
                pstmt.setInt(7, exp.getId());
            } else {
                pstmt.setInt(6, exp.getId());
            }
            
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
    public boolean updateExpenseByReceiptCode(String receiptCode, String newExpenseName, double newAmount, String newMonth, String newNotes, String newCreatedAt) {
        boolean hasCreatedAt = newCreatedAt != null && !newCreatedAt.trim().isEmpty();
        String sql = hasCreatedAt
            ? "UPDATE fixed_expenses SET expense_name = ?, amount = ?, expense_month = ?, notes = ?, created_at = ? WHERE notes LIKE ?"
            : "UPDATE fixed_expenses SET expense_name = ?, amount = ?, expense_month = ?, notes = ? WHERE notes LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newExpenseName);
            pstmt.setDouble(2, newAmount);
            pstmt.setString(3, newMonth);
            pstmt.setString(4, newNotes);
            if (hasCreatedAt) {
                pstmt.setString(5, newCreatedAt.trim());
                pstmt.setString(6, "%" + receiptCode + "%");
            } else {
                pstmt.setString(5, "%" + receiptCode + "%");
            }
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateExpenseByReceiptCode(String receiptCode, String newExpenseName, double newAmount, String newMonth, String newNotes) {
        return updateExpenseByReceiptCode(receiptCode, newExpenseName, newAmount, newMonth, newNotes, null);
    }
}
