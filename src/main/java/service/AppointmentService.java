package service;

import dao.AppointmentDAO;
import model.Appointment;
import util.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private static final int MAX_CARS_PER_HOUR = 3;

    public static class CustomerInfo {
        private String name;
        private String phone;
        private String address;
        private String licensePlate;
        private String vehicleType;

        public CustomerInfo(String name, String phone, String address, String licensePlate, String vehicleType) {
            this.name = name;
            this.phone = phone;
            this.address = address;
            this.licensePlate = licensePlate;
            this.vehicleType = vehicleType;
        }

        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public String getLicensePlate() { return licensePlate; }
        public String getVehicleType() { return vehicleType; }
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    public Appointment getAppointmentById(int id) {
        return appointmentDAO.getAppointmentById(id);
    }

    public List<Appointment> getAppointmentsByDate(String date) {
        return appointmentDAO.getAppointmentsByDate(date);
    }

    public boolean addAppointment(Appointment appt) {
        return appointmentDAO.addAppointment(appt);
    }

    public boolean updateAppointment(Appointment appt) {
        return appointmentDAO.updateAppointment(appt);
    }

    public boolean deleteAppointment(int id) {
        return appointmentDAO.deleteAppointment(id);
    }

    /**
     * Search unique customers from invoices and appointments
     */
    public List<CustomerInfo> searchCustomers(String query) {
        List<CustomerInfo> result = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return result;
        }
        
        String sql = 
            "SELECT customer_name, phone, address, license_plate, vehicle_type FROM (" +
            "  SELECT customer_name, phone, address, license_plate, vehicle_type FROM invoices " +
            "  WHERE phone LIKE ? OR customer_name LIKE ? " +
            "  UNION " +
            "  SELECT customer_name, phone, address, license_plate, vehicle_type FROM appointments " +
            "  WHERE phone LIKE ? OR customer_name LIKE ? " +
            ") GROUP BY phone, customer_name LIMIT 10";

        String dbPattern = "%" + query.trim() + "%";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dbPattern);
            pstmt.setString(2, dbPattern);
            pstmt.setString(3, dbPattern);
            pstmt.setString(4, dbPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new CustomerInfo(
                        rs.getString("customer_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("license_plate"),
                        rs.getString("vehicle_type")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Checks conflict and overload status for a proposed appointment.
     * Returns a warning message if there is an issue, or null if OK.
     */
    public String checkConflictOrOverload(String date, String time, String licensePlate, int currentId) {
        // 1. Check same vehicle conflict on the same day
        List<Appointment> vehicleAppts = appointmentDAO.getActiveAppointmentsByVehicle(licensePlate);
        for (Appointment appt : vehicleAppts) {
            if (appt.getId() != currentId && appt.getAppointmentDate().equals(date)) {
                // If it's the exact same hour or within 1 hour
                if (Math.abs(timeDifferenceInMinutes(appt.getAppointmentTime(), time)) < 60) {
                    return "Xung đột: Xe " + licensePlate + " đã có lịch hẹn khác lúc " + appt.getAppointmentTime() + " ngày " + date + " (" + appt.getServiceName() + ")";
                }
            }
        }

        // 2. Check overload in time slot (±30 mins from selected time)
        List<Appointment> dayAppts = appointmentDAO.getAppointmentsByDate(date);
        int activeInSlot = 0;
        for (Appointment appt : dayAppts) {
            if (appt.getId() != currentId && (appt.getStatus().equals("Chờ") || appt.getStatus().equals("Đang thực hiện"))) {
                if (Math.abs(timeDifferenceInMinutes(appt.getAppointmentTime(), time)) < 60) {
                    activeInSlot++;
                }
            }
        }

        if (activeInSlot >= MAX_CARS_PER_HOUR) {
            return "Quá tải: Khung giờ " + time + " đã đạt số lượng xe tối đa (" + activeInSlot + "/" + MAX_CARS_PER_HOUR + " xe).";
        }

        return null;
    }

    /**
     * Proposes alternate time slots for the given date.
     */
    public List<String> suggestAlternativeTimes(String date, String originalTime, String licensePlate, int currentId) {
        List<String> suggestions = new ArrayList<>();
        LocalTime baseTime = LocalTime.parse(originalTime);
        
        // Define standard garage working hours: 08:00 to 17:00
        int[] hoursToTry = {1, -1, 2, -2, 3, -3, 4, -4};
        for (int offset : hoursToTry) {
            LocalTime candidate = baseTime.plusHours(offset);
            if (candidate.isBefore(LocalTime.of(8, 0)) || candidate.isAfter(LocalTime.of(17, 0))) {
                continue; // Skip out of business hours
            }
            
            String candidateStr = candidate.format(DateTimeFormatter.ofPattern("HH:mm"));
            String checkResult = checkConflictOrOverload(date, candidateStr, licensePlate, currentId);
            if (checkResult == null) {
                suggestions.add(candidateStr);
                if (suggestions.size() >= 3) {
                    break;
                }
            }
        }
        
        // If still no slot in the day, suggest next day morning
        if (suggestions.isEmpty()) {
            suggestions.add("08:00 (Ngày tiếp theo)");
            suggestions.add("09:00 (Ngày tiếp theo)");
            suggestions.add("10:00 (Ngày tiếp theo)");
        }
        
        return suggestions;
    }

    /**
     * Polls database for upcoming unreminded appointments starting in 1 to 2 hours
     */
    public List<Appointment> getUpcomingUnremindedAppointments() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        LocalTime start = now.plusHours(1);
        LocalTime end = now.plusHours(2);
        
        String dateStr = today.toString();
        String startStr = start.format(DateTimeFormatter.ofPattern("HH:mm"));
        String endStr = end.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        return appointmentDAO.getUpcomingUnremindedAppointments(dateStr, startStr, endStr);
    }

    private int timeDifferenceInMinutes(String t1, String t2) {
        try {
            LocalTime lt1 = LocalTime.parse(t1);
            LocalTime lt2 = LocalTime.parse(t2);
            return (int) java.time.temporal.ChronoUnit.MINUTES.between(lt1, lt2);
        } catch (Exception e) {
            return 999;
        }
    }
}
