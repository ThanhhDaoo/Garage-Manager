Garage Management System (MTProAuto)
Hệ thống quản lý gara ô tô chuyên nghiệp với giao diện hiện đại, được xây dựng bằng Java và JavaFX

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Mục Lục
- [Giới Thiệu]
- [Tính Năng Chính]
- [Công Nghệ Sử Dụng]
- [Kiến Trúc Hệ Thống]
- [Cài Đặt & Chạy]
- [Screenshots]
- [Điểm Nổi Bật Kỹ Thuật]
- [Roadmap]

Giới Thiệu

Garage Management System là ứng dụng desktop quản lý toàn diện cho gara ô tô, giúp tự động hóa quy trình quản lý hóa đơn, dịch vụ, sản phẩm và báo cáo doanh thu. Ứng dụng được thiết kế với giao diện hiện đại, trực quan và hỗ trợ đầy đủ tiếng Việt.

Mục Đích Dự Án
- Áp dụng kiến thức Java Core và JavaFX vào thực tế
- Thực hành Design Pattern (MVC, DAO, Service Layer)
- Xây dựng ứng dụng desktop hoàn chỉnh từ A-Z
- Giải quyết bài toán thực tế trong quản lý gara

Tính Năng Chính

Dashboard & Thống Kê
- Hiển thị doanh thu real-time
- Thống kê số lượng hóa đơn và khách hàng
- Biểu đồ trực quan với Material Design

Quản Lý Hóa Đơn
- Tạo hóa đơn mới với giao diện thân thiện
- Tìm kiếm và lọc hóa đơn theo trạng thái
- Xuất hóa đơn ra PDF với font tiếng Việt
- Xem chi tiết và lịch sử hóa đơn

Quản Lý Dịch Vụ
- CRUD operations cho dịch vụ
- Phân loại theo loại xe (Sedan, SUV)
- Tính giá tự động theo loại xe

Quản Lý Gói Dịch Vụ
- Tạo combo dịch vụ với giá ưu đãi
- Quản lý danh sách dịch vụ trong gói
- Tính toán tiết kiệm tự động

#Quản Lý Sản Phẩm
- Quản lý kho sản phẩm
- Phân loại theo danh mục
- Theo dõi tồn kho

Báo Cáo & Xuất PDF
- Báo cáo doanh thu theo khoảng thời gian
- Xuất báo cáo PDF với font tiếng Việt
- Thống kê chi tiết theo khách hàng

Công Nghệ Sử Dụng

Core Technologies
- Java 17 - Programming Language
- JavaFX 17.0.2 - UI Framework
- Maven - Build Tool & Dependency Management
- SQLite 3.47.1 - Embedded Database

Libraries & Frameworks
- iText7 7.2.5 - PDF Generation & Export
- ZXing 3.5.1 - QR Code Generation
- JDBC - Database Connectivity

Design & Architecture
- MVC Pattern- Model-View-Controller
- DAO Pattern- Data Access Object
- Service Layer- Business Logic Separation
- Material Design- Modern UI/UX

Kiến Trúc Hệ Thống

src/main/java/
├── dao/                    # Data Access Layer
│   ├── InvoiceDAO.java
│   ├── ServiceDAO.java
│   ├── ProductDAO.java
│   └── PackageDAO.java
├── model/                  # Domain Models
│   ├── Invoice.java
│   ├── Service.java
│   ├── Product.java
│   └── Package.java
├── service/                # Business Logic Layer
│   ├── InvoiceService.java
│   ├── ServiceService.java
│   ├── ProductService.java
│   └── PackageService.java
├── ui/                     # Presentation Layer
│   ├── MainUI.java
│   ├── CreateInvoiceForm.java
│   ├── ServiceForm.java
│   └── ReportHelper.java
└── util/                   # Utilities
    ├── DatabaseManager.java
    ├── PDFFontHelper.java
    └── AlertHelper.java

Data Flow
UI Layer → Service Layer → DAO Layer → Database
   ↓           ↓              ↓
View ←── Business Logic ←── Data Access

Cài Đặt & Chạy

Yêu Cầu Hệ Thống
- Java JDK 17 hoặc cao hơn
- Maven 3.8+
- Windows 10/11 (hoặc macOS/Linux)
- 4GB RAM (khuyến nghị)

Cài Đặt

1.Clone repository
git clone https://github.com/ThanhhDaoo/Garage-Manager.git
cd Garage-Manager

2.Build project
mvn clean install

3.Chạy ứng dụng
mvn javafx:run

Build File .exe (Windows)
# Build JAR file
mvn clean package

# Sử dụng Launch4j hoặc jpackage để tạo .exe
jpackage --input target \
  --main-jar GarageManager-1.0-SNAPSHOT-shaded.jar \
  --main-class ui.MainUI \
  --name "Garage Manager" \
  --win-dir-chooser --win-menu --win-shortcut

Screenshots

Dashboard
![Dashboard](docs/screenshots/dashboard.png)
*Dashboard với thống kê real-time và quick actions*

Quản Lý Hóa Đơn
![Invoice Management](docs/screenshots/invoice.png)
*Giao diện quản lý hóa đơn với tìm kiếm và lọc*

Xuất PDF
![PDF Export](docs/screenshots/pdf-export.png)
*Hóa đơn PDF với font tiếng Việt hoàn hảo*

Điểm Nổi Bật Kỹ Thuật

1. Custom Font Management System
PDFFontHelper.createVietnameseFont()
- Tự động detect font từ Windows System
- Fallback mechanism cho nhiều font
- Embed font vào PDF để cross-platform compatibility

2. Layered Architecture
- Separation of Concerns: UI, Business Logic, Data Access tách biệt
- Dependency Injection: Loose coupling giữa các layer
- Reusability: Code có thể tái sử dụng cao

3. Modern UI/UX Design
- Material Design principles
- Responsive layout với CSS styling
- Smooth animations và transitions
- User-friendly error handling

4. Database Design
- Normalized database schema
- Foreign key constraints
- Efficient indexing
- Transaction management

5. PDF Generation
- Professional invoice layout
- Vietnamese font embedding
- QR code integration
- Automatic formatting

Thống Kê Dự Án

- Lines of Code: ~3,500+
- Java Files: 28
- Database Tables: 5+
- UI Forms: 7
- Development Time: 2 months
- Test Coverage**: Manual testing

Roadmap

Version 2.0 (Planned)
- [ ] User authentication & authorization
- [ ] Multi-user support với roles
- [ ] Cloud backup & sync
- [ ] Email notification cho khách hàng
- [ ] SMS integration
- [ ] Advanced reporting với charts
- [ ] Export to Excel
- [ ] Mobile app companion

Version 2.1 (Future)
- [ ] REST API backend
- [ ] Web dashboard
- [ ] Real-time notifications
- [ ] Integration với payment gateway
- [ ] Customer loyalty program

Đóng Góp

Mọi đóng góp đều được chào đón! Vui lòng:
1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

License

Dự án này được phân phối dưới giấy phép MIT. Xem file `LICENSE` để biết thêm chi tiết.

Tác Giả

Trần Thành Đạo
- GitHub: [@ThanhhDaoo](https://github.com/ThanhhDaoo)
- Email: tranthanhdao82@gmail.com
- Zalo: 0362625218

Acknowledgments

- JavaFX Community
- iText PDF Library
- Material Design Guidelines
- Stack Overflow Community

⭐Nếu bạn thấy dự án hữu ích, hãy cho một star!⭐
