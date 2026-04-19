package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:identifier.sqlite";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Đọc file schema.sql từ resources
            InputStream is = DatabaseManager.class.getClassLoader()
                    .getResourceAsStream("schema.sql");
            
            if (is == null) {
                throw new RuntimeException("Không tìm thấy file schema.sql");
            }

            String sql = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));

            // Xử lý từng câu lệnh SQL
            StringBuilder currentStatement = new StringBuilder();
            boolean inTrigger = false;
            
            for (String line : sql.split("\n")) {
                String trimmed = line.trim();
                
                // Bỏ qua comment và dòng trống
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                
                // Kiểm tra trigger
                if (trimmed.toUpperCase().startsWith("CREATE TRIGGER")) {
                    inTrigger = true;
                }
                
                currentStatement.append(line).append("\n");
                
                // Kết thúc trigger
                if (inTrigger && trimmed.equals("END;")) {
                    stmt.execute(currentStatement.toString());
                    currentStatement.setLength(0);
                    inTrigger = false;
                }
                // Kết thúc câu lệnh thông thường
                else if (!inTrigger && trimmed.endsWith(";")) {
                    stmt.execute(currentStatement.toString());
                    currentStatement.setLength(0);
                }
            }

            System.out.println("✓ Database đã được khởi tạo thành công!");
            
        } catch (Exception e) {
            System.err.println("✗ Lỗi khi khởi tạo database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Đã đóng kết nối database");
            }
        } catch (SQLException e) {
            System.err.println("✗ Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }
}
