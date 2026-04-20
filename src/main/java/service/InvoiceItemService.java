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
                                  int quantity, double unitPrice, double totalPrice) {
        InvoiceItem item = new InvoiceItem();
        item.setInvoiceId(invoiceId);
        item.setItemType(itemType);
        item.setItemName(itemName);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setTotalPrice(totalPrice);
        
        return invoiceItemDAO.addInvoiceItem(item);
    }
    
    public boolean deleteItemsByInvoiceId(int invoiceId) {
        return invoiceItemDAO.deleteItemsByInvoiceId(invoiceId);
    }
}
