package service;

import dao.ServiceDAO;
import model.Service;
import java.util.List;

public class ServiceService {
    private ServiceDAO serviceDAO = new ServiceDAO();
    
    public List<Service> getAllServices() {
        return serviceDAO.getAllServices();
    }
    
    public Service getServiceById(int id) {
        return serviceDAO.getServiceById(id);
    }
    
    public boolean addService(String name, String description, double priceMini, double priceSedan, 
                             double priceCuv, double priceSuv, double pricePickup) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Service service = new Service(0, name, description, priceMini, priceSedan, priceCuv, priceSuv, pricePickup);
        return serviceDAO.addService(service);
    }
    
    public boolean updateService(int id, String name, String description, double priceMini, double priceSedan,
                                double priceCuv, double priceSuv, double pricePickup) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Service service = new Service(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, pricePickup);
        return serviceDAO.updateService(service);
    }
    
    // Backward compatibility method
    @Deprecated
    public boolean addService(String name, String description, double priceSmall, double priceLarge) {
        return addService(name, description, priceSmall * 0.8, priceSmall, 
                         (priceSmall + priceLarge) / 2, priceLarge, priceLarge * 1.1);
    }
    
    // Backward compatibility method
    @Deprecated
    public boolean updateService(int id, String name, String description, double priceSmall, double priceLarge) {
        return updateService(id, name, description, priceSmall * 0.8, priceSmall,
                           (priceSmall + priceLarge) / 2, priceLarge, priceLarge * 1.1);
    }
    
    public boolean deleteService(int id) {
        return serviceDAO.deleteService(id);
    }
}
