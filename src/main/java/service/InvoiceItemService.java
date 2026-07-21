package service;

import dao.InvoiceItemDAO;
import model.InvoiceItem;
import java.util.List;

public class InvoiceItemService {
    private InvoiceItemDAO invoiceItemDAO;
    
    public InvoiceItemService() {
        this.invoiceItemDAO = new InvoiceItemDAO();
    }
    
    public List<InvoiceItem> getItemsByInvoiceId(int invoiceId) {
        return invoiceItemDAO.getItemsByInvoiceId(invoiceId);
    }
    
    public boolean addInvoiceItem(int invoiceId, String itemType, String itemName, 
                                  double quantity, double unitPrice, double totalPrice) {
        return addInvoiceItem(invoiceId, itemType, itemName, quantity, unitPrice, totalPrice, null, null, 0.0, 0);
    }

    public boolean addInvoiceItem(int invoiceId, String itemType, String itemName, 
                                  double quantity, double unitPrice, double totalPrice,
                                  Integer itemId, String category, double costPrice) {
        return addInvoiceItem(invoiceId, itemType, itemName, quantity, unitPrice, totalPrice, itemId, category, costPrice, 0);
    }

    public boolean addInvoiceItem(int invoiceId, String itemType, String itemName, 
                                  double quantity, double unitPrice, double totalPrice,
                                  Integer itemId, String category, double costPrice, int isHidden) {
        InvoiceItem item = new InvoiceItem(0, invoiceId, itemType, itemName, quantity, unitPrice, totalPrice, itemId, category, costPrice, isHidden);
        return invoiceItemDAO.addInvoiceItem(item);
    }
    
    public boolean deleteItemsByInvoiceId(int invoiceId) {
        return invoiceItemDAO.deleteItemsByInvoiceId(invoiceId);
    }
}
