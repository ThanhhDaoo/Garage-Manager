package service;

import dao.EmployeeDAO;
import model.Employee;
import java.util.List;

public class EmployeeService {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }
    
    public Employee getEmployeeById(int id) {
        return employeeDAO.getEmployeeById(id);
    }
    
    public Employee getEmployeeByCode(String code) {
        return employeeDAO.getEmployeeByCode(code);
    }
    
    public boolean addEmployee(Employee emp) {
        // Validation: employee code must be unique
        if (employeeDAO.getEmployeeByCode(emp.getEmployeeCode()) != null) {
            return false;
        }
        return employeeDAO.addEmployee(emp);
    }
    
    public boolean updateEmployee(Employee emp) {
        Employee existing = employeeDAO.getEmployeeByCode(emp.getEmployeeCode());
        if (existing != null && existing.getId() != emp.getId()) {
            return false; // Code belongs to another employee
        }
        return employeeDAO.updateEmployee(emp);
    }
    
    public boolean deleteEmployee(int id) {
        return employeeDAO.deleteEmployee(id);
    }
}
