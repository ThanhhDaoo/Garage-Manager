package dao;

import model.Service;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {
    
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int linkedIdVal = rs.getInt("linked_product_id");
                Integer linkedProductId = rs.wasNull() ? null : linkedIdVal;
                Service service = new Service(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price_mini"),
                    rs.getDouble("price_sedan"),
                    rs.getDouble("price_cuv"),
                    rs.getDouble("price_suv"),
                    rs.getDouble("price_mpv"),
                    rs.getDouble("price_pickup"),
                    rs.getString("category"),
                    rs.getDouble("cost_price"),
                    linkedProductId
                );
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
    
    public Service getServiceById(int id) {
        String sql = "SELECT * FROM services WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int linkedIdVal = rs.getInt("linked_product_id");
                Integer linkedProductId = rs.wasNull() ? null : linkedIdVal;
                return new Service(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price_mini"),
                    rs.getDouble("price_sedan"),
                    rs.getDouble("price_cuv"),
                    rs.getDouble("price_suv"),
                    rs.getDouble("price_mpv"),
                    rs.getDouble("price_pickup"),
                    rs.getString("category"),
                    rs.getDouble("cost_price"),
                    linkedProductId
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addService(Service service) {
        String sql = "INSERT INTO services (name, description, price_mini, price_sedan, price_cuv, price_suv, price_mpv, price_pickup, category, cost_price, linked_product_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setDouble(3, service.getPriceMini());
            pstmt.setDouble(4, service.getPriceSedan());
            pstmt.setDouble(5, service.getPriceCuv());
            pstmt.setDouble(6, service.getPriceSuv());
            pstmt.setDouble(7, service.getPriceMpv());
            pstmt.setDouble(8, service.getPricePickup());
            pstmt.setString(9, service.getCategory());
            pstmt.setDouble(10, service.getCostPrice());
            if (service.getLinkedProductId() != null && service.getLinkedProductId() > 0) {
                pstmt.setInt(11, service.getLinkedProductId());
            } else {
                pstmt.setNull(11, java.sql.Types.INTEGER);
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateService(Service service) {
        String sql = "UPDATE services SET name = ?, description = ?, price_mini = ?, price_sedan = ?, price_cuv = ?, price_suv = ?, price_mpv = ?, price_pickup = ?, category = ?, cost_price = ?, linked_product_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setDouble(3, service.getPriceMini());
            pstmt.setDouble(4, service.getPriceSedan());
            pstmt.setDouble(5, service.getPriceCuv());
            pstmt.setDouble(6, service.getPriceSuv());
            pstmt.setDouble(7, service.getPriceMpv());
            pstmt.setDouble(8, service.getPricePickup());
            pstmt.setString(9, service.getCategory());
            pstmt.setDouble(10, service.getCostPrice());
            if (service.getLinkedProductId() != null && service.getLinkedProductId() > 0) {
                pstmt.setInt(11, service.getLinkedProductId());
            } else {
                pstmt.setNull(11, java.sql.Types.INTEGER);
            }
            pstmt.setInt(12, service.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteService(int id) {
        String sql = "DELETE FROM services WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
