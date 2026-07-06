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
    cost_price REAL DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0,
    unit TEXT,
    status TEXT DEFAULT 'Còn hàng',
    min_stock INTEGER NOT NULL DEFAULT 0,
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

CREATE TABLE IF NOT EXISTS appointments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    address TEXT,
    license_plate TEXT NOT NULL,
    vehicle_type TEXT NOT NULL,
    service_name TEXT NOT NULL,
    appointment_date TEXT NOT NULL,
    appointment_time TEXT NOT NULL,
    expected_completion TEXT,
    notes TEXT,
    status TEXT DEFAULT 'Chờ',
    reminded INTEGER DEFAULT 0,
    created_at TEXT DEFAULT (datetime('now','localtime'))
);

CREATE TABLE IF NOT EXISTS employees (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    phone TEXT,
    address TEXT,
    dob TEXT,
    gender TEXT,
    start_date TEXT,
    position TEXT,
    basic_salary REAL NOT NULL DEFAULT 0,
    allowance_responsibility REAL DEFAULT 0,
    allowance_other REAL DEFAULT 0,
    commission_consulting REAL DEFAULT 0,
    commission_service REAL DEFAULT 0,
    overtime_pay REAL DEFAULT 0,
    social_insurance REAL DEFAULT 0,
    advance_payment REAL DEFAULT 0,
    net_salary REAL DEFAULT 0,
    created_at TEXT DEFAULT (datetime('now','localtime'))
);

CREATE TABLE IF NOT EXISTS attendance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id INTEGER NOT NULL,
    employee_name TEXT NOT NULL,
    work_month TEXT NOT NULL,
    attendance_data TEXT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE(employee_id, work_month)
);

CREATE TABLE IF NOT EXISTS payroll (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id INTEGER NOT NULL,
    pay_month TEXT NOT NULL,
    total_days INTEGER NOT NULL,
    actual_work_days REAL NOT NULL,
    basic_salary REAL NOT NULL,
    allowance_responsibility REAL DEFAULT 0,
    allowance_other REAL DEFAULT 0,
    commission_consulting REAL DEFAULT 0,
    commission_service REAL DEFAULT 0,
    overtime_pay REAL DEFAULT 0,
    social_insurance REAL DEFAULT 0,
    advance_payment REAL DEFAULT 0,
    net_salary REAL NOT NULL,
    created_at TEXT DEFAULT (datetime('now','localtime')),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE(employee_id, pay_month)
);

CREATE TABLE IF NOT EXISTS fixed_expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expense_name TEXT NOT NULL,
    category TEXT NOT NULL DEFAULT 'cố định',
    amount REAL NOT NULL DEFAULT 0,
    expense_month TEXT NOT NULL,
    notes TEXT,
    created_at TEXT DEFAULT (datetime('now','localtime'))
);
