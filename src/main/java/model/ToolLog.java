package model;

public class ToolLog {
    private int id;
    private int toolId;
    private String toolName;
    private String actionType; // 'NHAP_MUA', 'BAO_HONG', 'THANH_LY'
    private double quantity;
    private double unitPrice;
    private double totalPrice;
    private String employeeName;
    private String logDate;
    private String reason;
    private String notes;
    private String createdAt;

    public ToolLog() {}

    public ToolLog(int id, int toolId, String toolName, String actionType,
                   double quantity, double unitPrice, double totalPrice,
                   String employeeName, String logDate, String reason,
                   String notes, String createdAt) {
        this.id = id;
        this.toolId = toolId;
        this.toolName = toolName;
        this.actionType = actionType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.employeeName = employeeName;
        this.logDate = logDate;
        this.reason = reason;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getToolId() { return toolId; }
    public void setToolId(int toolId) { this.toolId = toolId; }

    public String getToolName() { return toolName; }
    public void setToolName(String toolName) { this.toolName = toolName; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getLogDate() { return logDate; }
    public void setLogDate(String logDate) { this.logDate = logDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getActionTypeDisplay() {
        if ("NHAP_MUA".equalsIgnoreCase(actionType)) {
            return "📥 Nhập mua";
        } else if ("BAO_HONG".equalsIgnoreCase(actionType)) {
            return "⚠️ Báo hỏng/hao hụt";
        } else if ("THANH_LY".equalsIgnoreCase(actionType)) {
            return "🗑 Thanh lý";
        }
        return actionType;
    }
}

