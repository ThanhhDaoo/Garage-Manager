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
    
    public int addInvoice(String customerName, String phone, String licensePlate, String vehicleType, String address,
                              double totalBeforeDiscount, double discount, double totalAmount, String notes) {
        return addInvoice(customerName, phone, licensePlate, vehicleType, address, totalBeforeDiscount, discount, totalAmount, notes, "nhap", null);
    }

    public int addInvoice(String customerName, String phone, String licensePlate, String vehicleType, String address,
                              double totalBeforeDiscount, double discount, double totalAmount, String notes, String status, String paymentMethod) {
        return addInvoice(customerName, phone, licensePlate, vehicleType, address, totalBeforeDiscount, discount, totalAmount, notes, status, paymentMethod, null);
    }

    public int addInvoice(String customerName, String phone, String licensePlate, String vehicleType, String address,
                              double totalBeforeDiscount, double discount, double totalAmount, String notes, String status, String paymentMethod, String createdAt) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return -1;
        }
        Invoice invoice = new Invoice(0, customerName, phone, licensePlate, vehicleType, address,
                                     totalBeforeDiscount, discount, totalAmount, notes, status, createdAt, paymentMethod);
        return invoiceDAO.addInvoice(invoice);
    }
    
    public boolean updateInvoice(int id, String customerName, String phone, String licensePlate, String vehicleType, String address,
                                 double totalBeforeDiscount, double discount, double totalAmount, String notes, String status) {
        return updateInvoice(id, customerName, phone, licensePlate, vehicleType, address, totalBeforeDiscount, discount, totalAmount, notes, status, null, null);
    }

    public boolean updateInvoice(int id, String customerName, String phone, String licensePlate, String vehicleType, String address,
                                 double totalBeforeDiscount, double discount, double totalAmount, String notes, String status, String paymentMethod) {
        return updateInvoice(id, customerName, phone, licensePlate, vehicleType, address, totalBeforeDiscount, discount, totalAmount, notes, status, paymentMethod, null);
    }

    public boolean updateInvoice(int id, String customerName, String phone, String licensePlate, String vehicleType, String address,
                                 double totalBeforeDiscount, double discount, double totalAmount, String notes, String status, String paymentMethod, String createdAt) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return false;
        }
        Invoice invoice = new Invoice(id, customerName, phone, licensePlate, vehicleType, address,
                                     totalBeforeDiscount, discount, totalAmount, notes, status, createdAt, paymentMethod);
        return invoiceDAO.updateInvoice(invoice);
    }
    
    public boolean updateInvoice(Invoice invoice) {
        if (invoice == null || invoice.getCustomerName() == null || invoice.getCustomerName().trim().isEmpty()) {
            return false;
        }
        return invoiceDAO.updateInvoice(invoice);
    }
    
    public boolean deleteInvoice(int id) {
        return invoiceDAO.deleteInvoice(id);
    }
    
    public boolean updateInvoiceStatus(int id, String status) {
        Invoice invoice = invoiceDAO.getInvoiceById(id);
        if (invoice == null) {
            return false;
        }
        invoice.setStatus(status);
        return invoiceDAO.updateInvoice(invoice);
    }
}
