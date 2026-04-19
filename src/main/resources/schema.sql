-- =====================================================
-- Gara Xe — SQLite Schema
-- Sedan / SUV · Dịch vụ / Sản phẩm / Gói · Hóa đơn
-- =====================================================
PRAGMA journal_mode = WAL;
PRAGMA foreign_keys = ON;

-- -----------------------------------------------------
-- 1. DICH_VU — Danh mục dịch vụ
--    Giá khác nhau theo Sedan / SUV
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS dich_vu (
    ma_dv          INTEGER PRIMARY KEY AUTOINCREMENT,
    ten            TEXT    NOT NULL,
    don_vi         TEXT    DEFAULT 'lần',
    con_hoat_dong  INTEGER NOT NULL DEFAULT 1,
    ngay_tao       TEXT    NOT NULL DEFAULT (datetime('now','localtime'))
);

-- -----------------------------------------------------
-- 2. GIA_DICH_VU — Giá dịch vụ theo loại xe
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS gia_dich_vu (
    ma_gia   INTEGER PRIMARY KEY AUTOINCREMENT,
    ma_dv    INTEGER NOT NULL REFERENCES dich_vu(ma_dv) ON DELETE CASCADE,
    loai_xe  TEXT    NOT NULL CHECK (loai_xe IN ('sedan','suv')),
    don_gia  REAL    NOT NULL DEFAULT 0 CHECK (don_gia >= 0),
    UNIQUE (ma_dv, loai_xe)
);

-- -----------------------------------------------------
-- 3. SAN_PHAM — Danh mục sản phẩm bán kèm
--    Giá cố định, không phân biệt loại xe
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS san_pham (
    ma_sp          INTEGER PRIMARY KEY AUTOINCREMENT,
    ten            TEXT    NOT NULL,
    don_vi         TEXT    DEFAULT 'cái',
    gia            REAL    NOT NULL DEFAULT 0 CHECK (gia >= 0),
    ton_kho        INTEGER NOT NULL DEFAULT 0,
    con_hoat_dong  INTEGER NOT NULL DEFAULT 1,
    ngay_tao       TEXT    NOT NULL DEFAULT (datetime('now','localtime'))
);

-- -----------------------------------------------------
-- 4. GOI_DICH_VU — Gói bundle dịch vụ
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS goi_dich_vu (
    ma_goi         INTEGER PRIMARY KEY AUTOINCREMENT,
    ten_goi        TEXT    NOT NULL,
    mo_ta          TEXT,
    con_hoat_dong  INTEGER NOT NULL DEFAULT 1,
    ngay_tao       TEXT    NOT NULL DEFAULT (datetime('now','localtime'))
);

-- -----------------------------------------------------
-- 5. GOI_CHI_TIET — Gói gồm những dịch vụ nào
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS goi_chi_tiet (
    ma_goi  INTEGER NOT NULL REFERENCES goi_dich_vu(ma_goi) ON DELETE CASCADE,
    ma_dv   INTEGER NOT NULL REFERENCES dich_vu(ma_dv)      ON DELETE CASCADE,
    PRIMARY KEY (ma_goi, ma_dv)
);

-- -----------------------------------------------------
-- 6. GIA_GOI — Giá gói theo loại xe
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS gia_goi (
    ma_gia_goi  INTEGER PRIMARY KEY AUTOINCREMENT,
    ma_goi      INTEGER NOT NULL REFERENCES goi_dich_vu(ma_goi) ON DELETE CASCADE,
    loai_xe     TEXT    NOT NULL CHECK (loai_xe IN ('sedan','suv')),
    don_gia     REAL    NOT NULL DEFAULT 0 CHECK (don_gia >= 0),
    UNIQUE (ma_goi, loai_xe)
);

-- -----------------------------------------------------
-- 7. HOA_DON — Hóa đơn
--    trang_thai: 'nhap' | 'da_thanh_toan' | 'huy'
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS hoa_don (
    ma_hd           INTEGER PRIMARY KEY AUTOINCREMENT,
    ten_khach       TEXT,
    sdt             TEXT,
    bien_so         TEXT,
    loai_xe         TEXT    NOT NULL CHECK (loai_xe IN ('sedan','suv')),
    tong_truoc_giam REAL    NOT NULL DEFAULT 0,
    giam_gia        REAL    NOT NULL DEFAULT 0,
    tong_thanh_toan REAL    NOT NULL DEFAULT 0,
    ghi_chu         TEXT,
    trang_thai      TEXT    NOT NULL DEFAULT 'nhap'
        CHECK (trang_thai IN ('nhap','da_thanh_toan','huy')),
    ngay_tao        TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    ngay_thanh_toan TEXT,
    ngay_xuat       TEXT
);

