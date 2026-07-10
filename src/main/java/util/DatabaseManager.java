package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseManager {
    // Database sẽ được copy ra thư mục data bên cạnh file .exe
    private static final String DB_DIR = "data";
    private static final String DB_FILE = "DuLieuMTProAuto.sqlite";
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
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Kích hoạt khóa ngoại và tự động dọn dẹp dữ liệu rác (mồ côi) từ các nhân viên/hóa đơn đã bị xóa trước đây
            stmt.execute("PRAGMA foreign_keys = ON;");
            int deletedAtt = stmt.executeUpdate("DELETE FROM attendance WHERE employee_id NOT IN (SELECT id FROM employees);");
            int deletedPayroll = stmt.executeUpdate("DELETE FROM payroll WHERE employee_id NOT IN (SELECT id FROM employees);");
            int deletedItems = stmt.executeUpdate("DELETE FROM invoice_items WHERE invoice_id NOT IN (SELECT id FROM invoices);");
            if (deletedAtt > 0 || deletedPayroll > 0 || deletedItems > 0) {
                System.out.println(String.format("✓ Đã dọn dẹp dữ liệu mồ côi: %d dòng chấm công, %d dòng bảng lương, %d dòng chi tiết hóa đơn.", 
                                   deletedAtt, deletedPayroll, deletedItems));
            }
            
            // Tự động dọn dẹp các cột thừa trong bảng employees để bảng chỉ lưu thông tin cá nhân và lương cơ bản
            String[] columnsToRemove = {
                "allowance_responsibility", "allowance_other", "commission_consulting",
                "commission_service", "overtime_pay", "social_insurance", "advance_payment", "net_salary"
            };
            for (String col : columnsToRemove) {
                boolean hasCol = false;
                try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(employees)")) {
                    while (rs.next()) {
                        if (col.equals(rs.getString("name"))) {
                            hasCol = true;
                        }
                    }
                }
                if (hasCol) {
                    try (Statement dropColStmt = conn.createStatement()) {
                        dropColStmt.execute("ALTER TABLE employees DROP COLUMN " + col + ";");
                        System.out.println("✓ Đã dọn dẹp cột " + col + " khỏi bảng employees.");
                    } catch (SQLException e) {
                        System.err.println("⚠ Không thể xóa cột " + col + " khỏi employees: " + e.getMessage());
                    }
                }
            }

            // Tự động kiểm tra và khởi tạo bảng payroll
            boolean hasPayrollTable = false;
            boolean hasEmployeeNameInPayroll = false;
            try (ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='payroll'")) {
                if (rs.next()) {
                    hasPayrollTable = true;
                }
            }
            if (hasPayrollTable) {
                try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(payroll)")) {
                    while (rs.next()) {
                        if ("employee_name".equals(rs.getString("name"))) {
                            hasEmployeeNameInPayroll = true;
                        }
                    }
                }
                if (!hasEmployeeNameInPayroll) {
                    try (Statement dropStmt = conn.createStatement()) {
                        dropStmt.execute("DROP TABLE IF EXISTS payroll;");
                        hasPayrollTable = false;
                        System.out.println("⚠ Đã xóa bảng payroll cũ thiếu cột employee_name để chuẩn bị tạo lại.");
                    } catch (SQLException e) { System.err.println("⚠ Lỗi khi xóa bảng payroll cũ: " + e.getMessage()); }
                }
            }
            
            if (!hasPayrollTable) {
                try (Statement createStmt = conn.createStatement()) {
                    createStmt.execute("CREATE TABLE IF NOT EXISTS payroll (" +
                                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                       "employee_id INTEGER NOT NULL, " +
                                       "employee_name TEXT NOT NULL, " +
                                       "pay_month TEXT NOT NULL, " +
                                       "total_days INTEGER NOT NULL, " +
                                       "actual_work_days REAL NOT NULL, " +
                                       "basic_salary REAL NOT NULL, " +
                                       "allowance_responsibility REAL DEFAULT 0, " +
                                       "allowance_other REAL DEFAULT 0, " +
                                       "commission_consulting REAL DEFAULT 0, " +
                                       "commission_service REAL DEFAULT 0, " +
                                       "overtime_pay REAL DEFAULT 0, " +
                                       "social_insurance REAL DEFAULT 0, " +
                                       "advance_payment REAL DEFAULT 0, " +
                                       "net_salary REAL NOT NULL, " +
                                       "created_at TEXT DEFAULT (datetime('now','localtime')), " +
                                       "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE, " +
                                       "UNIQUE(employee_id, pay_month));");
                    System.out.println("✓ Đã tạo lại bảng payroll (có cột employee_name) trong CSDL.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể tạo lại bảng payroll: " + e.getMessage());
                }
            }

            // Tự động khôi phục dữ liệu tính lương tháng 7/2026 từ bảng employees sang bảng payroll nếu bảng payroll trống
            boolean julyHasData = false;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM payroll WHERE pay_month = '2026-07'")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    julyHasData = true;
                }
            }
            if (!julyHasData) {
                System.out.println("⚠ Đang tự động khôi phục lịch sử tính lương Tháng 07/2026...");
                String recoverySql = 
                    "INSERT INTO payroll (employee_id, employee_name, pay_month, total_days, actual_work_days, basic_salary, " +
                    "allowance_responsibility, allowance_other, commission_consulting, commission_service, " +
                    "overtime_pay, social_insurance, advance_payment, net_salary) " +
                    "SELECT id, name, '2026-07', 31, " +
                    "COALESCE((SELECT " +
                    "  (length(attendance_data) - length(replace(attendance_data, '1', '')))*1.0 + " +
                    "  (length(attendance_data) - length(replace(attendance_data, '0.5', '')))/6.0 " +
                    "FROM attendance WHERE employee_id = employees.id AND work_month = '2026-07'), 0), " +
                    "basic_salary, allowance_responsibility, allowance_other, commission_consulting, commission_service, " +
                    "overtime_pay, social_insurance, advance_payment, " +
                    "CASE WHEN net_salary > 0 THEN net_salary ELSE " +
                    "  ((basic_salary / 31.0) * COALESCE((SELECT " +
                    "    (length(attendance_data) - length(replace(attendance_data, '1', '')))*1.0 + " +
                    "    (length(attendance_data) - length(replace(attendance_data, '0.5', '')))/6.0 " +
                    "  FROM attendance WHERE employee_id = employees.id AND work_month = '2026-07'), 0) + " +
                    "  allowance_responsibility + allowance_other + commission_consulting + commission_service + overtime_pay - " +
                    "  social_insurance - advance_payment) END " +
                    "FROM employees WHERE net_salary > 0 OR allowance_responsibility > 0 OR commission_consulting > 0 OR commission_service > 0 OR overtime_pay > 0 OR advance_payment > 0;";
                try (Statement recoveryStmt = conn.createStatement()) {
                    int rows = recoveryStmt.executeUpdate(recoverySql);
                    System.out.println("✓ Đã khôi phục thành công " + rows + " bản ghi tính lương Tháng 07/2026!");
                } catch (SQLException e) {
                    System.err.println("⚠ Lỗi khôi phục lương Tháng 7: " + e.getMessage());
                }
            }

            // Tự động kiểm tra và nâng cấp bảng attendance sang dạng 1 dòng/tháng
            boolean attendanceNeedsMigration = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(attendance)")) {
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    if ("work_date".equals(columnName)) {
                        attendanceNeedsMigration = true;
                    }
                }
            }
            if (attendanceNeedsMigration) {
                System.out.println("⚠ Đang tiến hành di trú bảng attendance sang cấu trúc 1 dòng/tháng...");
                // 1. Đọc dữ liệu cũ
                class TempAtt {
                    int empId;
                    String month;
                    String date;
                    String val;
                }
                java.util.List<TempAtt> oldAtts = new java.util.ArrayList<>();
                try (ResultSet rs = stmt.executeQuery("SELECT employee_id, work_month, work_date, attendance_val FROM attendance")) {
                    while (rs.next()) {
                        TempAtt ta = new TempAtt();
                        ta.empId = rs.getInt("employee_id");
                        ta.month = rs.getString("work_month");
                        ta.date = rs.getString("work_date");
                        ta.val = rs.getString("attendance_val");
                        oldAtts.add(ta);
                    }
                } catch (Exception e) { e.printStackTrace(); }
                
                // 2. Drop bảng cũ
                stmt.execute("DROP TABLE IF EXISTS attendance;");
                
                // 3. Tạo bảng mới
                stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "employee_id INTEGER NOT NULL, " +
                             "employee_name TEXT NOT NULL, " +
                             "work_month TEXT NOT NULL, " +
                             "attendance_data TEXT NOT NULL, " +
                             "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE, " +
                             "UNIQUE(employee_id, work_month));");
                
                // 4. Group dữ liệu theo employee_id và work_month
                java.util.Map<String, java.util.Map<Integer, String>> grouped = new java.util.HashMap<>();
                for (TempAtt ta : oldAtts) {
                    String key = ta.empId + "_" + ta.month;
                    grouped.putIfAbsent(key, new java.util.HashMap<>());
                    try {
                        int day = Integer.parseInt(ta.date.substring(8, 10));
                        grouped.get(key).put(day, ta.val);
                    } catch (Exception e) {}
                }
                
                // 5. Lưu lại dữ liệu mới
                for (var entry : grouped.entrySet()) {
                    String[] parts = entry.getKey().split("_");
                    int empId = Integer.parseInt(parts[0]);
                    String monthStr = parts[1];
                    int year = Integer.parseInt(monthStr.substring(0, 4));
                    int month = Integer.parseInt(monthStr.substring(5, 7));
                    int daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth();
                    
                    var dayVals = entry.getValue();
                    java.util.List<String> list = new java.util.ArrayList<>();
                    for (int d = 1; d <= daysInMonth; d++) {
                        list.add(dayVals.getOrDefault(d, "1"));
                    }
                    String csvData = String.join(",", list);
                    
                    try (java.sql.PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO attendance (employee_id, employee_name, work_month, attendance_data) VALUES (?, ?, ?, ?)")) {
                        insertStmt.setInt(1, empId);
                        
                        String empName = "";
                        try (java.sql.PreparedStatement getEmpStmt = conn.prepareStatement("SELECT name FROM employees WHERE id = ?")) {
                            getEmpStmt.setInt(1, empId);
                            try (ResultSet empRs = getEmpStmt.executeQuery()) {
                                if (empRs.next()) {
                                    empName = empRs.getString("name");
                                }
                            }
                        }
                        if (empName.isEmpty()) empName = "Nhân viên " + empId;
                        
                        insertStmt.setString(2, empName);
                        insertStmt.setString(3, monthStr);
                        insertStmt.setString(4, csvData);
                        insertStmt.executeUpdate();
                    } catch (Exception e) { e.printStackTrace(); }
                }
                System.out.println("✓ Di trú bảng attendance hoàn tất!");
            }

            // Tự động kiểm tra và nâng cấp bảng attendance để chèn cột employee_name ngay sau employee_id
            boolean columnsOrderedCorrectly = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(attendance)")) {
                int index = 0;
                boolean col2IsName = false;
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    if (index == 2 && "employee_name".equals(columnName)) {
                        col2IsName = true;
                    }
                    index++;
                }
                if (index == 5 && col2IsName) {
                    columnsOrderedCorrectly = true;
                }
            }
            if (!columnsOrderedCorrectly) {
                try {
                    System.out.println("⚠ Đang tái cấu trúc bảng attendance để đưa employee_name lên đầu...");
                    
                    // 1. Kiểm tra xem bảng attendance có cột attendance_data hay chưa (để chắc chắn là bảng đã di trú sang dạng tháng)
                    boolean isMonthly = false;
                    try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(attendance)")) {
                        while (rs.next()) {
                            if ("attendance_data".equals(rs.getString("name"))) {
                                isMonthly = true;
                            }
                        }
                    }
                    
                    if (isMonthly) {
                        // Đổi tên bảng hiện tại sang tạm thời
                        stmt.execute("ALTER TABLE attendance RENAME TO temp_attendance;");
                        
                        // Tạo bảng mới với đúng thứ tự cột: id, employee_id, employee_name, work_month, attendance_data
                        stmt.execute("CREATE TABLE attendance (" +
                                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                     "employee_id INTEGER NOT NULL, " +
                                     "employee_name TEXT NOT NULL, " +
                                     "work_month TEXT NOT NULL, " +
                                     "attendance_data TEXT NOT NULL, " +
                                     "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE, " +
                                     "UNIQUE(employee_id, work_month));");
                        
                        // Sao chép dữ liệu và kết hợp với bảng employees để lấy tên
                        stmt.execute("INSERT INTO attendance (id, employee_id, employee_name, work_month, attendance_data) " +
                                     "SELECT t.id, t.employee_id, COALESCE((SELECT name FROM employees WHERE id = t.employee_id), 'Nhân viên ' || t.employee_id), " +
                                     "t.work_month, t.attendance_data FROM temp_attendance t;");
                        
                        // Xóa bảng tạm thời
                        stmt.execute("DROP TABLE temp_attendance;");
                        System.out.println("✓ Đã tái cấu trúc bảng attendance thành công với cột employee_name nằm sau employee_id.");
                    }
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể tái cấu trúc bảng attendance: " + e.getMessage());
                }
            }

            // Tự động kiểm tra và thêm các cột min_stock, cost_price, unit nếu chưa tồn tại
            boolean hasMinStock = false;
            boolean hasCostPrice = false;
            boolean hasUnit = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(products)")) {
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    if ("min_stock".equals(columnName)) {
                        hasMinStock = true;
                    } else if ("cost_price".equals(columnName)) {
                        hasCostPrice = true;
                    } else if ("unit".equals(columnName)) {
                        hasUnit = true;
                    }
                }
            }
            if (!hasMinStock) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE products ADD COLUMN min_stock INTEGER NOT NULL DEFAULT 0;");
                    System.out.println("✓ Đã thêm cột min_stock vào bảng products.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột min_stock: " + e.getMessage());
                }
            }
            if (!hasCostPrice) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE products ADD COLUMN cost_price REAL DEFAULT 0;");
                    System.out.println("✓ Đã thêm cột cost_price vào bảng products.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột cost_price: " + e.getMessage());
                }
            }
            if (!hasUnit) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE products ADD COLUMN unit TEXT;");
                    System.out.println("✓ Đã thêm cột unit vào bảng products.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột unit: " + e.getMessage());
                }
            }

            // Tự động kiểm tra và thêm các cột category, cost_price, linked_product_id vào bảng services nếu chưa tồn tại
            boolean hasServiceCategory = false;
            boolean hasServiceCostPrice = false;
            boolean hasLinkedProduct = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(services)")) {
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    if ("category".equals(columnName)) {
                        hasServiceCategory = true;
                    } else if ("cost_price".equals(columnName)) {
                        hasServiceCostPrice = true;
                    } else if ("linked_product_id".equals(columnName)) {
                        hasLinkedProduct = true;
                    }
                }
            }
            if (!hasServiceCategory) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE services ADD COLUMN category TEXT NOT NULL DEFAULT 'rửa xe';");
                    System.out.println("✓ Đã thêm cột category vào bảng services.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột category: " + e.getMessage());
                }
            }
            if (!hasServiceCostPrice) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE services ADD COLUMN cost_price REAL NOT NULL DEFAULT 0;");
                    System.out.println("✓ Đã thêm cột cost_price vào bảng services.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột cost_price: " + e.getMessage());
                }
            }
            if (!hasLinkedProduct) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE services ADD COLUMN linked_product_id INTEGER;");
                    System.out.println("✓ Đã thêm cột linked_product_id vào bảng services.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột linked_product_id: " + e.getMessage());
                }
            }

            // Tự động di trú bảng packages
            boolean hasPackageCategory = false;
            boolean hasPackageCostPrice = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(packages)")) {
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    if ("category".equals(columnName)) {
                        hasPackageCategory = true;
                    } else if ("cost_price".equals(columnName)) {
                        hasPackageCostPrice = true;
                    }
                }
            }
            if (!hasPackageCategory) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE packages ADD COLUMN category TEXT NOT NULL DEFAULT 'chăm sóc';");
                    System.out.println("✓ Đã thêm cột category vào bảng packages.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột category vào packages: " + e.getMessage());
                }
            }
            if (!hasPackageCostPrice) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE packages ADD COLUMN cost_price REAL NOT NULL DEFAULT 0;");
                    System.out.println("✓ Đã thêm cột cost_price vào bảng packages.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột cost_price vào packages: " + e.getMessage());
                }
            }

            // Tự động di trú bảng invoices
            boolean hasInvoicePaymentMethod = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(invoices)")) {
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    if ("payment_method".equals(columnName)) {
                        hasInvoicePaymentMethod = true;
                    }
                }
            }
            if (!hasInvoicePaymentMethod) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE invoices ADD COLUMN payment_method TEXT;");
                    System.out.println("✓ Đã thêm cột payment_method vào bảng invoices.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột payment_method vào invoices: " + e.getMessage());
                }
            }

            // Tự động di trú bảng invoice_items
            boolean hasItemId = false;
            boolean hasItemCategory = false;
            boolean hasItemCostPrice = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(invoice_items)")) {
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    if ("item_id".equals(columnName)) {
                        hasItemId = true;
                    } else if ("category".equals(columnName)) {
                        hasItemCategory = true;
                    } else if ("cost_price".equals(columnName)) {
                        hasItemCostPrice = true;
                    }
                }
            }
            if (!hasItemId) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE invoice_items ADD COLUMN item_id INTEGER;");
                    System.out.println("✓ Đã thêm cột item_id vào bảng invoice_items.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột item_id vào invoice_items: " + e.getMessage());
                }
            }
            if (!hasItemCategory) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE invoice_items ADD COLUMN category TEXT;");
                    System.out.println("✓ Đã thêm cột category vào bảng invoice_items.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột category vào invoice_items: " + e.getMessage());
                }
            }
            if (!hasItemCostPrice) {
                try (Statement alterStmt = conn.createStatement()) {
                    alterStmt.execute("ALTER TABLE invoice_items ADD COLUMN cost_price REAL DEFAULT 0;");
                    System.out.println("✓ Đã thêm cột cost_price vào bảng invoice_items.");
                } catch (SQLException e) {
                    System.err.println("⚠ Không thể thêm cột cost_price vào invoice_items: " + e.getMessage());
                }
            }

            // Tự động kiểm tra và thêm các cột cho bảng payroll nếu CSDL cũ đã tồn tại bảng này nhưng chưa có các cột mới
            boolean payrollTableExists = false;
            try (ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='payroll'")) {
                if (rs.next()) {
                    payrollTableExists = true;
                }
            }
            if (payrollTableExists) {
                boolean prHasResponsibility = false;
                boolean prHasOther = false;
                boolean prHasConsulting = false;
                boolean prHasService = false;
                boolean prHasOvertime = false;
                boolean prHasInsurance = false;
                boolean prHasAdvance = false;
                boolean prHasNet = false;
                try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(payroll)")) {
                    while (rs.next()) {
                        String columnName = rs.getString("name");
                        if ("allowance_responsibility".equals(columnName)) prHasResponsibility = true;
                        else if ("allowance_other".equals(columnName)) prHasOther = true;
                        else if ("commission_consulting".equals(columnName)) prHasConsulting = true;
                        else if ("commission_service".equals(columnName)) prHasService = true;
                        else if ("overtime_pay".equals(columnName)) prHasOvertime = true;
                        else if ("social_insurance".equals(columnName)) prHasInsurance = true;
                        else if ("advance_payment".equals(columnName)) prHasAdvance = true;
                        else if ("net_salary".equals(columnName)) prHasNet = true;
                    }
                }
                if (!prHasResponsibility) {
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.execute("ALTER TABLE payroll ADD COLUMN allowance_responsibility REAL DEFAULT 0;");
                        System.out.println("✓ Đã thêm cột allowance_responsibility vào bảng payroll.");
                    } catch (SQLException e) { System.err.println("⚠ Lỗi thêm cột payroll: " + e.getMessage()); }
                }
                if (!prHasOther) {
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.execute("ALTER TABLE payroll ADD COLUMN allowance_other REAL DEFAULT 0;");
                        System.out.println("✓ Đã thêm cột allowance_other vào bảng payroll.");
                    } catch (SQLException e) { System.err.println("⚠ Lỗi thêm cột payroll: " + e.getMessage()); }
                }
                if (!prHasConsulting) {
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.execute("ALTER TABLE payroll ADD COLUMN commission_consulting REAL DEFAULT 0;");
                        System.out.println("✓ Đã thêm cột commission_consulting vào bảng payroll.");
                    } catch (SQLException e) { System.err.println("⚠ Lỗi thêm cột payroll: " + e.getMessage()); }
                }
                if (!prHasService) {
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.execute("ALTER TABLE payroll ADD COLUMN commission_service REAL DEFAULT 0;");
                        System.out.println("✓ Đã thêm cột commission_service vào bảng payroll.");
                    } catch (SQLException e) { System.err.println("⚠ Lỗi thêm cột payroll: " + e.getMessage()); }
                }
                if (!prHasOvertime) {
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.execute("ALTER TABLE payroll ADD COLUMN overtime_pay REAL DEFAULT 0;");
                        System.out.println("✓ Đã thêm cột overtime_pay vào bảng payroll.");
                    } catch (SQLException e) { System.err.println("⚠ Lỗi thêm cột payroll: " + e.getMessage()); }
                }
                if (!prHasInsurance) {
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.execute("ALTER TABLE payroll ADD COLUMN social_insurance REAL DEFAULT 0;");
                        System.out.println("✓ Đã thêm cột social_insurance vào bảng payroll.");
                    } catch (SQLException e) { System.err.println("⚠ Lỗi thêm cột payroll: " + e.getMessage()); }
                }
                if (!prHasAdvance) {
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.execute("ALTER TABLE payroll ADD COLUMN advance_payment REAL DEFAULT 0;");
                        System.out.println("✓ Đã thêm cột advance_payment vào bảng payroll.");
                    } catch (SQLException e) { System.err.println("⚠ Lỗi thêm cột payroll: " + e.getMessage()); }
                }
                if (!prHasNet) {
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.execute("ALTER TABLE payroll ADD COLUMN net_salary REAL DEFAULT 0;");
                        System.out.println("✓ Đã thêm cột net_salary vào bảng payroll.");
                    } catch (SQLException e) { System.err.println("⚠ Lỗi thêm cột payroll: " + e.getMessage()); }
                }
            }
            
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
