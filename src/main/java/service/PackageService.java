package service;

import dao.PackageDAO;
import model.Package;
import java.util.List;

public class PackageService {
    private PackageDAO packageDAO = new PackageDAO();
    
    public List<Package> getAllPackages() {
        return packageDAO.getAllPackages();
    }
    
    public Package getPackageById(int id) {
        return packageDAO.getPackageById(id);
    }
    
    public boolean addPackage(String name, String description, double price, double savings, String status) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Package pkg = new Package(0, name, description, price, savings, status);
        return packageDAO.addPackage(pkg);
    }
    
    public boolean updatePackage(int id, String name, String description, double price, double savings, String status) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Package pkg = new Package(id, name, description, price, savings, status);
        return packageDAO.updatePackage(pkg);
    }
    
    public boolean deletePackage(int id) {
        return packageDAO.deletePackage(id);
    }
}