-- -----------------------------------------------------
-- 8. DONG_HOA_DON — Từng dòng trong hóa đơn
--    loai_dong: 'dich_vu' | 'san_pham' | 'goi'
--    ten_dv: snapshot tên lúc lập đơn, không bị ảnh
--            hưởng khi đổi tên sau này
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS dong_hoa_don (
    ma_dong    INTEGER PRIMARY KEY AUTOINCREMENT,
    ma_hd      INTEGER NOT NULL REFERENCES hoa_don(ma_hd) ON DELETE CASCADE,
    loai_dong  TEXT    NOT NULL CHECK (loai_dong IN ('dich_vu','san_pham','goi')),
    ma_ref     INTEGER NOT NULL,   -- ma_dv, ma_sp hoặc ma_goi tùy loai_dong
    ten_dv     TEXT    NOT NULL,   -- snapshot tên
    so_luong   INTEGER NOT NULL DEFAULT 1 CHECK (so_luong > 0),
    don_gia    REAL    NOT NULL DEFAULT 0,
    thanh_tien REAL    NOT NULL DEFAULT 0   -- = so_luong × don_gia
);

-- =====================================================
-- INDEXES
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_hd_trang_thai  ON hoa_don(trang_thai);
CREATE INDEX IF NOT EXISTS idx_hd_ngay_tao    ON hoa_don(ngay_tao);
CREATE INDEX IF NOT EXISTS idx_dong_hd        ON dong_hoa_don(ma_hd);
CREATE INDEX IF NOT EXISTS idx_gia_dv         ON gia_dich_vu(ma_dv, loai_xe);
CREATE INDEX IF NOT EXISTS idx_gia_goi        ON gia_goi(ma_goi, loai_xe);

-- =====================================================
-- TRIGGER — Tự tính lại tổng khi thêm/sửa/xoá dòng
-- =====================================================
CREATE TRIGGER IF NOT EXISTS cap_nhat_tong_them
    AFTER INSERT ON dong_hoa_don
BEGIN
    UPDATE hoa_don
    SET tong_truoc_giam = (SELECT COALESCE(SUM(thanh_tien),0) FROM dong_hoa_don WHERE ma_hd = NEW.ma_hd),
        tong_thanh_toan = (SELECT COALESCE(SUM(thanh_tien),0) FROM dong_hoa_don WHERE ma_hd = NEW.ma_hd) - giam_gia
    WHERE ma_hd = NEW.ma_hd;
END;

CREATE TRIGGER IF NOT EXISTS cap_nhat_tong_xoa
    AFTER DELETE ON dong_hoa_don
BEGIN
    UPDATE hoa_don
    SET tong_truoc_giam = (SELECT COALESCE(SUM(thanh_tien),0) FROM dong_hoa_don WHERE ma_hd = OLD.ma_hd),
        tong_thanh_toan = (SELECT COALESCE(SUM(thanh_tien),0) FROM dong_hoa_don WHERE ma_hd = OLD.ma_hd) - giam_gia
    WHERE ma_hd = OLD.ma_hd;
END;

-- =====================================================
-- VIEWS tiện dụng
-- =====================================================

-- Bảng giá dịch vụ (Sedan vs SUV)
CREATE VIEW IF NOT EXISTS v_bang_gia_dich_vu AS
SELECT
    dv.ma_dv,
    dv.ten,
    dv.don_vi,
    MAX(CASE WHEN g.loai_xe = 'sedan' THEN g.don_gia END) AS gia_sedan,
    MAX(CASE WHEN g.loai_xe = 'suv'   THEN g.don_gia END) AS gia_suv
FROM dich_vu dv
         LEFT JOIN gia_dich_vu g ON g.ma_dv = dv.ma_dv
WHERE dv.con_hoat_dong = 1
GROUP BY dv.ma_dv;

