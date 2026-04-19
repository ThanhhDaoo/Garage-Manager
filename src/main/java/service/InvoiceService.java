package service;

import dao.InvoiceDAO;
import model.Invoice;
import java.util.List;

public class InvoiceService {
    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    
    public List<Invoice> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }
    
    public Invoice getInvoiceById(int id) {
        return invoiceDAO.getInvoiceById(id);
    }
    
    public boolean addInvoice(String customerName, String phone, String licensePlate, String vehicleType,
                              double totalBeforeDiscount, double discount, double totalAmount, String notes) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return false;
        }
        Invoice invoice = new Invoice(0, customerName, phone, licensePlate, vehicleType,
                                     totalBeforeDiscount, discount, totalAmount, notes, "nhap", null);
        return invoiceDAO.addInvoice(invoice);
    }
    
    public boolean updateInvoice(int id, String customerName, String phone, String licensePlate, String vehicleType,
                                 double totalBeforeDiscount, double discount, double totalAmount, String notes, String status) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return false;
        }
        Invoice invoice = new Invoice(id, customerName, phone, licensePlate, vehicleType,
                                     totalBeforeDiscount, discount, totalAmount, notes, status, null);
        return invoiceDAO.updateInvoice(invoice);
    }
    
    public boolean deleteInvoice(int id) {
        return invoiceDAO.deleteInvoice(id);
    }
}
