package dao;

import model.Appointment;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Appointment getAppointmentById(int id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Appointment> getAppointmentsByDate(String date) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date = ? ORDER BY appointment_time ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Appointment> getActiveAppointmentsByVehicle(String licensePlate) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE license_plate = ? AND (status = 'Chờ' OR status = 'Đang thực hiện')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, licensePlate);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Appointment> getUpcomingUnremindedAppointments(String date, String startTime, String endTime) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date = ? AND appointment_time >= ? AND appointment_time <= ? AND status = 'Chờ' AND reminded = 0";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            pstmt.setString(2, startTime);
            pstmt.setString(3, endTime);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addAppointment(Appointment appt) {
        String sql = "INSERT INTO appointments (customer_name, phone, address, license_plate, vehicle_type, " +
                     "service_name, appointment_date, appointment_time, expected_completion, notes, status, reminded) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, appt.getCustomerName());
            pstmt.setString(2, appt.getPhone());
            pstmt.setString(3, appt.getAddress());
            pstmt.setString(4, appt.getLicensePlate());
            pstmt.setString(5, appt.getVehicleType());
            pstmt.setString(6, appt.getServiceName());
            pstmt.setString(7, appt.getAppointmentDate());
            pstmt.setString(8, appt.getAppointmentTime());
            pstmt.setString(9, appt.getExpectedCompletion());
            pstmt.setString(10, appt.getNotes());
            pstmt.setString(11, appt.getStatus() != null ? appt.getStatus() : "Chờ");
            pstmt.setInt(12, appt.getReminded());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appt.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAppointment(Appointment appt) {
        String sql = "UPDATE appointments SET customer_name = ?, phone = ?, address = ?, license_plate = ?, " +
                     "vehicle_type = ?, service_name = ?, appointment_date = ?, appointment_time = ?, " +
                     "expected_completion = ?, notes = ?, status = ?, reminded = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, appt.getCustomerName());
            pstmt.setString(2, appt.getPhone());
            pstmt.setString(3, appt.getAddress());
            pstmt.setString(4, appt.getLicensePlate());
            pstmt.setString(5, appt.getVehicleType());
            pstmt.setString(6, appt.getServiceName());
            pstmt.setString(7, appt.getAppointmentDate());
            pstmt.setString(8, appt.getAppointmentTime());
            pstmt.setString(9, appt.getExpectedCompletion());
            pstmt.setString(10, appt.getNotes());
            pstmt.setString(11, appt.getStatus());
            pstmt.setInt(12, appt.getReminded());
            pstmt.setInt(13, appt.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAppointment(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt("id"),
            rs.getString("customer_name"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("license_plate"),
            rs.getString("vehicle_type"),
            rs.getString("service_name"),
            rs.getString("appointment_date"),
            rs.getString("appointment_time"),
            rs.getString("expected_completion"),
            rs.getString("notes"),
            rs.getString("status"),
            rs.getInt("reminded"),
            rs.getString("created_at")
        );
    }
}
