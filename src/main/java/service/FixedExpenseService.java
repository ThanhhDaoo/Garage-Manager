package service;

import dao.FixedExpenseDAO;
import model.FixedExpense;
import java.util.List;

public class FixedExpenseService {
    private final FixedExpenseDAO fixedExpenseDAO = new FixedExpenseDAO();

    public List<FixedExpense> getAllExpensesByMonth(String expenseMonth) {
        return fixedExpenseDAO.getAllExpensesByMonth(expenseMonth);
    }

    public FixedExpense getExpenseById(int id) {
        return fixedExpenseDAO.getExpenseById(id);
    }

    public boolean addExpense(FixedExpense exp) {
        return fixedExpenseDAO.addExpense(exp);
    }

    public boolean updateExpense(FixedExpense exp) {
        return fixedExpenseDAO.updateExpense(exp);
    }

    public boolean deleteExpense(int id) {
        return fixedExpenseDAO.deleteExpense(id);
    }
}
