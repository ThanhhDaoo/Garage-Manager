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
    
    public boolean addProduct(String name, String category, double price, int stock, String status) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Product product = new Product(0, name, category, price, stock, status);
        return productDAO.addProduct(product);
    }
    
    public boolean updateProduct(int id, String name, String category, double price, int stock, String status) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Product product = new Product(id, name, category, price, stock, status);
        return productDAO.updateProduct(product);
    }
    
    public boolean deleteProduct(int id) {
        return productDAO.deleteProduct(id);
    }
}