-- Bảng sản phẩm còn hàng
CREATE VIEW IF NOT EXISTS v_san_pham_con_hang AS
SELECT ma_sp, ten, don_vi, gia, ton_kho
FROM san_pham
WHERE con_hoat_dong = 1 AND ton_kho > 0
ORDER BY ten;

-- Bảng giá gói dịch vụ
CREATE VIEW IF NOT EXISTS v_bang_gia_goi AS
SELECT
    gv.ma_goi,
    gv.ten_goi,
    gv.mo_ta,
    MAX(CASE WHEN gg.loai_xe = 'sedan' THEN gg.don_gia END) AS gia_sedan,
    MAX(CASE WHEN gg.loai_xe = 'suv'   THEN gg.don_gia END) AS gia_suv,
    GROUP_CONCAT(dv.ten, ' · ')                              AS bao_gom
FROM goi_dich_vu gv
         LEFT JOIN gia_goi      gg ON gg.ma_goi = gv.ma_goi
         LEFT JOIN goi_chi_tiet gc ON gc.ma_goi = gv.ma_goi
         LEFT JOIN dich_vu      dv ON dv.ma_dv  = gc.ma_dv
WHERE gv.con_hoat_dong = 1
GROUP BY gv.ma_goi;

-- Danh sách hóa đơn tổng hợp
CREATE VIEW IF NOT EXISTS v_danh_sach_hoa_don AS
SELECT
    hd.ma_hd,
    hd.ten_khach,
    hd.sdt,
    hd.bien_so,
    UPPER(hd.loai_xe)       AS loai_xe,
    hd.tong_truoc_giam,
    hd.giam_gia,
    hd.tong_thanh_toan,
    hd.trang_thai,
    hd.ngay_tao,
    hd.ngay_xuat,
    COUNT(dh.ma_dong)       AS so_dong
FROM hoa_don hd
         LEFT JOIN dong_hoa_don dh ON dh.ma_hd = hd.ma_hd
GROUP BY hd.ma_hd
ORDER BY hd.ngay_tao DESC;

-- =====================================================
-- DỮ LIỆU MẪU
-- =====================================================

-- Dịch vụ mẫu
INSERT OR IGNORE INTO dich_vu (ten, don_vi) VALUES
('Rửa ngoài cơ bản',  'lần'),
('Rửa ngoài cao cấp', 'lần'),
('Hút bụi nội thất',  'lần'),
('Vệ sinh taplo',     'lần'),
('Phủ nano kính',     'lần');

-- Giá dịch vụ
INSERT OR IGNORE INTO gia_dich_vu (ma_dv, loai_xe, don_gia) VALUES
(1,'sedan', 50000), (1,'suv', 65000),
(2,'sedan', 80000), (2,'suv',105000),
(3,'sedan', 60000), (3,'suv', 80000),
(4,'sedan', 40000), (4,'suv', 55000),
(5,'sedan',200000), (5,'suv',260000);

-- Sản phẩm mẫu
INSERT OR IGNORE INTO san_pham (ten, don_vi, gia, ton_kho) VALUES
('Nước rửa kính',      'chai', 35000, 50),
('Nước làm bóng sơn',  'chai', 55000, 30),
('Khăn lau microfiber','cái',  25000, 100),
('Nước rửa xe đậm đặc','lít',  45000, 20),
('Dung dịch vệ sinh taplo', 'chai', 40000, 25);

-- Gói dịch vụ mẫu
INSERT OR IGNORE INTO goi_dich_vu (ten_goi, mo_ta) VALUES
('Gói Cơ Bản',     'Rửa ngoài + hút bụi'),
('Gói Tiêu Chuẩn', 'Rửa cao cấp + hút bụi + taplo'),
('Gói VIP',        'Toàn bộ + phủ nano');

INSERT OR IGNORE INTO goi_chi_tiet VALUES
(1,1),(1,3),
(2,2),(2,3),(2,4),
(3,2),(3,3),(3,4),(3,5);

INSERT OR IGNORE INTO gia_goi (ma_goi, loai_xe, don_gia) VALUES
(1,'sedan',104000), (1,'suv',138000),
(2,'sedan',162000), (2,'suv',216000),
(3,'sedan',342000), (3,'suv',450000);
