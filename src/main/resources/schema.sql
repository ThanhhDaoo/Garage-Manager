-- =====================================================
-- Gara Xe — SQLite Schema
-- Sedan / SUV · Dịch vụ / Sản phẩm / Gói · Hóa đơn
-- =====================================================
PRAGMA journal_mode = WAL;
PRAGMA foreign_keys = ON;

-- =====================================================
-- CÁC BẢNG KHÔNG SỬ DỤNG - ĐÃ XÓA
-- Ứng dụng sử dụng bảng đơn giản: services, packages, products
-- =====================================================

-- =====================================================
-- BẢNG ĐƠN GIẢN CHO UI
-- =====================================================

-- Bảng services (Dịch vụ đơn giản)
CREATE TABLE IF NOT EXISTS services (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price_small REAL NOT NULL DEFAULT 0,
    price_large REAL NOT NULL DEFAULT 0,
    created_at TEXT DEFAULT (datetime('now','localtime'))
);

-- Bảng packages (Gói dịch vụ)
CREATE TABLE IF NOT EXISTS packages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price REAL NOT NULL DEFAULT 0,
    savings REAL NOT NULL DEFAULT 0,
    status TEXT DEFAULT 'Đang bán',
    created_at TEXT DEFAULT (datetime('now','localtime'))
);

-- Bảng products (Sản phẩm)
CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT,
    price REAL NOT NULL DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0,
    status TEXT DEFAULT 'Còn hàng',
    created_at TEXT DEFAULT (datetime('now','localtime'))
);

-- Bảng invoices (Hóa đơn đơn giản)
CREATE TABLE IF NOT EXISTS invoices (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_name TEXT NOT NULL,
    phone TEXT,
    license_plate TEXT,
    vehicle_type TEXT,
    total_before_discount REAL NOT NULL DEFAULT 0,
    discount REAL NOT NULL DEFAULT 0,
    total_amount REAL NOT NULL DEFAULT 0,
    notes TEXT,
    status TEXT DEFAULT 'nhap',
    created_at TEXT DEFAULT (datetime('now','localtime'))
);

-- Bảng invoice_items (Chi tiết hóa đơn)
CREATE TABLE IF NOT EXISTS invoice_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_id INTEGER NOT NULL,
    item_type TEXT NOT NULL, -- 'service', 'package', 'product'
    item_name TEXT NOT NULL,
    quantity INTEGER DEFAULT 1,
    unit_price REAL NOT NULL DEFAULT 0,
    total_price REAL NOT NULL DEFAULT 0,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
);

-- =====================================================
-- DỮ LIỆU MẪU
-- =====================================================

-- Dữ liệu cho bảng services
INSERT OR IGNORE INTO services (name, description, price_small, price_large) VALUES
('Rửa Xe Cơ Bản', 'Rửa ngoài, hút bụi nội thất', 50000, 100000),
('Rửa Xe Cao Cấp', 'Rửa ngoài, nội thất, đánh bóng', 100000, 200000),
('Đánh Bóng Xe', 'Đánh bóng toàn bộ xe', 200000, 400000),
('Phủ Ceramic', 'Phủ ceramic bảo vệ sơn xe', 2000000, 5000000);

-- Dữ liệu cho bảng packages
INSERT OR IGNORE INTO packages (name, description, price, savings, status) VALUES
('Gói VIP 1', 'Rửa xe cơ bản + Hút bụi nội thất', 120000, 30000, 'Đang bán'),
('Gói VIP 2', 'Rửa xe cao cấp + Đánh bóng + Hút bụi', 280000, 70000, 'Đang bán'),
('Gói VIP 3', 'Rửa xe + Đánh bóng + Phủ wax + Vệ sinh nội thất', 450000, 150000, 'Đang bán');

-- Dữ liệu cho bảng products
INSERT OR IGNORE INTO products (name, category, price, stock, status) VALUES
('Nước Rửa Xe Foam', 'Nước rửa xe', 150000, 25, 'Còn hàng'),
('Dung Dịch Đánh Bóng', 'Dung dịch', 200000, 15, 'Còn hàng'),
('Khăn Lau Xe', 'Phụ kiện', 50000, 50, 'Còn hàng'),
('Wax Đánh Bóng', 'Dung dịch', 300000, 10, 'Còn hàng');
