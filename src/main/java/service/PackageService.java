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
    
    public boolean addPackage(String name, String description, double priceMini, double priceSedan,
                             double priceCuv, double priceSuv, double priceMpv, double pricePickup, double savings, String status, String category, double costPrice) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Package pkg = new Package(0, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, savings, status, category, costPrice);
        return packageDAO.addPackage(pkg);
    }
    
    public boolean updatePackage(int id, String name, String description, double priceMini, double priceSedan,
                                double priceCuv, double priceSuv, double priceMpv, double pricePickup, double savings, String status, String category, double costPrice) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Package pkg = new Package(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, savings, status, category, costPrice);
        return packageDAO.updatePackage(pkg);
    }

    @Deprecated
    public boolean addPackage(String name, String description, double priceMini, double priceSedan,
                             double priceCuv, double priceSuv, double priceMpv, double pricePickup, double savings, String status) {
        return addPackage(name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, savings, status, "chăm sóc", 0.0);
    }

    @Deprecated
    public boolean updatePackage(int id, String name, String description, double priceMini, double priceSedan,
                                double priceCuv, double priceSuv, double priceMpv, double pricePickup, double savings, String status) {
        return updatePackage(id, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, savings, status, "chăm sóc", 0.0);
    }
    
    // Backward compatibility methods
    @Deprecated
    public boolean addPackage(String name, String description, double price, double savings, String status) {
        return addPackage(name, description, price * 0.8, price, price * 1.5, price * 2, price * 2.1, price * 2.2, savings, status, "chăm sóc", 0.0);
    }
    
    @Deprecated
    public boolean updatePackage(int id, String name, String description, double price, double savings, String status) {
        return updatePackage(id, name, description, price * 0.8, price, price * 1.5, price * 2, price * 2.1, price * 2.2, savings, status, "chăm sóc", 0.0);
    }
    
    public boolean deletePackage(int id) {
        return packageDAO.deletePackage(id);
    }
}
