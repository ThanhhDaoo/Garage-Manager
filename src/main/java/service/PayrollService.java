package service;

import dao.PayrollDAO;
import model.Payroll;
import java.util.List;

public class PayrollService {
    private final PayrollDAO payrollDAO = new PayrollDAO();
    
    public Payroll getPayroll(int employeeId, String payMonth) {
        return payrollDAO.getPayroll(employeeId, payMonth);
    }
    
    public boolean savePayroll(Payroll pr) {
        return payrollDAO.savePayroll(pr);
    }
    
    public List<Payroll> getAllPayrollsByMonth(String payMonth) {
        return payrollDAO.getAllPayrollsByMonth(payMonth);
    }
    
    public boolean deletePayroll(int employeeId, String payMonth) {
        return payrollDAO.deletePayroll(employeeId, payMonth);
    }
}
