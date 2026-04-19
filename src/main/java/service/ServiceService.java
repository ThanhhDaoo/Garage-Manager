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
    
    public boolean addService(String name, String description, double priceSmall, double priceLarge) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Service service = new Service(0, name, description, priceSmall, priceLarge);
        return serviceDAO.addService(service);
    }
    
    public boolean updateService(int id, String name, String description, double priceSmall, double priceLarge) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Service service = new Service(id, name, description, priceSmall, priceLarge);
        return serviceDAO.updateService(service);
    }
    
    public boolean deleteService(int id) {
        return serviceDAO.deleteService(id);
    }
}
