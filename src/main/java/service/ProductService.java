package service;

import dao.ProductDAO;
import model.Product;
import java.util.List;

public class ProductService {
    private ProductDAO productDAO = new ProductDAO();
    
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }
    
    public Product getProductById(int id) {
        return productDAO.getProductById(id);
    }
    
    public boolean addProduct(String name, String category, double price, double costPrice, double stock, String unit, String status, int minStock) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Product product = new Product(0, name, category, price, costPrice, stock, unit, status, minStock);
        return productDAO.addProduct(product);
    }
    
    public boolean updateProduct(int id, String name, String category, double price, double costPrice, double stock, String unit, String status, int minStock) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Product product = new Product(id, name, category, price, costPrice, stock, unit, status, minStock);
        return productDAO.updateProduct(product);
    }
    
    public boolean reduceStock(int productId, double quantity) {
        return productDAO.reduceStock(productId, quantity);
    }
    
    public Product getProductByName(String name) {
        return productDAO.getProductByName(name);
    }
    
    public boolean deleteProduct(int id) {
        return productDAO.deleteProduct(id);
    }
}
