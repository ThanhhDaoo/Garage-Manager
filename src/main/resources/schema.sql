CREATE TABLE IF NOT EXISTS services (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price_small REAL NOT NULL DEFAULT 0,
    price_large REAL NOT NULL DEFAULT 0,
    created_at TEXT DEFAULT (datetime('now','localtime')),
    price_mini REAL NOT NULL DEFAULT 0,
    price_sedan REAL NOT NULL DEFAULT 0,
    price_cuv REAL NOT NULL DEFAULT 0,
    price_suv REAL NOT NULL DEFAULT 0,
    price_pickup REAL NOT NULL DEFAULT 0,
    price_mpv REAL NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS packages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price REAL NOT NULL DEFAULT 0,
    savings REAL NOT NULL DEFAULT 0,
    status TEXT DEFAULT 'Đang bán',
    created_at TEXT DEFAULT (datetime('now','localtime')),
    price_mini REAL NOT NULL DEFAULT 0,
    price_sedan REAL NOT NULL DEFAULT 0,
    price_cuv REAL NOT NULL DEFAULT 0,
    price_suv REAL NOT NULL DEFAULT 0,
    price_pickup REAL NOT NULL DEFAULT 0,
    price_mpv REAL NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT,
    price REAL NOT NULL DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0,
    status TEXT DEFAULT 'Còn hàng',
    created_at TEXT DEFAULT (datetime('now','localtime'))
);

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
    created_at TEXT DEFAULT (datetime('now','localtime')),
    address TEXT
);

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
