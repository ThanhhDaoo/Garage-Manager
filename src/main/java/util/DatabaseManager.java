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
    // Database sẽ được copy ra thư mục data bên cạnh file .exe
    private static final String DB_DIR = "data";
    private static final String DB_FILE = "identifier.sqlite";
    private static final String DB_URL;
    private static Connection connection;
    
    static {
        // Tạo thư mục data nếu chưa có
        java.io.File dataDir = new java.io.File(DB_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("✓ Đã tạo thư mục data: " + dataDir.getAbsolutePath());
        }
        
        // Đường dẫn đến database bên ngoài
        java.io.File externalDb = new java.io.File(DB_DIR + "/" + DB_FILE);
        
        // Nếu database chưa có bên ngoài, copy từ thư mục gốc
        if (!externalDb.exists()) {
            java.io.File sourceDb = new java.io.File(DB_FILE);
            if (sourceDb.exists()) {
                try {
                    java.nio.file.Files.copy(sourceDb.toPath(), externalDb.toPath());
                    System.out.println("✓ Đã copy database ra thư mục data");
                } catch (Exception e) {
                    System.err.println("⚠ Không thể copy database: " + e.getMessage());
                }
            }
        }
        
        // Đường dẫn đầy đủ đến database
        DB_URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;
        System.out.println("📁 Database path: " + externalDb.getAbsolutePath());
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
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
