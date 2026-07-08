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
                             double priceCuv, double priceSuv, double priceMpv, double pricePickup, String category, double costPrice) {
        return addService(name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, category, costPrice, null);
    }

    public boolean addService(String name, String description, double priceMini, double priceSedan, 
                             double priceCuv, double priceSuv, double priceMpv, double pricePickup, String category, double costPrice, Integer linkedProductId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Service service = new Service(0, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, category, costPrice, linkedProductId);
        return serviceDAO.addService(service);
    }
    
    public boolean updateService(int id, String name, String description, double priceMini, double priceSedan,
                                double priceCuv, double priceSuv, double priceMpv, double pricePickup, String category, double costPrice) {
        return updateService(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, category, costPrice, null);
    }

    public boolean updateService(int id, String name, String description, double priceMini, double priceSedan,
                                double priceCuv, double priceSuv, double priceMpv, double pricePickup, String category, double costPrice, Integer linkedProductId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Service service = new Service(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, category, costPrice, linkedProductId);
        return serviceDAO.updateService(service);
    }
    
    // Backward compatibility method
    @Deprecated
    public boolean addService(String name, String description, double priceSmall, double priceLarge) {
        return addService(name, description, priceSmall * 0.8, priceSmall, 
                         (priceSmall + priceLarge) / 2, priceLarge, priceLarge * 1.05, priceLarge * 1.1, "rửa xe", 0.0);
    }
    
    // Backward compatibility method
    @Deprecated
    public boolean updateService(int id, String name, String description, double priceSmall, double priceLarge) {
        return updateService(id, name, description, priceSmall * 0.8, priceSmall,
                           (priceSmall + priceLarge) / 2, priceLarge, priceLarge * 1.05, priceLarge * 1.1, "rửa xe", 0.0);
    }
    
    public boolean deleteService(int id) {
        return serviceDAO.deleteService(id);
    }
}
