package dao;

import model.Tool;
import model.ToolLog;
import model.FixedExpense;
import util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToolDAO {

    private Tool mapResultSetToTool(ResultSet rs) throws SQLException {
        return new Tool(
                rs.getInt("id"),
                rs.getString("tool_code"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("unit"),
                rs.getDouble("total_qty"),
                rs.getDouble("damaged_qty"),
                rs.getDouble("usable_qty"),
                rs.getDouble("min_qty"),
                rs.getDouble("cost_price"),
                rs.getString("status"),
                rs.getString("notes"),
                rs.getString("created_at")
        );
    }

    private ToolLog mapResultSetToToolLog(ResultSet rs) throws SQLException {
        return new ToolLog(
                rs.getInt("id"),
                rs.getInt("tool_id"),
                rs.getString("tool_name"),
                rs.getString("action_type"),
                rs.getDouble("quantity"),
                rs.getDouble("unit_price"),
                rs.getDouble("total_price"),
                rs.getString("employee_name"),
                rs.getString("log_date"),
                rs.getString("reason"),
                rs.getString("notes"),
                rs.getString("created_at")
        );
    }

    public List<Tool> getAllTools() {
        List<Tool> list = new ArrayList<>();
        String sql = "SELECT * FROM tools ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToTool(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Tool getToolById(int id) {
        String sql = "SELECT * FROM tools WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTool(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateToolCode() {
        String sql = "SELECT MAX(id) FROM tools";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int nextId = rs.getInt(1) + 1;
                return String.format("DC-%03d", nextId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "DC-001";
    }

    public boolean addTool(Tool tool) {
        String sql = "INSERT INTO tools (tool_code, name, category, unit, total_qty, damaged_qty, usable_qty, min_qty, cost_price, status, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tool.getToolCode());
            ps.setString(2, tool.getName());
            ps.setString(3, tool.getCategory() != null ? tool.getCategory() : "Khác");
            ps.setString(4, tool.getUnit() != null ? tool.getUnit() : "Cái");
            ps.setDouble(5, tool.getTotalQty());
            ps.setDouble(6, tool.getDamagedQty());
            ps.setDouble(7, tool.getUsableQty());
            ps.setDouble(8, tool.getMinQty());
            ps.setDouble(9, tool.getCostPrice());
            ps.setString(10, tool.getStatus() != null ? tool.getStatus() : "Đang sử dụng");
            ps.setString(11, tool.getNotes());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tool.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTool(Tool tool) {
        String sql = "UPDATE tools SET tool_code = ?, name = ?, category = ?, unit = ?, min_qty = ?, cost_price = ?, status = ?, notes = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tool.getToolCode());
            ps.setString(2, tool.getName());
            ps.setString(3, tool.getCategory());
            ps.setString(4, tool.getUnit());
            ps.setDouble(5, tool.getMinQty());
            ps.setDouble(6, tool.getCostPrice());
            ps.setString(7, tool.getStatus());
            ps.setString(8, tool.getNotes());
            ps.setInt(9, tool.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTool(int id) {
        String sql = "DELETE FROM tools WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Ghi nhận nhập mua thêm dụng cụ:
     * 1. Thêm vào nhật ký tool_logs (action_type = 'NHAP_MUA')
     * 2. Tăng số lượng trong bảng tools (total_qty và usable_qty)
     * 3. Tự động thêm một khoản vào bảng fixed_expenses với category = 'biến thiên'
     */
    public boolean recordPurchase(int toolId, double quantity, double unitPrice, String logDate, String notes) {
        Tool tool = getToolById(toolId);
        if (tool == null) return false;

        double totalPrice = quantity * unitPrice;
        String insertLogSql = "INSERT INTO tool_logs (tool_id, tool_name, action_type, quantity, unit_price, total_price, log_date, notes) " +
                "VALUES (?, ?, 'NHAP_MUA', ?, ?, ?, ?, ?)";

        String updateToolSql = "UPDATE tools SET total_qty = total_qty + ?, usable_qty = usable_qty + ?, cost_price = ? WHERE id = ?";

        String insertExpSql = "INSERT INTO fixed_expenses (expense_name, category, amount, expense_month, notes, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                int logId = -1;

                try (PreparedStatement psLog = conn.prepareStatement(insertLogSql, Statement.RETURN_GENERATED_KEYS)) {
                    psLog.setInt(1, tool.getId());
                    psLog.setString(2, tool.getName());
                    psLog.setDouble(3, quantity);
                    psLog.setDouble(4, unitPrice);
                    psLog.setDouble(5, totalPrice);
                    psLog.setString(6, logDate);
                    psLog.setString(7, notes != null ? notes : "");
                    psLog.executeUpdate();

                    try (ResultSet keys = psLog.getGeneratedKeys()) {
                        if (keys.next()) {
                            logId = keys.getInt(1);
                        }
                    }
                }

                try (PreparedStatement psTool = conn.prepareStatement(updateToolSql)) {
                    psTool.setDouble(1, quantity);
                    psTool.setDouble(2, quantity);
                    psTool.setDouble(3, unitPrice > 0 ? unitPrice : tool.getCostPrice());
                    psTool.setInt(4, tool.getId());
                    psTool.executeUpdate();
                }

                // Đồng bộ sang chi phí biến thiên nếu có giá trị > 0
                if (totalPrice > 0 && logId > 0) {
                    String expMonth = logDate != null && logDate.length() >= 7 ? logDate.substring(0, 7) : java.time.LocalDate.now().toString().substring(0, 7);
                    String expName = "Mua dụng cụ: " + tool.getName() + " (SL: " + new java.text.DecimalFormat("#.##").format(quantity) + " " + (tool.getUnit() != null ? tool.getUnit() : "cái") + ")";
                    String expNotes = "Mã phiếu dụng cụ: DC-LOG-" + String.format("%04d", logId);
                    String expCreatedAt = (logDate != null && logDate.length() >= 10) ? logDate.substring(0, 10) + " 12:00:00" : logDate;

                    try (PreparedStatement psExp = conn.prepareStatement(insertExpSql)) {
                        psExp.setString(1, expName);
                        psExp.setString(2, "biến thiên");
                        psExp.setDouble(3, totalPrice);
                        psExp.setString(4, expMonth);
                        psExp.setString(5, expNotes);
                        psExp.setString(6, expCreatedAt);
                        psExp.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Ghi nhận báo hỏng / hao hụt dụng cụ:
     * 1. Thêm vào nhật ký tool_logs (action_type = 'BAO_HONG')
     * 2. Tăng số hỏng damaged_qty và giảm số còn tốt usable_qty trong bảng tools
     */
    public boolean recordDamage(int toolId, double quantity, String employeeName, String logDate, String reason, String notes) {
        Tool tool = getToolById(toolId);
        if (tool == null) return false;

        String insertLogSql = "INSERT INTO tool_logs (tool_id, tool_name, action_type, quantity, unit_price, total_price, employee_name, log_date, reason, notes) " +
                "VALUES (?, ?, 'BAO_HONG', ?, 0, 0, ?, ?, ?, ?)";

        String updateToolSql = "UPDATE tools SET damaged_qty = damaged_qty + ?, usable_qty = MAX(0, usable_qty - ?) WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psLog = conn.prepareStatement(insertLogSql)) {
                    psLog.setInt(1, tool.getId());
                    psLog.setString(2, tool.getName());
                    psLog.setDouble(3, quantity);
                    psLog.setString(4, employeeName != null ? employeeName : "");
                    psLog.setString(5, logDate);
                    psLog.setString(6, reason != null ? reason : "");
                    psLog.setString(7, notes != null ? notes : "");
                    psLog.executeUpdate();
                }

                try (PreparedStatement psTool = conn.prepareStatement(updateToolSql)) {
                    psTool.setDouble(1, quantity);
                    psTool.setDouble(2, quantity);
                    psTool.setInt(3, tool.getId());
                    psTool.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ToolLog> getLogsByMonth(String monthStr) {
        List<ToolLog> list = new ArrayList<>();
        String sql = (monthStr == null || monthStr.trim().isEmpty() || "Tất cả".equalsIgnoreCase(monthStr))
                ? "SELECT * FROM tool_logs ORDER BY log_date DESC, id DESC"
                : "SELECT * FROM tool_logs WHERE log_date LIKE ? ORDER BY log_date DESC, id DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (sql.contains("?")) {
                ps.setString(1, monthStr + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToToolLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteToolLog(int logId) {
        String selectSql = "SELECT * FROM tool_logs WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                ToolLog log = null;
                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setInt(1, logId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            log = mapResultSetToToolLog(rs);
                        }
                    }
                }

                if (log != null) {
                    if ("NHAP_MUA".equalsIgnoreCase(log.getActionType())) {
                        String revertToolSql = "UPDATE tools SET total_qty = MAX(0, total_qty - ?), usable_qty = MAX(0, usable_qty - ?) WHERE id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(revertToolSql)) {
                            ps.setDouble(1, log.getQuantity());
                            ps.setDouble(2, log.getQuantity());
                            ps.setInt(3, log.getToolId());
                            ps.executeUpdate();
                        }
                        String noteCode = "Mã phiếu dụng cụ: DC-LOG-" + String.format("%04d", log.getId());
                        String delExpSql = "DELETE FROM fixed_expenses WHERE notes LIKE ?";
                        try (PreparedStatement psExp = conn.prepareStatement(delExpSql)) {
                            psExp.setString(1, "%" + noteCode + "%");
                            psExp.executeUpdate();
                        }
                    } else if ("BAO_HONG".equalsIgnoreCase(log.getActionType())) {
                        String revertToolSql = "UPDATE tools SET damaged_qty = MAX(0, damaged_qty - ?), usable_qty = usable_qty + ? WHERE id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(revertToolSql)) {
                            ps.setDouble(1, log.getQuantity());
                            ps.setDouble(2, log.getQuantity());
                            ps.setInt(3, log.getToolId());
                            ps.executeUpdate();
                        }
                    }

                    String deleteLogSql = "DELETE FROM tool_logs WHERE id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteLogSql)) {
                        ps.setInt(1, logId);
                        ps.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

