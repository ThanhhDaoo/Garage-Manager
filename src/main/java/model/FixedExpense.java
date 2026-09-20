package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FixedExpense {
    private int id;
    private String expenseName;
    private String category;
    private double amount;
    private String expenseMonth; // Format: YYYY-MM
    private String notes;
    private String createdAt;    // Format: YYYY-MM-DD HH:MM:SS hoặc YYYY-MM-DD

    public FixedExpense() {}

    public FixedExpense(int id, String expenseName, String category, double amount, String expenseMonth, String notes, String createdAt) {
        this.id = id;
        this.expenseName = expenseName;
        this.category = category;
        this.amount = amount;
        this.expenseMonth = expenseMonth;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getExpenseName() { return expenseName; }
    public void setExpenseName(String expenseName) { this.expenseName = expenseName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getExpenseMonth() { return expenseMonth; }
    public void setExpenseMonth(String expenseMonth) { this.expenseMonth = expenseMonth; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * Lấy ngày chi hiển thị rõ ràng theo định dạng dd/MM/yyyy
     * Dùng trường created_at có sẵn trong CSDL (không cần thay đổi cấu trúc bảng).
     */
    public String getFormattedDate() {
        if (createdAt != null && createdAt.trim().length() >= 10) {
            try {
                LocalDate d = LocalDate.parse(createdAt.trim().substring(0, 10));
                return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception ignored) {}
        }
        if (expenseMonth != null && expenseMonth.trim().length() >= 7) {
            try {
                return "01/" + expenseMonth.trim().substring(5, 7) + "/" + expenseMonth.trim().substring(0, 4);
            } catch (Exception ignored) {}
        }
        return "-";
    }
}
