package org.example;

import util.DatabaseManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== GARA XE - HỆ THỐNG QUẢN LÝ ===\n");

        // Khởi tạo database
        DatabaseManager.initializeDatabase();

        // Test: Hiển thị bảng giá dịch vụ
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM v_bang_gia_dich_vu")) {

            System.out.println("\n📋 BẢNG GIÁ DỊCH VỤ:");
            System.out.println("─".repeat(70));
            System.out.printf("%-25s %-10s %12s %12s%n", 
                "Tên dịch vụ", "Đơn vị", "Sedan", "SUV");
            System.out.println("─".repeat(70));

            while (rs.next()) {
                System.out.printf("%-25s %-10s %,12.0f %,12.0f%n",
                    rs.getString("ten"),
                    rs.getString("don_vi"),
                    rs.getDouble("gia_sedan"),
                    rs.getDouble("gia_suv"));
            }
            System.out.println("─".repeat(70));

        } catch (Exception e) {
            System.err.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        // Test: Hiển thị gói dịch vụ
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM v_bang_gia_goi")) {

            System.out.println("\n📦 GÓI DỊCH VỤ:");
            System.out.println("─".repeat(70));

            while (rs.next()) {
                System.out.printf("\n%s - %s%n", 
                    rs.getString("ten_goi"),
                    rs.getString("mo_ta"));
                System.out.printf("  Bao gồm: %s%n", rs.getString("bao_gom"));
                System.out.printf("  Giá: Sedan %,12.0f đ | SUV %,12.0f đ%n",
                    rs.getDouble("gia_sedan"),
                    rs.getDouble("gia_suv"));
            }
            System.out.println("\n" + "─".repeat(70));

        } catch (Exception e) {
            System.err.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        DatabaseManager.closeConnection();
        System.out.println("\n✓ Hoàn tất!");
    }
}
