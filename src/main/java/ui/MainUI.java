package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Modality;
import util.DatabaseManager;
import service.ServiceService;
import service.PackageService;
import service.ProductService;
import service.InvoiceService;
import service.InvoiceItemService;
import service.AppointmentService;
import service.EmployeeService;
import service.AttendanceService;
import service.PayrollService;

import model.Service;
import model.Package;
import model.Product;
import model.Invoice;
import model.InvoiceItem;
import model.Appointment;
import model.Employee;
import model.Attendance;
import model.Payroll;
import java.util.List;
import java.time.LocalDate;
import java.time.YearMonth;

public class MainUI extends Application {

    private BorderPane mainLayout;
    private StackPane contentArea;
    private VBox sidebar;
    private VBox invoiceTableRows;
    private String currentView = "dashboard";

    @Override
    public void start(Stage stage) {
        DatabaseManager.initializeDatabase();

        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f8f9fa;");
        
        HBox header = createModernHeader();
        mainLayout.setTop(header);
        
        sidebar = createModernSidebar();
        mainLayout.setLeft(sidebar);
        
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #f8f9fa;");
        mainLayout.setCenter(contentArea);
        
        showDashboard();

        Scene scene = new Scene(mainLayout, 1400, 800);
        
        // Apply global font stylesheet
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("Could not load global-styles.css");
        }
        
        // Add application icon
        try {
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/logo.png")));
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
        
        stage.setScene(scene);
        stage.setTitle("🚗 MTProAuto - Quản Lý Chuyên Nghiệp");
        
        // Khởi chạy tác vụ kiểm tra lịch hẹn sắp diễn ra (mỗi phút)
        setupAppointmentNotificationTimeline();
        
        stage.show();
    }

    private HBox createModernHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e8eaf6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        header.setSpacing(20);

        // Logo
        javafx.scene.image.ImageView logoView = new javafx.scene.image.ImageView();
        try {
            javafx.scene.image.Image logoImage = new javafx.scene.image.Image(getClass().getResourceAsStream("/logo.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(44);
            logoView.setFitHeight(44);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load logo image: " + e.getMessage());
        }

        VBox titleBox = new VBox(2);
        Label title = new Label("MTProAuto");
        title.setStyle("-fx-text-fill: #1976D2; -fx-font-size: 22px; -fx-font-weight: bold;");
        
        Label subtitle = new Label("Hệ Thống Quản Lý");
        subtitle.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");
        
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField searchBox = new TextField();
        searchBox.setPromptText("🔍 Tìm kiếm...");
        searchBox.setPrefWidth(350);
        searchBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #424242;" +
            "-fx-prompt-text-fill: #757575;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10px 18px;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: 600;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 8;"
        );

        HBox userBox = new HBox(12);
        userBox.setAlignment(Pos.CENTER);
        
        Circle avatar = new Circle(18);
        avatar.setFill(Color.web("#2196F3"));
        
        StackPane avatarContainer = new StackPane();
        Label avatarText = new Label("A");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        avatarContainer.getChildren().addAll(avatar, avatarText);
        
        VBox userInfo = new VBox(2);
        Label userName = new Label("Admin");
        userName.setStyle("-fx-text-fill: #212121; -fx-font-weight: 600; -fx-font-size: 13px;");
        Label userRole = new Label("Quản trị viên");
        userRole.setStyle("-fx-text-fill: #757575; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userName, userRole);
        
        userBox.getChildren().addAll(avatarContainer, userInfo);

        header.getChildren().addAll(logoView, titleBox, spacer, searchBox, userBox);
        return header;
    }

    private VBox createModernSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e8eaf6;" +
            "-fx-border-width: 0 1 0 0;"
        );
        sidebar.setPrefWidth(240);

        Button btnDashboard = createModernMenuButton("🏠", "Trang Chủ", "#2196F3", true);
        Button btnAppointment = createModernMenuButton("📅", "Lịch Hẹn", "#2196F3", false);
        Button btnInvoice = createModernMenuButton("📄", "Hóa Đơn", "#2196F3", false);
        Button btnService = createModernMenuButton("🛠", "Dịch Vụ", "#2196F3", false);
        Button btnPackage = createModernMenuButton("📦", "Gói Dịch Vụ", "#2196F3", false);
        Button btnProduct = createModernMenuButton("🛒", "Sản Phẩm", "#2196F3", false);
        Button btnHR = createModernMenuButton("👥", "Nhân Sự", "#2196F3", false);
        Button btnReport = createModernMenuButton("📈", "Báo Cáo", "#2196F3", false);

        btnDashboard.setOnAction(e -> {
            resetMenuButtons();
            setActiveButton(btnDashboard);
            showDashboard();
        });
        btnAppointment.setOnAction(e -> {
            resetMenuButtons();
            setActiveButton(btnAppointment);
            showAppointmentManagement();
        });
        btnInvoice.setOnAction(e -> {
            resetMenuButtons();
            setActiveButton(btnInvoice);
            showInvoiceManagement();
        });
        btnService.setOnAction(e -> {
            resetMenuButtons();
            setActiveButton(btnService);
            showServiceManagement();
        });
        btnPackage.setOnAction(e -> {
            resetMenuButtons();
            setActiveButton(btnPackage);
            showPackageManagement();
        });
        btnProduct.setOnAction(e -> {
            resetMenuButtons();
            setActiveButton(btnProduct);
            showProductManagement();
        });
        btnReport.setOnAction(e -> {
            resetMenuButtons();
            setActiveButton(btnReport);
            showReport();
        });
        btnHR.setOnAction(e -> {
            resetMenuButtons();
            setActiveButton(btnHR);
            showHRManagement();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = createModernMenuButton("🚪", "Đăng Xuất", "#2196F3", false);
        btnLogout.setStyle(
            btnLogout.getStyle() + 
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1 0 0 0;" +
            "-fx-padding: 12px 12px 8px 12px;"
        );
        
        btnLogout.setOnAction(e -> handleLogout());

        sidebar.getChildren().addAll(
            btnDashboard, btnAppointment, btnInvoice, btnService, 
            btnPackage, btnProduct, btnHR, btnReport, spacer, btnLogout
        );

        return sidebar;
    }

    private Button createModernMenuButton(String icon, String text, String color, boolean active) {
        Button btn = new Button();
        
        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        iconLabel.setMinWidth(28);
        iconLabel.setMaxWidth(28);
        iconLabel.setAlignment(Pos.CENTER);
        
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 500;");
        
        content.getChildren().addAll(iconLabel, textLabel);
        btn.setGraphic(content);
        
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        
        if (active) {
            btn.setStyle(
                "-fx-background-color: #E3F2FD;" +
                "-fx-text-fill: #1976D2;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12px 12px;" +
                "-fx-cursor: hand;"
            );
            textLabel.setStyle(textLabel.getStyle() + "-fx-text-fill: #1976D2;");
        } else {
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #616161;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12px 12px;" +
                "-fx-cursor: hand;"
            );
            textLabel.setStyle(textLabel.getStyle() + "-fx-text-fill: #616161;");
        }
        
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#E3F2FD")) {
                btn.setStyle(btn.getStyle() + "-fx-background-color: #f5f5f5;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#E3F2FD")) {
                btn.setStyle(btn.getStyle().replace("-fx-background-color: #f5f5f5;", "-fx-background-color: transparent;"));
            }
        });
        
        return btn;
    }

    private void resetMenuButtons() {
        for (var node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                HBox content = (HBox) btn.getGraphic();
                if (content != null && content.getChildren().size() > 1) {
                    Label textLabel = (Label) content.getChildren().get(1);
                    
                    btn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: #616161;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10px 12px;" +
                        "-fx-cursor: hand;"
                    );
                    textLabel.setStyle(textLabel.getStyle().replace("-fx-text-fill: #1976D2;", "-fx-text-fill: #616161;"));
                }
            }
        }
    }

    private void setActiveButton(Button btn) {
        HBox content = (HBox) btn.getGraphic();
        Label textLabel = (Label) content.getChildren().get(1);
        
        btn.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-text-fill: #1976D2;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10px 12px;" +
            "-fx-cursor: hand;"
        );
        textLabel.setStyle(textLabel.getStyle() + "-fx-text-fill: #1976D2;");
    }

    private String adjustColor(String color) {
        return color.replace("67", "76").replace("ea", "4b");
    }

    private boolean matchesTimeFilters(String dateStr, String period, String month, String year) {
        if (dateStr == null || dateStr.trim().isEmpty()) return false;
        try {
            LocalDate date = LocalDate.parse(dateStr.substring(0, 10));
            LocalDate today = LocalDate.now();
            
            if (period != null && !period.contains("Tất cả")) {
                if (period.equals("Tuần này")) {
                    LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                    LocalDate endOfWeek = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
                    return !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek);
                } else if (period.equals("Tháng này")) {
                    return date.getYear() == today.getYear() && date.getMonthValue() == today.getMonthValue();
                } else if (period.equals("Quý này")) {
                    int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
                    int dateQuarter = (date.getMonthValue() - 1) / 3 + 1;
                    return date.getYear() == today.getYear() && dateQuarter == currentQuarter;
                } else if (period.equals("Năm nay")) {
                    return date.getYear() == today.getYear();
                }
            }
            
            if (year != null && !year.contains("Tất cả")) {
                int targetYear = Integer.parseInt(year);
                if (date.getYear() != targetYear) return false;
            }
            if (month != null && !month.contains("Tất cả")) {
                int targetMonth = Integer.parseInt(month.replace("Tháng ", ""));
                if (date.getMonthValue() != targetMonth) return false;
            }
            
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public List<String> getAvailableYears() {
        java.util.Set<String> years = new java.util.TreeSet<>(java.util.Comparator.reverseOrder());
        
        int curYear = LocalDate.now().getYear();
        for (int y = curYear; y >= 2020; y--) {
            years.add(String.valueOf(y));
        }
        
        try {
            List<Invoice> invoices = new InvoiceService().getAllInvoices();
            for (Invoice inv : invoices) {
                if (inv.getCreatedAt() != null && inv.getCreatedAt().length() >= 4) {
                    years.add(inv.getCreatedAt().substring(0, 4));
                }
            }
        } catch (Exception e) {}
        
        try {
            List<model.Appointment> appts = new service.AppointmentService().getAllAppointments();
            for (model.Appointment appt : appts) {
                if (appt.getAppointmentDate() != null && appt.getAppointmentDate().length() >= 4) {
                    years.add(appt.getAppointmentDate().substring(0, 4));
                }
            }
        } catch (Exception e) {}
        
        return new java.util.ArrayList<>(years);
    }

    private void showDashboard() {
        currentView = "dashboard";
        VBox dashboard = new VBox(30);
        dashboard.setPadding(new Insets(30));

        Label title = new Label("■ Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");

        // Load statistics from database
        InvoiceService invoiceService = new InvoiceService();
        List<Invoice> allInvoices = invoiceService.getAllInvoices();
        
        // Calculate today's revenue (invoices with status "paid")
        double todayRevenue = allInvoices.stream()
            .filter(inv -> inv.getStatus().equals("paid"))
            .mapToDouble(Invoice::getTotalAmount)
            .sum();
        
        // Count total invoices
        int totalInvoices = allInvoices.size();
        
        // Count unique customers (by phone number)
        long uniqueCustomers = allInvoices.stream()
            .map(Invoice::getPhone)
            .filter(phone -> phone != null && !phone.trim().isEmpty())
            .distinct()
            .count();

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        VBox card1 = createCleanStatCard("Doanh Thu Hôm Nay", "Doanh Thu Hôm Nay", 
            String.format("%,.0f đ", todayRevenue), "#2196F3");
        VBox card2 = createCleanStatCard("Hóa Đơn", "Hóa Đơn", 
            String.valueOf(totalInvoices), "#2196F3");
        VBox card3 = createCleanStatCard("Khách Hàng", "Khách Hàng", 
            String.valueOf(uniqueCustomers), "#2196F3");

        statsGrid.add(card1, 0, 0);
        statsGrid.add(card2, 1, 0);
        statsGrid.add(card3, 2, 0);

        Label quickTitle = new Label("⚡ Thao Tác Nhanh");
        quickTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121;");

        HBox quickActions = new HBox(12);
        quickActions.setAlignment(Pos.CENTER_LEFT);

        Button btnNewInvoice = createCleanButton("+ Tạo Hóa Đơn Mới", "#2196F3");
        Button btnViewServices = createCleanButton("⚙ Xem Dịch Vụ", "#2196F3");
        Button btnViewPackages = createCleanButton("📦 Xem Gói", "#2196F3");

        btnNewInvoice.setOnAction(e -> {
            CreateInvoiceForm form = new CreateInvoiceForm(() -> refreshInvoiceTable(invoiceTableRows));
            form.show();
        });
        btnViewServices.setOnAction(e -> showServiceManagement());
        btnViewPackages.setOnAction(e -> showPackageManagement());

        quickActions.getChildren().addAll(btnNewInvoice, btnViewServices, btnViewPackages);

        VBox todayApptsSection = createTodayAppointmentsSection();
        dashboard.getChildren().addAll(title, statsGrid, quickTitle, quickActions, todayApptsSection);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboard);
    }

    private VBox createGradientStatCard(String icon, String title, String value, String color1, String color2) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: linear-gradient(135deg, " + color1 + " 0%, " + color2 + " 100%);" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);"
        );
        card.setPrefWidth(280);
        card.setPrefHeight(140);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 40px;");

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.9); -fx-font-weight: 500;");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;");

        card.getChildren().addAll(iconLabel, lblTitle, lblValue);
        return card;
    }

    private Button createGradientButton(String text, String color1, String color2) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: linear-gradient(to right, " + color1 + ", " + color2 + ");" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 15px 30px;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        
        btn.setOnMouseEntered(e -> btn.setOpacity(0.9));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        
        return btn;
    }

    private VBox createCleanStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);"
        );
        card.setPrefWidth(260);
        card.setPrefHeight(160);

        HBox iconBox = new HBox();
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-background-radius: 14;" +
            "-fx-pref-width: 140;" +
            "-fx-pref-height: 60;"
        );
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        iconLabel.setAlignment(Pos.CENTER);
        iconBox.getChildren().add(iconLabel);

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        card.getChildren().addAll(iconBox, lblValue);
        return card;
    }

    private Button createCleanButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 24px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        btn.setOnMouseEntered(e -> btn.setOpacity(0.9));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        
        return btn;
    }

    private void showInvoiceManagement() {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("🧾 Quản Lý Hóa Đơn");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Create tableRows container first
        invoiceTableRows = new VBox(0);
        
        Button btnNew = new Button("+ Tạo Hóa Đơn Mới");
        btnNew.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 24px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnNew.setOnMouseEntered(e -> btnNew.setOpacity(0.9));
        btnNew.setOnMouseExited(e -> btnNew.setOpacity(1.0));
        btnNew.setOnAction(e -> {
            CreateInvoiceForm form = new CreateInvoiceForm(() -> refreshInvoiceTable(invoiceTableRows));
            form.show();
        });
        
        header.getChildren().addAll(title, spacer, btnNew);

        // Search and filter bar
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(20));
        filterBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Tìm kiếm hóa đơn...");
        searchField.setPrefWidth(200);
        searchField.setMinWidth(200);
        searchField.setMaxWidth(200);
        searchField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        
        ComboBox<String> cbStatusFilter = new ComboBox<>();
        cbStatusFilter.getItems().addAll("Tất cả trạng thái", "Đã thanh toán", "Chưa thanh toán");
        cbStatusFilter.setValue("Tất cả trạng thái");
        cbStatusFilter.setPrefWidth(150);
        cbStatusFilter.setMinWidth(150);
        cbStatusFilter.setMaxWidth(150);
        cbStatusFilter.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        ComboBox<String> cbPeriod = new ComboBox<>();
        cbPeriod.getItems().addAll("Tất cả mốc thời gian", "Tuần này", "Tháng này", "Quý này", "Năm nay");
        cbPeriod.setValue("Tất cả mốc thời gian");
        cbPeriod.setPrefWidth(180);
        cbPeriod.setMinWidth(180);
        cbPeriod.setMaxWidth(180);
        cbPeriod.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        ComboBox<String> cbMonth = new ComboBox<>();
        cbMonth.getItems().add("Tất cả các tháng");
        for (int m = 1; m <= 12; m++) {
            cbMonth.getItems().add("Tháng " + m);
        }
        cbMonth.setValue("Tất cả các tháng");
        cbMonth.setPrefWidth(150);
        cbMonth.setMinWidth(150);
        cbMonth.setMaxWidth(150);
        cbMonth.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        ComboBox<String> cbYear = new ComboBox<>();
        cbYear.getItems().add("Tất cả các năm");
        cbYear.getItems().addAll(getAvailableYears());
        cbYear.setValue("Tất cả các năm");
        cbYear.setPrefWidth(140);
        cbYear.setMinWidth(140);
        cbYear.setMaxWidth(140);
        cbYear.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        Runnable triggerFilter = () -> {
            refreshInvoiceTableWithFilter(
                invoiceTableRows,
                searchField.getText(),
                cbStatusFilter.getValue(),
                cbPeriod.getValue(),
                cbMonth.getValue(),
                cbYear.getValue()
            );
        };
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> triggerFilter.run());
        cbStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> triggerFilter.run());
        cbPeriod.valueProperty().addListener((obs, old, newVal) -> triggerFilter.run());
        cbMonth.valueProperty().addListener((obs, old, newVal) -> triggerFilter.run());
        cbYear.valueProperty().addListener((obs, old, newVal) -> triggerFilter.run());
        
        filterBar.getChildren().addAll(searchField, cbStatusFilter, cbPeriod, cbMonth, cbYear);

        // Table
        VBox tableContainer = new VBox(0);
        tableContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label tableTitle = new Label("Danh Sách Hóa Đơn");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0;");
        
        // Table header
        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 0, 12, 0));
        tableHeader.setStyle(
            "-fx-background-color: #F9FAFB;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        Label hMaHD = new Label("Mã HĐ");
        hMaHD.setPrefWidth(70);
        hMaHD.setMinWidth(70);
        hMaHD.setMaxWidth(70);
        hMaHD.setPadding(new Insets(0, 5, 0, 5));
        hMaHD.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hMaHD.setAlignment(Pos.CENTER_LEFT);
        
        Label hKhachHang = new Label("Khách Hàng");
        hKhachHang.setPrefWidth(150);
        hKhachHang.setMinWidth(150);
        hKhachHang.setMaxWidth(150);
        hKhachHang.setPadding(new Insets(0, 5, 0, 5));
        hKhachHang.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hKhachHang.setAlignment(Pos.CENTER_LEFT);
        
        Label hDichVu = new Label("Dịch Vụ");
        hDichVu.setPrefWidth(120);
        hDichVu.setMinWidth(120);
        hDichVu.setMaxWidth(120);
        hDichVu.setPadding(new Insets(0, 5, 0, 5));
        hDichVu.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hDichVu.setAlignment(Pos.CENTER_LEFT);
        
        Label hGhiChu = new Label("Ghi Chú");
        hGhiChu.setPrefWidth(200);
        hGhiChu.setMinWidth(150);
        hGhiChu.setMaxWidth(Double.MAX_VALUE);
        hGhiChu.setPadding(new Insets(0, 5, 0, 5));
        hGhiChu.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hGhiChu.setAlignment(Pos.CENTER_LEFT);
        
        Label hTongTien = new Label("Tổng Tiền");
        hTongTien.setPrefWidth(120);
        hTongTien.setMinWidth(120);
        hTongTien.setMaxWidth(120);
        hTongTien.setPadding(new Insets(0, 5, 0, 5));
        hTongTien.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hTongTien.setAlignment(Pos.CENTER_RIGHT);
        
        Label hTrangThai = new Label("Trạng Thái");
        hTrangThai.setPrefWidth(130);
        hTrangThai.setMinWidth(130);
        hTrangThai.setMaxWidth(130);
        hTrangThai.setPadding(new Insets(0, 5, 0, 5));
        hTrangThai.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hTrangThai.setAlignment(Pos.CENTER);
        
        Label hThaoTac = new Label("Thao Tác");
        hThaoTac.setPrefWidth(150);
        hThaoTac.setMinWidth(150);
        hThaoTac.setMaxWidth(150);
        hThaoTac.setPadding(new Insets(0, 5, 0, 5));
        hThaoTac.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hThaoTac.setAlignment(Pos.CENTER);
        
        tableHeader.getChildren().addAll(hMaHD, hKhachHang, hDichVu, hGhiChu, hTongTien, hTrangThai, hThaoTac);
        HBox.setHgrow(hGhiChu, Priority.ALWAYS);
        
        // Load data from database
        refreshInvoiceTable(invoiceTableRows);
        
        // Wrap invoiceTableRows in ScrollPane for scrolling
        ScrollPane scrollPane = new ScrollPane(invoiceTableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        tableContainer.getChildren().addAll(tableTitle, tableHeader, scrollPane);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        view.getChildren().addAll(header, filterBar, tableContainer);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }
    
    private void refreshInvoiceTable(VBox tableRows) {
        refreshInvoiceTableWithFilter(tableRows, "", "", "Tất cả", "Tất cả các tháng", "Tất cả các năm");
    }
    
    private void refreshInvoiceTableWithFilter(VBox tableRows, String searchText, String status, String period, String month, String year) {
        tableRows.getChildren().clear();
        InvoiceService invoiceService = new InvoiceService();
        List<Invoice> invoices = invoiceService.getAllInvoices();
        
        // Filter by status
        if (status != null && !status.trim().isEmpty()) {
            invoices = invoices.stream()
                .filter(i -> i.getStatus().equals(status))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Filter by time filters
        invoices = invoices.stream()
            .filter(i -> matchesTimeFilters(i.getCreatedAt(), period, month, year))
            .collect(java.util.stream.Collectors.toList());
        
        // Filter by search text
        if (searchText != null && !searchText.trim().isEmpty()) {
            String search = searchText.toLowerCase().trim();
            invoices = invoices.stream()
                .filter(i -> i.getCustomerName().toLowerCase().contains(search) || 
                            (i.getPhone() != null && i.getPhone().contains(search)) ||
                            (i.getLicensePlate() != null && i.getLicensePlate().toLowerCase().contains(search)))
                .collect(java.util.stream.Collectors.toList());
        }
        
        if (invoices.isEmpty()) {
            Label emptyState = new Label("Không tìm thấy hóa đơn nào");
            emptyState.setStyle("-fx-font-size: 14px; -fx-text-fill: #9e9e9e; -fx-padding: 40px;");
            tableRows.getChildren().add(emptyState);
        } else {
            for (Invoice invoice : invoices) {
                tableRows.getChildren().add(createInvoiceRow(
                    invoice.getId(),
                    invoice.getCustomerName(),
                    "Dịch vụ",
                    String.format("%.0f đ", invoice.getTotalAmount()),
                    invoice.getStatus(),
                    invoice.getNotes(),
                    tableRows
                ));
            }
        }
    }
    
    private HBox createInvoiceRow(int id, String customerName, String service, String totalAmount, String status, String notes, VBox tableRows) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0));
        row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #f9fafb;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        
        Label lblId = new Label(String.format("%05d", id));
        lblId.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121; -fx-font-weight: 500;");
        lblId.setPrefWidth(70);
        lblId.setMinWidth(70);
        lblId.setMaxWidth(70);
        lblId.setPadding(new Insets(12, 5, 12, 5));
        lblId.setAlignment(Pos.CENTER_LEFT);
        
        Label lblCustomer = new Label(customerName);
        lblCustomer.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121;");
        lblCustomer.setPrefWidth(150);
        lblCustomer.setMinWidth(150);
        lblCustomer.setMaxWidth(150);
        lblCustomer.setPadding(new Insets(12, 5, 12, 5));
        lblCustomer.setAlignment(Pos.CENTER_LEFT);
        
        Label lblService = new Label(service);
        lblService.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        lblService.setPrefWidth(120);
        lblService.setMinWidth(120);
        lblService.setMaxWidth(120);
        lblService.setPadding(new Insets(12, 5, 12, 5));
        lblService.setAlignment(Pos.CENTER_LEFT);
        
        Label lblNotes = new Label(notes != null ? notes : "");
        lblNotes.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        lblNotes.setPrefWidth(200);
        lblNotes.setMinWidth(150);
        lblNotes.setMaxWidth(Double.MAX_VALUE);
        lblNotes.setPadding(new Insets(12, 5, 12, 5));
        lblNotes.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTotal = new Label(totalAmount);
        lblTotal.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        lblTotal.setPrefWidth(120);
        lblTotal.setMinWidth(120);
        lblTotal.setMaxWidth(120);
        lblTotal.setPadding(new Insets(12, 5, 12, 5));
        lblTotal.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblStatus = new Label(status.equals("nhap") ? "Chưa thanh toán" : "Đã thanh toán");
        lblStatus.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + (status.equals("nhap") ? "#f44336" : "#4CAF50") + ";" +
            "-fx-background-color: " + (status.equals("nhap") ? "#FFEBEE" : "#E8F5E9") + ";" +
            "-fx-padding: 4px 10px;" +
            "-fx-background-radius: 6;"
        );
        lblStatus.setPrefWidth(130);
        lblStatus.setMinWidth(130);
        lblStatus.setMaxWidth(130);
        lblStatus.setAlignment(Pos.CENTER);
        
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWidth(150);
        actions.setMinWidth(150);
        actions.setMaxWidth(150);
        actions.setPadding(new Insets(12, 5, 12, 5));
        
        Button btnPDF = new Button("Xuất");
        btnPDF.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10px 18px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 60;"
        );
        btnPDF.setOnAction(e -> {
            InvoiceService invoiceService = new InvoiceService();
            Invoice invoice = invoiceService.getInvoiceById(id);
            if (invoice != null) {
                exportInvoiceToPDF(invoice);
            }
        });
        
        Button btnView = new Button("👁");
        btnView.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 10px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 32;" +
            "-fx-min-height: 32;"
        );
        btnView.setOnAction(e -> {
            showInvoiceDetail(id, tableRows);
        });
        
        Button btnDelete = new Button("🗑");
        btnDelete.setStyle(
            "-fx-background-color: #FFEBEE;" +
            "-fx-text-fill: #f44336;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 10px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 32;" +
            "-fx-min-height: 32;"
        );
        btnDelete.setOnAction(e -> {
            showDeleteConfirmation("hóa đơn", "HĐ #" + String.format("%05d", id), () -> {
                InvoiceService invoiceService = new InvoiceService();
                if (invoiceService.deleteInvoice(id)) {
                    refreshInvoiceTable(tableRows);
                } else {
                    showErrorAlert("Lỗi", "Không thể xóa hóa đơn!");
                }
            });
        });
        
        actions.getChildren().addAll(btnPDF, btnView, btnDelete);
        
        row.getChildren().addAll(lblId, lblCustomer, lblService, lblNotes, lblTotal, lblStatus, actions);
        HBox.setHgrow(lblNotes, Priority.ALWAYS);
        return row;
    }

    private void showServiceManagement() {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("🔧 Quản Lý Dịch Vụ");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Create tableRows container first so it can be referenced in button action
        VBox tableRows = new VBox(0);
        
        Button btnNew = new Button("+ Thêm Dịch Vụ");
        btnNew.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 24px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnNew.setOnMouseEntered(e -> btnNew.setOpacity(0.9));
        btnNew.setOnMouseExited(e -> btnNew.setOpacity(1.0));
        btnNew.setOnAction(e -> {
            ServiceForm form = new ServiceForm(() -> refreshServiceTable(tableRows));
            form.show();
        });
        
        header.getChildren().addAll(title, spacer, btnNew);

        // Search bar
        HBox searchBar = new HBox(15);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(20));
        searchBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Tìm kiếm dịch vụ...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        
        // Add search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            refreshServiceTableWithSearch(tableRows, newVal);
        });
        
        searchBar.getChildren().add(searchField);

        // Table container
        VBox tableContainer = new VBox(0);
        tableContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label tableTitle = new Label("Danh Sách Dịch Vụ");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0;");
        
        // Table header with fixed widths
        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 0, 12, 0));
        tableHeader.setStyle(
            "-fx-background-color: #F9FAFB;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        Label hName = new Label("Tên Dịch Vụ");
        hName.setPrefWidth(150);
        hName.setMinWidth(120);
        hName.setPadding(new Insets(0, 10, 0, 15));
        hName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hName.setAlignment(Pos.CENTER_LEFT);
        
        Label hDesc = new Label("Mô Tả");
        hDesc.setPrefWidth(150);
        hDesc.setMinWidth(120);
        hDesc.setMaxWidth(Double.MAX_VALUE);
        hDesc.setPadding(new Insets(0, 10, 0, 10));
        hDesc.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hDesc.setAlignment(Pos.CENTER_LEFT);
        
        Label hPriceMini = new Label("Mini");
        hPriceMini.setPrefWidth(75);
        hPriceMini.setMinWidth(70);
        hPriceMini.setMaxWidth(80);
        hPriceMini.setPadding(new Insets(0, 4, 0, 4));
        hPriceMini.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #1976D2;");
        hPriceMini.setAlignment(Pos.CENTER_RIGHT);
        
        Label hPriceSedan = new Label("Sedan");
        hPriceSedan.setPrefWidth(75);
        hPriceSedan.setMinWidth(70);
        hPriceSedan.setMaxWidth(80);
        hPriceSedan.setPadding(new Insets(0, 4, 0, 4));
        hPriceSedan.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #388E3C;");
        hPriceSedan.setAlignment(Pos.CENTER_RIGHT);
        
        Label hPriceCuv = new Label("CUV");
        hPriceCuv.setPrefWidth(75);
        hPriceCuv.setMinWidth(70);
        hPriceCuv.setMaxWidth(80);
        hPriceCuv.setPadding(new Insets(0, 4, 0, 4));
        hPriceCuv.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #F57C00;");
        hPriceCuv.setAlignment(Pos.CENTER_RIGHT);
        
        Label hPriceSuv = new Label("SUV");
        hPriceSuv.setPrefWidth(75);
        hPriceSuv.setMinWidth(70);
        hPriceSuv.setMaxWidth(80);
        hPriceSuv.setPadding(new Insets(0, 4, 0, 4));
        hPriceSuv.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #C2185B;");
        hPriceSuv.setAlignment(Pos.CENTER_RIGHT);
        
        Label hPricePickup = new Label("Pickup");
        hPricePickup.setPrefWidth(75);
        hPricePickup.setMinWidth(70);
        hPricePickup.setMaxWidth(80);
        hPricePickup.setPadding(new Insets(0, 4, 0, 4));
        hPricePickup.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #7B1FA2;");
        hPricePickup.setAlignment(Pos.CENTER_RIGHT);
        
        Label hPriceMpv = new Label("MPV");
        hPriceMpv.setPrefWidth(75);
        hPriceMpv.setMinWidth(70);
        hPriceMpv.setMaxWidth(80);
        hPriceMpv.setPadding(new Insets(0, 4, 0, 4));
        hPriceMpv.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #00838F;");
        hPriceMpv.setAlignment(Pos.CENTER_RIGHT);
        
        Label hAction = new Label("Thao Tác");
        hAction.setPrefWidth(85);
        hAction.setMinWidth(80);
        hAction.setMaxWidth(90);
        hAction.setPadding(new Insets(0, 5, 0, 5));
        hAction.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hAction.setAlignment(Pos.CENTER);
        
        tableHeader.getChildren().addAll(hName, hDesc, hPriceMini, hPriceSedan, hPriceCuv, hPriceSuv, hPriceMpv, hPricePickup, hAction);
        HBox.setHgrow(hDesc, Priority.ALWAYS);
        
        // Load data from database
        refreshServiceTable(tableRows);
        
        // Wrap tableRows in ScrollPane for scrolling
        ScrollPane scrollPane = new ScrollPane(tableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        tableContainer.getChildren().addAll(tableTitle, tableHeader, scrollPane);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        view.getChildren().addAll(header, searchBar, tableContainer);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }
    
    private void refreshServiceTable(VBox tableRows) {
        refreshServiceTableWithSearch(tableRows, "");
    }
    
    private void refreshServiceTableWithSearch(VBox tableRows, String searchText) {
        tableRows.getChildren().clear();
        ServiceService serviceService = new ServiceService();
        List<Service> services = serviceService.getAllServices();
        
        // Filter by search text
        if (searchText != null && !searchText.trim().isEmpty()) {
            String search = searchText.toLowerCase().trim();
            services = services.stream()
                .filter(s -> s.getName().toLowerCase().contains(search) || 
                            (s.getDescription() != null && s.getDescription().toLowerCase().contains(search)))
                .collect(java.util.stream.Collectors.toList());
        }
        
        if (services.isEmpty()) {
            Label emptyState = new Label(searchText != null && !searchText.trim().isEmpty() ? 
                "Không tìm thấy dịch vụ nào" : "Chưa có dịch vụ nào");
            emptyState.setStyle("-fx-font-size: 14px; -fx-text-fill: #9e9e9e; -fx-padding: 40px;");
            tableRows.getChildren().add(emptyState);
        } else {
            for (Service service : services) {
                tableRows.getChildren().add(createServiceRow(service, tableRows));
            }
        }
    }

    private HBox createServiceRow(Service service, VBox tableRows) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0));
        row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #f9fafb;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        
        // Name cell
        Label lblName = new Label(service.getName());
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121; -fx-font-weight: 500;");
        lblName.setPrefWidth(150);
        lblName.setMinWidth(120);
        lblName.setPadding(new Insets(12, 10, 12, 15));
        lblName.setAlignment(Pos.CENTER_LEFT);
        
        // Description cell
        Label lblDesc = new Label(service.getDescription());
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        lblDesc.setPrefWidth(150);
        lblDesc.setMinWidth(120);
        lblDesc.setMaxWidth(Double.MAX_VALUE);
        lblDesc.setPadding(new Insets(12, 10, 12, 10));
        lblDesc.setAlignment(Pos.CENTER_LEFT);
        lblDesc.setWrapText(true);
        
        // Price cells for 5 vehicle types
        Label lblPriceMini = new Label(String.format("%.0f đ", service.getPriceMini()));
        lblPriceMini.setStyle("-fx-font-size: 13px; -fx-text-fill: #1976D2; -fx-font-weight: 600;");
        lblPriceMini.setPrefWidth(75);
        lblPriceMini.setMinWidth(70);
        lblPriceMini.setMaxWidth(80);
        lblPriceMini.setPadding(new Insets(12, 4, 12, 4));
        lblPriceMini.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblPriceSedan = new Label(String.format("%.0f đ", service.getPriceSedan()));
        lblPriceSedan.setStyle("-fx-font-size: 13px; -fx-text-fill: #388E3C; -fx-font-weight: 600;");
        lblPriceSedan.setPrefWidth(75);
        lblPriceSedan.setMinWidth(70);
        lblPriceSedan.setMaxWidth(80);
        lblPriceSedan.setPadding(new Insets(12, 4, 12, 4));
        lblPriceSedan.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblPriceCuv = new Label(String.format("%.0f đ", service.getPriceCuv()));
        lblPriceCuv.setStyle("-fx-font-size: 13px; -fx-text-fill: #F57C00; -fx-font-weight: 600;");
        lblPriceCuv.setPrefWidth(75);
        lblPriceCuv.setMinWidth(70);
        lblPriceCuv.setMaxWidth(80);
        lblPriceCuv.setPadding(new Insets(12, 4, 12, 4));
        lblPriceCuv.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblPriceSuv = new Label(String.format("%.0f đ", service.getPriceSuv()));
        lblPriceSuv.setStyle("-fx-font-size: 13px; -fx-text-fill: #C2185B; -fx-font-weight: 600;");
        lblPriceSuv.setPrefWidth(75);
        lblPriceSuv.setMinWidth(70);
        lblPriceSuv.setMaxWidth(80);
        lblPriceSuv.setPadding(new Insets(12, 4, 12, 4));
        lblPriceSuv.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblPricePickup = new Label(String.format("%.0f đ", service.getPricePickup()));
        lblPricePickup.setStyle("-fx-font-size: 13px; -fx-text-fill: #7B1FA2; -fx-font-weight: 600;");
        lblPricePickup.setPrefWidth(75);
        lblPricePickup.setMinWidth(70);
        lblPricePickup.setMaxWidth(80);
        lblPricePickup.setPadding(new Insets(12, 4, 12, 4));
        lblPricePickup.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblPriceMpv = new Label(String.format("%.0f đ", service.getPriceMpv()));
        lblPriceMpv.setStyle("-fx-font-size: 13px; -fx-text-fill: #00838F; -fx-font-weight: 600;");
        lblPriceMpv.setPrefWidth(75);
        lblPriceMpv.setMinWidth(70);
        lblPriceMpv.setMaxWidth(80);
        lblPriceMpv.setPadding(new Insets(12, 4, 12, 4));
        lblPriceMpv.setAlignment(Pos.CENTER_RIGHT);
        
        // Actions cell
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWidth(85);
        actions.setMinWidth(80);
        actions.setMaxWidth(90);
        actions.setPadding(new Insets(12, 5, 12, 5));
        
        Button btnEdit = new Button("✏");
        btnEdit.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 10px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 32;" +
            "-fx-min-height: 32;"
        );
        btnEdit.setOnAction(e -> {
            ServiceForm form = new ServiceForm(service.getId(), service, () -> refreshServiceTable(tableRows));
            form.show();
        });
        
        Button btnDelete = new Button("🗑");
        btnDelete.setStyle(
            "-fx-background-color: #FFEBEE;" +
            "-fx-text-fill: #f44336;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 10px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 32;" +
            "-fx-min-height: 32;"
        );
        btnDelete.setOnAction(e -> {
            showDeleteConfirmation("dịch vụ", service.getName(), () -> {
                ServiceService serviceService = new ServiceService();
                if (serviceService.deleteService(service.getId())) {
                    refreshServiceTable(tableRows);
                } else {
                    showErrorAlert("Lỗi", "Không thể xóa dịch vụ!");
                }
            });
        });
        
        actions.getChildren().addAll(btnEdit, btnDelete);
        
        row.getChildren().addAll(lblName, lblDesc, lblPriceMini, lblPriceSedan, lblPriceCuv, lblPriceSuv, lblPriceMpv, lblPricePickup, actions);
        HBox.setHgrow(lblDesc, Priority.ALWAYS);
        return row;
    }

    private void showPackageManagement() {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📦 Quản Lý Gói Dịch Vụ");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Create tableRows container first so it can be referenced in button action
        VBox tableRows = new VBox(0);
        
        Button btnNew = new Button("+ Tạo Gói Mới");
        btnNew.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 24px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnNew.setOnMouseEntered(e -> btnNew.setOpacity(0.9));
        btnNew.setOnMouseExited(e -> btnNew.setOpacity(1.0));
        btnNew.setOnAction(e -> {
            PackageForm form = new PackageForm(() -> refreshPackageTable(tableRows));
            form.show();
        });
        
        header.getChildren().addAll(title, spacer, btnNew);

        // Search bar
        HBox searchBar = new HBox(15);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(20));
        searchBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Tìm kiếm gói dịch vụ...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        
        // Add search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            refreshPackageTableWithSearch(tableRows, newVal);
        });
        
        searchBar.getChildren().add(searchField);

        // Table container
        VBox tableContainer = new VBox(0);
        tableContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label tableTitle = new Label("Danh Sách Gói Dịch Vụ");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0;");
        
        // Table header
        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 0, 12, 0));
        tableHeader.setStyle(
            "-fx-background-color: #F9FAFB;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        Label hName = new Label("Tên Gói");
        hName.setPrefWidth(150);
        hName.setMinWidth(150);
        hName.setPadding(new Insets(0, 10, 0, 15));
        hName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hName.setAlignment(Pos.CENTER_LEFT);
        
        Label hDesc = new Label("Mô Tả");
        hDesc.setPrefWidth(300);
        hDesc.setMinWidth(150);
        hDesc.setMaxWidth(Double.MAX_VALUE);
        hDesc.setPadding(new Insets(0, 10, 0, 10));
        hDesc.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hDesc.setAlignment(Pos.CENTER_LEFT);
        
        Label hPriceRange = new Label("Khoảng Giá");
        hPriceRange.setPrefWidth(150);
        hPriceRange.setMinWidth(150);
        hPriceRange.setPadding(new Insets(0, 10, 0, 10));
        hPriceRange.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hPriceRange.setAlignment(Pos.CENTER_RIGHT);
        
        Label hSavings = new Label("Tiết Kiệm");
        hSavings.setPrefWidth(100);
        hSavings.setMinWidth(100);
        hSavings.setPadding(new Insets(0, 10, 0, 10));
        hSavings.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #4CAF50;");
        hSavings.setAlignment(Pos.CENTER_RIGHT);
        
        Label hStatus = new Label("Trạng Thái");
        hStatus.setPrefWidth(100);
        hStatus.setMinWidth(100);
        hStatus.setPadding(new Insets(0, 10, 0, 10));
        hStatus.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hStatus.setAlignment(Pos.CENTER);
        
        Label hAction = new Label("Thao Tác");
        hAction.setPrefWidth(120);
        hAction.setMinWidth(120);
        hAction.setPadding(new Insets(0, 15, 0, 10));
        hAction.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hAction.setAlignment(Pos.CENTER);
        
        tableHeader.getChildren().addAll(hName, hDesc, hPriceRange, hSavings, hStatus, hAction);
        HBox.setHgrow(hDesc, Priority.ALWAYS);
        
        // Load data from database
        refreshPackageTable(tableRows);
        
        // Wrap tableRows in ScrollPane for scrolling
        ScrollPane scrollPane = new ScrollPane(tableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        tableContainer.getChildren().addAll(tableTitle, tableHeader, scrollPane);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        view.getChildren().addAll(header, searchBar, tableContainer);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }
    
    private void refreshPackageTable(VBox tableRows) {
        refreshPackageTableWithSearch(tableRows, "");
    }
    
    private void refreshPackageTableWithSearch(VBox tableRows, String searchText) {
        tableRows.getChildren().clear();
        PackageService packageService = new PackageService();
        List<Package> packages = packageService.getAllPackages();
        
        // Filter by search text
        if (searchText != null && !searchText.trim().isEmpty()) {
            String search = searchText.toLowerCase().trim();
            packages = packages.stream()
                .filter(p -> p.getName().toLowerCase().contains(search) || 
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(search)))
                .collect(java.util.stream.Collectors.toList());
        }
        
        if (packages.isEmpty()) {
            Label emptyState = new Label(searchText != null && !searchText.trim().isEmpty() ? 
                "Không tìm thấy gói dịch vụ nào" : "Chưa có gói dịch vụ nào");
            emptyState.setStyle("-fx-font-size: 14px; -fx-text-fill: #9e9e9e; -fx-padding: 40px;");
            tableRows.getChildren().add(emptyState);
        } else {
            for (Package pkg : packages) {
                tableRows.getChildren().add(createPackageRow(pkg, tableRows));
            }
        }
    }

    private void showProductManagement() {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("🛒 Quản Lý Sản Phẩm");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Create tableRows container first so it can be referenced in button action
        VBox tableRows = new VBox(0);
        
        Button btnNew = new Button("+ Thêm Sản Phẩm");
        btnNew.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 24px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnNew.setOnMouseEntered(e -> btnNew.setOpacity(0.9));
        btnNew.setOnMouseExited(e -> btnNew.setOpacity(1.0));
        btnNew.setOnAction(e -> {
            ProductForm form = new ProductForm(() -> refreshProductTable(tableRows));
            form.show();
        });
        
        header.getChildren().addAll(title, spacer, btnNew);

        // Search bar
        HBox searchBar = new HBox(15);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(20));
        searchBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Tìm kiếm sản phẩm...");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(40);
        searchField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        
        // Category filter buttons
        HBox categoryButtons = new HBox(8);
        categoryButtons.setAlignment(Pos.CENTER_LEFT);
        Button btnAllCat = createFilterButton("Tất cả", true);
        Button btnWater = createFilterButton("Nước rửa xe", false);
        Button btnSolution = createFilterButton("Dung dịch", false);
        Button btnAccessory = createFilterButton("Phụ kiện", false);
        
        // ComboBox for status filter
        ComboBox<String> statusFilterBox = new ComboBox<>();
        statusFilterBox.setPrefHeight(40);
        statusFilterBox.getItems().addAll("Tất cả trạng thái", "Đang bán", "Sắp hết hàng", "Hết hàng", "Tạm dừng");
        statusFilterBox.setValue("Tất cả trạng thái");
        statusFilterBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 8px 12px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        
        // Store current filter
        final String[] currentCategory = {""};
        final String[] currentStatus = {"Tất cả trạng thái"};
        
        // Add search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            refreshProductTableWithFilter(tableRows, newVal, currentCategory[0], currentStatus[0]);
        });
        
        // Add filter button actions
        btnAllCat.setOnAction(e -> {
            currentCategory[0] = "";
            updateFilterButtonStyles(btnAllCat, btnWater, btnSolution, btnAccessory);
            refreshProductTableWithFilter(tableRows, searchField.getText(), "", currentStatus[0]);
        });
        
        btnWater.setOnAction(e -> {
            currentCategory[0] = "Nước rửa xe";
            updateFilterButtonStyles(btnWater, btnAllCat, btnSolution, btnAccessory);
            refreshProductTableWithFilter(tableRows, searchField.getText(), "Nước rửa xe", currentStatus[0]);
        });
        
        btnSolution.setOnAction(e -> {
            currentCategory[0] = "Dung dịch";
            updateFilterButtonStyles(btnSolution, btnAllCat, btnWater, btnAccessory);
            refreshProductTableWithFilter(tableRows, searchField.getText(), "Dung dịch", currentStatus[0]);
        });
        
        btnAccessory.setOnAction(e -> {
            currentCategory[0] = "Phụ kiện";
            updateFilterButtonStyles(btnAccessory, btnAllCat, btnWater, btnSolution);
            refreshProductTableWithFilter(tableRows, searchField.getText(), "Phụ kiện", currentStatus[0]);
        });
        
        // Add status filter action
        statusFilterBox.setOnAction(e -> {
            currentStatus[0] = statusFilterBox.getValue();
            refreshProductTableWithFilter(tableRows, searchField.getText(), currentCategory[0], currentStatus[0]);
        });
        
        categoryButtons.getChildren().addAll(btnAllCat, btnWater, btnSolution, btnAccessory);
        
        Region searchSpacer = new Region();
        HBox.setHgrow(searchSpacer, Priority.ALWAYS);
        
        searchBar.getChildren().addAll(searchField, categoryButtons, searchSpacer, statusFilterBox);

        // Table container
        VBox tableContainer = new VBox(0);
        tableContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label tableTitle = new Label("Danh Sách Sản Phẩm");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0;");
        
        // Table header
        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 0, 12, 0));
        tableHeader.setStyle(
            "-fx-background-color: #F9FAFB;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        Label hName = new Label("Tên Sản Phẩm");
        hName.setPrefWidth(350);
        hName.setMinWidth(100);
        hName.setMaxWidth(Double.MAX_VALUE);
        hName.setPadding(new Insets(0, 15, 0, 15));
        hName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hName.setAlignment(Pos.CENTER_LEFT);
        
        Label hCategory = new Label("Danh Mục");
        hCategory.setPrefWidth(150);
        hCategory.setMinWidth(150);
        hCategory.setPadding(new Insets(0, 15, 0, 15));
        hCategory.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hCategory.setAlignment(Pos.CENTER_LEFT);
        
        Label hPrice = new Label("Giá Bán");
        hPrice.setPrefWidth(120);
        hPrice.setMinWidth(120);
        hPrice.setPadding(new Insets(0, 15, 0, 15));
        hPrice.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hPrice.setAlignment(Pos.CENTER_RIGHT);
        
        Label hStock = new Label("Tồn Kho");
        hStock.setPrefWidth(100);
        hStock.setMinWidth(100);
        hStock.setPadding(new Insets(0, 15, 0, 15));
        hStock.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hStock.setAlignment(Pos.CENTER_RIGHT);
        
        Label hStatus = new Label("Trạng Thái");
        hStatus.setPrefWidth(100);
        hStatus.setMinWidth(100);
        hStatus.setPadding(new Insets(0, 15, 0, 15));
        hStatus.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hStatus.setAlignment(Pos.CENTER);
        
        Label hAction = new Label("Thao Tác");
        hAction.setPrefWidth(150);
        hAction.setMinWidth(150);
        hAction.setPadding(new Insets(0, 15, 0, 15));
        hAction.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hAction.setAlignment(Pos.CENTER);
        
        tableHeader.getChildren().addAll(hName, hCategory, hPrice, hStock, hStatus, hAction);
        HBox.setHgrow(hName, Priority.ALWAYS);
        
        // Load data from database
        refreshProductTable(tableRows);
        
        // Wrap tableRows in ScrollPane for scrolling
        ScrollPane scrollPane = new ScrollPane(tableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        tableContainer.getChildren().addAll(tableTitle, tableHeader, scrollPane);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        view.getChildren().addAll(header, searchBar, tableContainer);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }
    
    private void refreshProductTable(VBox tableRows) {
        refreshProductTableWithFilter(tableRows, "", "", "Tất cả trạng thái");
    }
    
    private void refreshProductTableWithFilter(VBox tableRows, String searchText, String category, String statusFilter) {
        tableRows.getChildren().clear();
        ProductService productService = new ProductService();
        List<Product> products = productService.getAllProducts();
        
        // Filter by category
        if (category != null && !category.trim().isEmpty()) {
            products = products.stream()
                .filter(p -> p.getCategory().equals(category))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Filter by search text
        if (searchText != null && !searchText.trim().isEmpty()) {
            String search = searchText.toLowerCase().trim();
            products = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(search) || 
                            p.getCategory().toLowerCase().contains(search))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Filter by status
        if (statusFilter != null && !statusFilter.equals("Tất cả trạng thái")) {
            products = products.stream()
                .filter(p -> {
                    String status = p.getStatus();
                    int stock = p.getStock();
                    int minStock = p.getMinStock();

                    if ("Đang bán".equals(statusFilter)) {
                        if ("Tạm dừng".equals(status)) return false;
                        if (stock == 0 || "Hết hàng".equals(status)) return false;
                        if (stock <= minStock) return false;
                        return "Đang bán".equals(status);
                    } else if ("Sắp hết hàng".equals(statusFilter)) {
                        if ("Tạm dừng".equals(status)) return false;
                        if (stock == 0 || "Hết hàng".equals(status)) return false;
                        return stock <= minStock;
                    } else if ("Hết hàng".equals(statusFilter)) {
                        return stock == 0 || "Hết hàng".equals(status);
                    } else if ("Tạm dừng".equals(statusFilter)) {
                        return "Tạm dừng".equals(status);
                    }
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
        }
        
        if (products.isEmpty()) {
            Label emptyState = new Label((searchText != null && !searchText.trim().isEmpty()) || 
                                         (category != null && !category.trim().isEmpty()) ||
                                         (statusFilter != null && !statusFilter.equals("Tất cả trạng thái")) ? 
                "Không tìm thấy sản phẩm nào" : "Chưa có sản phẩm nào");
            emptyState.setStyle("-fx-font-size: 14px; -fx-text-fill: #9e9e9e; -fx-padding: 40px;");
            tableRows.getChildren().add(emptyState);
        } else {
            for (Product product : products) {
                tableRows.getChildren().add(createProductRow(product, tableRows));
            }
        }
    }
    
    private void updateFilterButtonStyles(Button activeBtn, Button... otherBtns) {
        // Set active button style
        activeBtn.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 8px 16px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;"
        );
        
        // Set inactive button styles
        for (Button btn : otherBtns) {
            btn.setStyle(
                "-fx-background-color: #f5f5f5;" +
                "-fx-text-fill: #616161;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 8px 16px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;"
            );
        }
    }

    private void showReport() {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));

        Label title = new Label("📈 Báo Cáo & Thống Kê");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");

        // Load data from database
        InvoiceService invoiceService = new InvoiceService();
        List<Invoice> allInvoices = invoiceService.getAllInvoices();
        
        // Calculate stats
        double totalRevenue = allInvoices.stream()
            .filter(inv -> inv.getStatus().equals("paid"))
            .mapToDouble(Invoice::getTotalAmount)
            .sum();
        
        int totalInvoices = allInvoices.size();
        
        long uniqueCustomers = allInvoices.stream()
            .map(Invoice::getPhone)
            .filter(phone -> phone != null && !phone.isEmpty())
            .distinct()
            .count();

        // Date range selector
        HBox dateRange = new HBox(15);
        dateRange.setAlignment(Pos.CENTER_LEFT);
        dateRange.setPadding(new Insets(20));
        dateRange.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        Button btnFilter = new Button("Lọc");
        btnFilter.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        Button btnExport = new Button("📊 Xuất PDF");
        btnExport.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        ComboBox<String> cbPeriod = new ComboBox<>();
        cbPeriod.getItems().addAll("Tất cả", "Tuần này", "Tháng này", "Quý này", "Năm nay");
        cbPeriod.setValue("Tất cả");
        cbPeriod.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        ComboBox<String> cbMonth = new ComboBox<>();
        cbMonth.getItems().add("Tất cả các tháng");
        for (int m = 1; m <= 12; m++) {
            cbMonth.getItems().add("Tháng " + m);
        }
        cbMonth.setValue("Tất cả các tháng");
        cbMonth.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        ComboBox<String> cbYear = new ComboBox<>();
        cbYear.getItems().add("Tất cả các năm");
        cbYear.getItems().addAll(getAvailableYears());
        cbYear.setValue("Tất cả các năm");
        cbYear.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );

        // Filter button action
        btnFilter.setOnAction(e -> {
            String period = cbPeriod.getValue();
            String month = cbMonth.getValue();
            String year = cbYear.getValue();
            List<Invoice> filtered = allInvoices.stream()
                .filter(inv -> matchesTimeFilters(inv.getCreatedAt(), period, month, year))
                .collect(java.util.stream.Collectors.toList());
            updateReportStats(filtered, statsGrid);
        });
        
        // Export button action
        btnExport.setOnAction(e -> {
            String period = cbPeriod.getValue();
            String month = cbMonth.getValue();
            String year = cbYear.getValue();
            List<Invoice> filtered = allInvoices.stream()
                .filter(inv -> matchesTimeFilters(inv.getCreatedAt(), period, month, year))
                .collect(java.util.stream.Collectors.toList());
            
            LocalDate minDate = null;
            LocalDate maxDate = null;
            for (Invoice inv : filtered) {
                if (inv.getCreatedAt() != null) {
                    try {
                        LocalDate d = LocalDate.parse(inv.getCreatedAt().substring(0, 10));
                        if (minDate == null || d.isBefore(minDate)) minDate = d;
                        if (maxDate == null || d.isAfter(maxDate)) maxDate = d;
                    } catch (Exception ex) {}
                }
            }
            if (minDate == null) minDate = LocalDate.now();
            if (maxDate == null) maxDate = LocalDate.now();
            
            ReportHelper.exportReportToPDF(filtered, minDate, maxDate, mainLayout.getScene().getWindow());
        });
        
        cbPeriod.valueProperty().addListener((obs, old, newVal) -> btnFilter.fire());
        cbMonth.valueProperty().addListener((obs, old, newVal) -> btnFilter.fire());
        cbYear.valueProperty().addListener((obs, old, newVal) -> btnFilter.fire());
        
        dateRange.getChildren().clear();
        dateRange.getChildren().addAll(new Label("Lọc theo:"), cbPeriod, cbMonth, cbYear, btnFilter, btnExport);

        VBox stat1 = createReportStatCard("Tổng Doanh Thu", String.format("%,.0fđ", totalRevenue), "");
        VBox stat2 = createReportStatCard("Tổng Hóa Đơn", String.valueOf(totalInvoices), "");
        VBox stat3 = createReportStatCard("Khách Hàng", String.valueOf(uniqueCustomers), "");
        VBox stat4 = createReportStatCard("Đã Thanh Toán", 
            String.valueOf(allInvoices.stream().filter(inv -> inv.getStatus().equals("paid")).count()), "");

        statsGrid.add(stat1, 0, 0);
        statsGrid.add(stat2, 1, 0);
        statsGrid.add(stat3, 2, 0);
        statsGrid.add(stat4, 3, 0);

        // Revenue chart by month
        VBox chartContainer = new VBox(15);
        chartContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 25;"
        );
        chartContainer.setPrefHeight(450);
        
        Label chartTitle = new Label("📊 Biểu Đồ Doanh Thu Theo Tháng");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        // Create bar chart
        javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
        xAxis.setLabel("Tháng");
        xAxis.setStyle("-fx-font-size: 12px; -fx-text-fill: #616161;");
        
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        yAxis.setLabel("Doanh Thu (VNĐ)");
        yAxis.setStyle("-fx-font-size: 12px; -fx-text-fill: #616161;");
        
        javafx.scene.chart.BarChart<String, Number> barChart = new javafx.scene.chart.BarChart<>(xAxis, yAxis);
        barChart.setTitle("");
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(320);
        barChart.setStyle(
            "-fx-background-color: transparent;" +
            "CHART_COLOR_1: #2196F3;"
        );
        
        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        
        // Calculate revenue by month
        java.util.Map<String, Double> revenueByMonth = new java.util.HashMap<>();
        for (Invoice invoice : allInvoices) {
            if (invoice.getStatus().equals("paid") && invoice.getCreatedAt() != null) {
                try {
                    String month = invoice.getCreatedAt().substring(0, 7); // YYYY-MM
                    revenueByMonth.put(month, revenueByMonth.getOrDefault(month, 0.0) + invoice.getTotalAmount());
                } catch (Exception e) {
                    // Skip invalid dates
                }
            }
        }
        
        // Add data to chart (last 6 months)
        java.util.List<String> sortedMonths = new java.util.ArrayList<>(revenueByMonth.keySet());
        java.util.Collections.sort(sortedMonths);
        java.util.Collections.reverse(sortedMonths);
        
        int count = 0;
        for (String month : sortedMonths) {
            if (count >= 6) break;
            series.getData().add(new javafx.scene.chart.XYChart.Data<>(month, revenueByMonth.get(month)));
            count++;
        }
        
        // If no data, show sample months
        if (series.getData().isEmpty()) {
            series.getData().add(new javafx.scene.chart.XYChart.Data<>("2026-04", 0));
            series.getData().add(new javafx.scene.chart.XYChart.Data<>("2026-03", 0));
            series.getData().add(new javafx.scene.chart.XYChart.Data<>("2026-02", 0));
        }
        
        barChart.getData().add(series);
        
        // Apply CSS styling
        try {
            String css = getClass().getResource("/chart-styles.css").toExternalForm();
            barChart.getStylesheets().add(css);
        } catch (Exception e) {
            // If CSS not found, apply inline styles
            barChart.setStyle(
                ".chart-bar { -fx-background-color: #2196F3; -fx-background-radius: 4px; }"
            );
        }
        
        chartContainer.getChildren().addAll(chartTitle, barChart);

        view.getChildren().addAll(title, dateRange, statsGrid, chartContainer);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }
    
    private void updateReportStats(List<Invoice> filteredInvoices, GridPane statsGrid) {
        // Calculate stats
        double totalRevenue = filteredInvoices.stream()
            .filter(inv -> inv.getStatus().equals("paid"))
            .mapToDouble(Invoice::getTotalAmount)
            .sum();
        
        int totalInvoices = filteredInvoices.size();
        
        long uniqueCustomers = filteredInvoices.stream()
            .map(Invoice::getPhone)
            .filter(phone -> phone != null && !phone.isEmpty())
            .distinct()
            .count();
        
        long paidInvoices = filteredInvoices.stream()
            .filter(inv -> inv.getStatus().equals("paid"))
            .count();

        // Update stats cards
        statsGrid.getChildren().clear();
        VBox stat1 = createReportStatCard("Tổng Doanh Thu", String.format("%,.0fđ", totalRevenue), "");
        VBox stat2 = createReportStatCard("Tổng Hóa Đơn", String.valueOf(totalInvoices), "");
        VBox stat3 = createReportStatCard("Khách Hàng", String.valueOf(uniqueCustomers), "");
        VBox stat4 = createReportStatCard("Đã Thanh Toán", String.valueOf(paidInvoices), "");

        statsGrid.add(stat1, 0, 0);
        statsGrid.add(stat2, 1, 0);
        statsGrid.add(stat3, 2, 0);
        statsGrid.add(stat4, 3, 0);
    }

    private VBox createModernView(String title, String subtitle) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(30));

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lblSubtitle = new Label(subtitle);
        lblSubtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        view.getChildren().addAll(lblTitle, lblSubtitle);
        return view;
    }

    private VBox createServiceCard(String name, String description, String price) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );
        card.setPrefWidth(280);
        card.setPrefHeight(180);
        
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Label lblDesc = new Label(description);
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        lblDesc.setWrapText(true);
        
        Label lblPrice = new Label(price);
        lblPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #2196F3;");
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        HBox actions = new HBox(10);
        Button btnEdit = new Button("✏ Sửa");
        btnEdit.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #616161;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 6px 12px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        
        Button btnDelete = new Button("🗑 Xóa");
        btnDelete.setStyle(
            "-fx-background-color: #ffebee;" +
            "-fx-text-fill: #f44336;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 6px 12px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        
        actions.getChildren().addAll(btnEdit, btnDelete);
        
        card.getChildren().addAll(lblName, lblDesc, lblPrice, spacer, actions);
        return card;
    }

    private VBox createProductCard(String name, String price, String stock) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );
        card.setPrefWidth(220);
        card.setPrefHeight(200);
        
        // Product image placeholder
        StackPane imagePlaceholder = new StackPane();
        imagePlaceholder.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;"
        );
        imagePlaceholder.setPrefSize(80, 80);
        Label imgIcon = new Label("📦");
        imgIcon.setStyle("-fx-font-size: 36px;");
        imagePlaceholder.getChildren().add(imgIcon);
        
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        lblName.setWrapText(true);
        lblName.setMaxWidth(180);
        lblName.setAlignment(Pos.CENTER);
        
        Label lblPrice = new Label(price);
        lblPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #2196F3;");
        
        Label lblStock = new Label(stock);
        lblStock.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");
        
        card.getChildren().addAll(imagePlaceholder, lblName, lblPrice, lblStock);
        return card;
    }

    private VBox createPackageCard(String name, String description, String price, String savings) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #2196F3;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(33,150,243,0.2), 15, 0, 0, 5);"
        );
        card.setPrefWidth(280);
        card.setPrefHeight(280);
        
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #2196F3;");
        
        Label lblDesc = new Label(description);
        lblDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        lblDesc.setWrapText(true);
        lblDesc.setMaxWidth(220);
        lblDesc.setAlignment(Pos.CENTER);
        
        Label lblPrice = new Label(price);
        lblPrice.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #212121;");
        
        Label lblSavings = new Label(savings);
        lblSavings.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #4CAF50;" +
            "-fx-background-color: #E8F5E9;" +
            "-fx-padding: 6px 12px;" +
            "-fx-background-radius: 6;"
        );
        
        Button btnSelect = new Button("Chọn Gói");
        btnSelect.setMaxWidth(Double.MAX_VALUE);
        btnSelect.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        card.getChildren().addAll(lblName, lblDesc, lblPrice, lblSavings, btnSelect);
        return card;
    }

    private VBox createReportStatCard(String title, String value, String change) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        card.setPrefWidth(260);
        card.setPrefHeight(120);
        
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575; -fx-font-weight: 500;");
        
        HBox valueBox = new HBox(10);
        valueBox.setAlignment(Pos.BASELINE_LEFT);
        
        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #212121;");
        
        if (!change.isEmpty()) {
            Label lblChange = new Label(change);
            lblChange.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: " + (change.startsWith("+") ? "#4CAF50" : "#f44336") + ";" +
                "-fx-font-weight: 600;"
            );
            valueBox.getChildren().addAll(lblValue, lblChange);
        } else {
            valueBox.getChildren().add(lblValue);
        }
        
        card.getChildren().addAll(lblTitle, valueBox);
        return card;
    }




    private Button createFilterButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        if (active) {
            btn.setStyle(
                "-fx-background-color: #2196F3;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 8px 16px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: #f5f5f5;" +
                "-fx-text-fill: #616161;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 8px 16px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;"
            );
        }
        
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#2196F3")) {
                btn.setStyle(btn.getStyle() + "-fx-background-color: #eeeeee;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#2196F3")) {
                btn.setStyle(btn.getStyle().replace("-fx-background-color: #eeeeee;", "-fx-background-color: #f5f5f5;"));
            }
        });
        
        return btn;
    }


    private HBox createProductRow(Product product, VBox tableRows) {
        int id = product.getId();
        String name = product.getName();
        String category = product.getCategory();
        String price = String.format("%.0f đ", product.getPrice());
        String stock = String.valueOf(product.getStock());
        if (product.getUnit() != null && !product.getUnit().trim().isEmpty()) {
            stock += " " + product.getUnit().trim();
        }
        String status = product.getStatus();
        
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0));
        row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #f9fafb;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121; -fx-font-weight: 500;");
        lblName.setPrefWidth(350);
        lblName.setMinWidth(100);
        lblName.setMaxWidth(Double.MAX_VALUE);
        lblName.setPadding(new Insets(12, 15, 12, 15));
        lblName.setAlignment(Pos.CENTER_LEFT);
        
        Label lblCategory = new Label(category);
        lblCategory.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        lblCategory.setPrefWidth(150);
        lblCategory.setMinWidth(150);
        lblCategory.setMaxWidth(150);
        lblCategory.setPadding(new Insets(12, 15, 12, 15));
        lblCategory.setAlignment(Pos.CENTER_LEFT);
        
        Label lblPrice = new Label(price);
        lblPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        lblPrice.setPrefWidth(120);
        lblPrice.setMinWidth(120);
        lblPrice.setMaxWidth(120);
        lblPrice.setPadding(new Insets(12, 15, 12, 15));
        lblPrice.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblStock = new Label(stock);
        lblStock.setPrefWidth(100);
        lblStock.setMinWidth(100);
        lblStock.setMaxWidth(100);
        lblStock.setPadding(new Insets(12, 15, 12, 15));
        lblStock.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblStatus = new Label();
        lblStatus.setPrefWidth(100);
        lblStatus.setMinWidth(100);
        lblStatus.setMaxWidth(100);
        lblStatus.setAlignment(Pos.CENTER);
        
        // Cảnh báo trực quan dựa trên tồn kho tối thiểu
        int stockVal = product.getStock();
        int minStockVal = product.getMinStock();
        
        if ("Tạm dừng".equals(status)) {
            lblStatus.setText("Tạm dừng");
            lblStatus.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #9E9E9E;" +
                "-fx-background-color: #F5F5F5;" +
                "-fx-padding: 4px 10px;" +
                "-fx-background-radius: 6;"
            );
            lblStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121;");
        } else if (stockVal == 0 || "Hết hàng".equals(status)) {
            lblStatus.setText("Hết hàng");
            lblStatus.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #C62828;" +
                "-fx-background-color: #FFEBEE;" +
                "-fx-padding: 4px 10px;" +
                "-fx-background-radius: 6;"
            );
            lblStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #C62828; -fx-font-weight: bold;");
        } else if (stockVal <= minStockVal) {
            lblStatus.setText("Sắp hết hàng");
            lblStatus.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #E65100;" +
                "-fx-background-color: #FFE0B2;" +
                "-fx-padding: 4px 10px;" +
                "-fx-background-radius: 6;"
            );
            lblStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #E65100; -fx-font-weight: bold;");
        } else {
            lblStatus.setText("Đang bán");
            lblStatus.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #2E7D32;" +
                "-fx-background-color: #E8F5E9;" +
                "-fx-padding: 4px 10px;" +
                "-fx-background-radius: 6;"
            );
            lblStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121;");
        }
        
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWidth(150);
        actions.setMinWidth(150);
        actions.setMaxWidth(150);
        actions.setPadding(new Insets(12, 15, 12, 15));
        
        Button btnEdit = new Button("✏");
        btnEdit.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 10px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 32;" +
            "-fx-min-height: 32;"
        );
        btnEdit.setOnAction(e -> {
            ProductForm form = new ProductForm(product, () -> refreshProductTable(tableRows));
            form.show();
        });
        
        Button btnDelete = new Button("🗑");
        btnDelete.setStyle(
            "-fx-background-color: #FFEBEE;" +
            "-fx-text-fill: #f44336;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 10px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 32;" +
            "-fx-min-height: 32;"
        );
        btnDelete.setOnAction(e -> {
            showDeleteConfirmation("sản phẩm", name, () -> {
                ProductService productService = new ProductService();
                if (productService.deleteProduct(id)) {
                    refreshProductTable(tableRows);
                } else {
                    showErrorAlert("Lỗi", "Không thể xóa sản phẩm!");
                }
            });
        });
        
        actions.getChildren().addAll(btnEdit, btnDelete);
        
        row.getChildren().addAll(lblName, lblCategory, lblPrice, lblStock, lblStatus, actions);
        HBox.setHgrow(lblName, Priority.ALWAYS);
        return row;
    }

    private HBox createPackageRow(Package pkg, VBox tableRows) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0));
        row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #f9fafb;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        
        // Name
        Label lblName = new Label(pkg.getName());
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121; -fx-font-weight: 600;");
        lblName.setPrefWidth(150);
        lblName.setMinWidth(150);
        lblName.setPadding(new Insets(12, 10, 12, 15));
        lblName.setAlignment(Pos.CENTER_LEFT);
        
        // Description
        Label lblDesc = new Label(pkg.getDescription());
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        lblDesc.setPrefWidth(300);
        lblDesc.setMinWidth(150);
        lblDesc.setMaxWidth(Double.MAX_VALUE);
        lblDesc.setPadding(new Insets(12, 10, 12, 10));
        lblDesc.setAlignment(Pos.CENTER_LEFT);
        lblDesc.setWrapText(true);
        
        // Price Range (Min - Max)
        String priceRange = String.format("%.0fđ - %.0fđ", pkg.getPriceMini(), pkg.getPricePickup());
        Label lblPriceRange = new Label(priceRange);
        lblPriceRange.setStyle("-fx-font-size: 13px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        lblPriceRange.setPrefWidth(150);
        lblPriceRange.setMinWidth(150);
        lblPriceRange.setPadding(new Insets(12, 10, 12, 10));
        lblPriceRange.setAlignment(Pos.CENTER_RIGHT);
        
        // Savings
        Label lblSavings = new Label(String.format("~%.0fđ", pkg.getSavings()));
        lblSavings.setStyle("-fx-font-size: 13px; -fx-text-fill: #4CAF50; -fx-font-weight: 600;");
        lblSavings.setPrefWidth(100);
        lblSavings.setMinWidth(100);
        lblSavings.setPadding(new Insets(12, 10, 12, 10));
        lblSavings.setAlignment(Pos.CENTER_RIGHT);
        
        // Status
        Label lblStatus = new Label(pkg.getStatus());
        lblStatus.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: " + (pkg.getStatus().equals("Đang bán") ? "#2196F3" : "#757575") + ";" +
            "-fx-background-color: " + (pkg.getStatus().equals("Đang bán") ? "#E3F2FD" : "#F5F5F5") + ";" +
            "-fx-padding: 5px 10px;" +
            "-fx-background-radius: 6;"
        );
        lblStatus.setAlignment(Pos.CENTER);
        
        HBox statusContainer = new HBox(lblStatus);
        statusContainer.setAlignment(Pos.CENTER);
        statusContainer.setPrefWidth(100);
        statusContainer.setMinWidth(100);
        statusContainer.setPadding(new Insets(12, 10, 12, 10));
        
        // Actions
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWidth(120);
        actions.setMinWidth(120);
        actions.setPadding(new Insets(12, 15, 12, 10));
        
        Button btnView = new Button("👁");
        btnView.setStyle(
            "-fx-background-color: #F3E5F5;" +
            "-fx-text-fill: #7B1FA2;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 6px 8px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 28;" +
            "-fx-min-height: 28;"
        );
        btnView.setOnAction(e -> {
            showPackageDetails(pkg);
        });
        
        Button btnEdit = new Button("✏");
        btnEdit.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 6px 8px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 28;" +
            "-fx-min-height: 28;"
        );
        btnEdit.setOnAction(e -> {
            PackageForm form = new PackageForm(pkg.getId(), pkg, () -> refreshPackageTable(tableRows));
            form.show();
        });
        
        Button btnDelete = new Button("🗑");
        btnDelete.setStyle(
            "-fx-background-color: #FFEBEE;" +
            "-fx-text-fill: #f44336;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 6px 8px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 28;" +
            "-fx-min-height: 28;"
        );
        btnDelete.setOnAction(e -> {
            showDeleteConfirmation("gói dịch vụ", pkg.getName(), () -> {
                PackageService packageService = new PackageService();
                if (packageService.deletePackage(pkg.getId())) {
                    refreshPackageTable(tableRows);
                } else {
                    showErrorAlert("Lỗi", "Không thể xóa gói dịch vụ!");
                }
            });
        });
        
        actions.getChildren().addAll(btnView, btnEdit, btnDelete);
        
        row.getChildren().addAll(lblName, lblDesc, lblPriceRange, lblSavings, statusContainer, actions);
        HBox.setHgrow(lblDesc, Priority.ALWAYS);
        return row;
    }
    
    private void showPackageDetails(Package pkg) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi Tiết Gói Dịch Vụ");
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        // Package name
        Label lblName = new Label(pkg.getName());
        lblName.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #212121;");
        
        // Description
        Label lblDesc = new Label(pkg.getDescription());
        lblDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        lblDesc.setWrapText(true);
        lblDesc.setMaxWidth(500);
        
        // Price table
        Label lblPriceTitle = new Label("💰 Bảng Giá Theo Loại Xe");
        lblPriceTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #1976D2; -fx-padding: 10 0 5 0;");
        
        GridPane priceGrid = new GridPane();
        priceGrid.setHgap(20);
        priceGrid.setVgap(12);
        priceGrid.setStyle("-fx-background-color: #F9FAFB; -fx-padding: 15; -fx-background-radius: 8;");
        
        String[][] prices = {
            {"Mini", String.format("%.0fđ", pkg.getPriceMini()), "#1976D2", "#E3F2FD"},
            {"Sedan", String.format("%.0fđ", pkg.getPriceSedan()), "#388E3C", "#E8F5E9"},
            {"CUV", String.format("%.0fđ", pkg.getPriceCuv()), "#F57C00", "#FFF3E0"},
            {"SUV", String.format("%.0fđ", pkg.getPriceSuv()), "#C2185B", "#FCE4EC"},
            {"MPV", String.format("%.0fđ", pkg.getPriceMpv()), "#00838F", "#E0F7FA"},
            {"Pickup", String.format("%.0fđ", pkg.getPricePickup()), "#7B1FA2", "#F3E5F5"}
        };
        
        for (int i = 0; i < prices.length; i++) {
            Label lblType = new Label(prices[i][0]);
            lblType.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: " + prices[i][2] + ";");
            lblType.setPrefWidth(80);
            
            Label lblPrice = new Label(prices[i][1]);
            lblPrice.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: " + prices[i][2] + ";" +
                "-fx-background-color: " + prices[i][3] + ";" +
                "-fx-padding: 8 15; -fx-background-radius: 6;"
            );
            lblPrice.setPrefWidth(150);
            lblPrice.setAlignment(Pos.CENTER_RIGHT);
            
            priceGrid.add(lblType, 0, i);
            priceGrid.add(lblPrice, 1, i);
        }
        
        // Savings
        Label lblSavings = new Label(String.format("💵 Tiết kiệm trung bình: %.0fđ", pkg.getSavings()));
        lblSavings.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #4CAF50;");
        
        // Status
        Label lblStatus = new Label("Trạng thái: " + pkg.getStatus());
        lblStatus.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        
        content.getChildren().addAll(lblName, lblDesc, lblPriceTitle, priceGrid, lblSavings, lblStatus);
        
        dialogPane.setContent(content);
        dialogPane.setPrefWidth(600);
        
        dialog.showAndWait();
    }

    private void handleLogout() {
        // Create custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Đăng xuất");
        
        // Set the dialog pane
        DialogPane dialogPane = dialog.getDialogPane();
        
        // Custom content
        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40, 50, 35, 50));
        content.setStyle("-fx-background-color: white;");
        content.setMinWidth(420);
        
        // Icon with gradient background
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle(
            "-fx-background-color: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);" +
            "-fx-background-radius: 50;" +
            "-fx-pref-width: 80;" +
            "-fx-pref-height: 80;"
        );
        
        Label iconLabel = new Label("→");
        iconLabel.setStyle(
            "-fx-font-size: 42px;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-font-weight: bold;" +
            "-fx-rotate: 180;"
        );
        iconContainer.getChildren().add(iconLabel);
        
        // Message
        Label message = new Label("Xác nhận đăng xuất");
        message.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-text-fill: #212121;" +
            "-fx-font-weight: 600;"
        );
        
        Label subMessage = new Label("Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?");
        subMessage.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #757575;" +
            "-fx-text-alignment: center;"
        );
        subMessage.setWrapText(true);
        subMessage.setMaxWidth(350);
        
        content.getChildren().addAll(iconContainer, message, subMessage);
        dialogPane.setContent(content);
        
        // Custom buttons
        ButtonType btnYes = new ButtonType("Đăng xuất", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(btnNo, btnYes);
        
        // Style the dialog pane
        dialogPane.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 25, 0, 0, 8);"
        );
        
        // Style buttons
        dialogPane.lookupButton(btnYes).setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 32px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120;"
        );
        
        dialogPane.lookupButton(btnNo).setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #616161;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 32px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120;"
        );
        
        // Add hover effects
        dialogPane.lookupButton(btnYes).setOnMouseEntered(e -> 
            dialogPane.lookupButton(btnYes).setStyle(
                dialogPane.lookupButton(btnYes).getStyle() + "-fx-background-color: #1976D2;"
            )
        );
        dialogPane.lookupButton(btnYes).setOnMouseExited(e -> 
            dialogPane.lookupButton(btnYes).setStyle(
                dialogPane.lookupButton(btnYes).getStyle().replace("-fx-background-color: #1976D2;", "-fx-background-color: #2196F3;")
            )
        );
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnYes) {
                // Tắt trạng thái tự động đăng nhập khi người dùng chủ động đăng xuất
                try {
                    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginUI.class);
                    prefs.putBoolean("remember_me", false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Close current window and open login directly
                Stage currentStage = (Stage) mainLayout.getScene().getWindow();
                currentStage.close();
                
                // Open login window
                LoginUI loginUI = new LoginUI();
                Stage loginStage = new Stage();
                try {
                    loginUI.start(loginStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void showInvoiceDetail(int invoiceId, VBox tableRows) {
        InvoiceService invoiceService = new InvoiceService();
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        
        if (invoice == null) {
            showErrorAlert("Lỗi", "Không tìm thấy hóa đơn!");
            return;
        }
        
        // Load invoice items
        service.InvoiceItemService itemService = new service.InvoiceItemService();
        List<model.InvoiceItem> items = itemService.getItemsByInvoiceId(invoiceId);
        
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Chi Tiết Hóa Đơn #" + String.format("%05d", invoiceId));
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");
        
        // Header
        Label title = new Label("📋 Chi Tiết Hóa Đơn #" + String.format("%05d", invoiceId));
        title.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-text-fill: #212121;" +
            "-fx-font-weight: 600;"
        );
        
        // Customer info section
        VBox customerSection = new VBox(12);
        customerSection.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label customerTitle = new Label("Thông Tin Khách Hàng");
        customerTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Label lblCustomer = new Label("Tên: " + invoice.getCustomerName());
        lblCustomer.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        Label lblPhone = new Label("SĐT: " + (invoice.getPhone() != null ? invoice.getPhone() : "N/A"));
        lblPhone.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        Label lblPlate = new Label("Biển số: " + (invoice.getLicensePlate() != null ? invoice.getLicensePlate() : "N/A"));
        lblPlate.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        Label lblVehicle = new Label("Loại xe: " + (invoice.getVehicleType() != null ? invoice.getVehicleType() : "N/A"));
        lblVehicle.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        customerSection.getChildren().addAll(customerTitle, lblCustomer, lblPhone, lblPlate, lblVehicle);
        
        // Invoice items section
        VBox itemsSection = new VBox(12);
        itemsSection.setStyle(
            "-fx-background-color: #FFF3E0;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label itemsTitle = new Label("Chi Tiết Dịch Vụ & Sản Phẩm");
        itemsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        if (items.isEmpty()) {
            Label noItems = new Label("Không có chi tiết");
            noItems.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
            itemsSection.getChildren().addAll(itemsTitle, noItems);
        } else {
            VBox itemsList = new VBox(12); // Slightly increased spacing
            for (model.InvoiceItem item : items) {
                VBox rowContainer = new VBox(4);
                
                HBox itemRow = new HBox(10);
                itemRow.setAlignment(Pos.CENTER_LEFT);
                
                String itemTypeIcon = "";
                if (item.getItemType().equals("service")) {
                    itemTypeIcon = "🔧";
                } else if (item.getItemType().equals("package")) {
                    itemTypeIcon = "📦";
                } else if (item.getItemType().equals("product")) {
                    itemTypeIcon = "🛒";
                }
                
                Label lblIcon = new Label(itemTypeIcon);
                lblIcon.setStyle("-fx-font-size: 14px;");
                
                Label lblName = new Label(item.getItemName());
                lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 500;");
                lblName.setPrefWidth(280); // Slightly increased width
                
                Label lblQty = new Label("x" + item.getQuantity());
                lblQty.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
                lblQty.setPrefWidth(40);
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                Label lblPrice = new Label(String.format("%,.0f đ", item.getTotalPrice()));
                lblPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
                
                itemRow.getChildren().addAll(lblIcon, lblName, lblQty, spacer, lblPrice);
                rowContainer.getChildren().add(itemRow);
                
                // Add detail sub-row showing unit price, quantity, and discount
                double originalTotal = item.getUnitPrice() * item.getQuantity();
                double itemDiscount = originalTotal - item.getTotalPrice();
                
                String detailStr = String.format("Đơn giá: %,.0f đ | Số lượng: %d", item.getUnitPrice(), item.getQuantity());
                if (itemDiscount > 0 && originalTotal > 0) {
                    double pct = (itemDiscount / originalTotal) * 100.0;
                    if (Math.abs(pct - Math.round(pct)) < 0.01 && Math.round(pct) % 5 == 0) {
                        detailStr += String.format(" | Giảm giá: %.0f%%", pct);
                    } else {
                        detailStr += String.format(" | Giảm giá: -%,.0f đ", itemDiscount);
                    }
                }
                
                Label lblDetails = new Label(detailStr);
                lblDetails.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888; -fx-padding: 0 0 0 25;");
                rowContainer.getChildren().add(lblDetails);
                
                itemsList.getChildren().add(rowContainer);
            }
            itemsSection.getChildren().addAll(itemsTitle, itemsList);
        }
        
        // Payment info section
        VBox paymentSection = new VBox(12);
        paymentSection.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label paymentTitle = new Label("Thông Tin Thanh Toán");
        paymentTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Label lblTotal = new Label(String.format("Tổng tiền trước giảm: %,.0f đ", invoice.getTotalBeforeDiscount()));
        lblTotal.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        Label lblDiscount = new Label(String.format("Giảm giá: %,.0f đ", invoice.getDiscount()));
        lblDiscount.setStyle("-fx-font-size: 14px; -fx-text-fill: #e53935;");
        
        double subtotalAfterDiscount = invoice.getTotalBeforeDiscount() - invoice.getDiscount();
        double vatAmount = subtotalAfterDiscount * 0.08;
        Label lblVat = new Label(String.format("Thuế VAT (8%%): %,.0f đ", vatAmount));
        lblVat.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        
        Label lblFinal = new Label(String.format("Tổng cộng thanh toán: %,.0f đ", invoice.getTotalAmount()));
        lblFinal.setStyle("-fx-font-size: 20px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        
        paymentSection.getChildren().addAll(paymentTitle, lblTotal, lblDiscount, lblVat, lblFinal);
        
        // Status section
        VBox statusSection = new VBox(15);
        statusSection.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label statusTitle = new Label("Trạng Thái Thanh Toán");
        statusTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        HBox statusBox = new HBox(15);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        ToggleGroup statusGroup = new ToggleGroup();
        
        RadioButton rbUnpaid = new RadioButton("Chưa thanh toán");
        rbUnpaid.setToggleGroup(statusGroup);
        rbUnpaid.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        if (invoice.getStatus().equals("nhap")) {
            rbUnpaid.setSelected(true);
        }
        
        RadioButton rbPaid = new RadioButton("Đã thanh toán");
        rbPaid.setToggleGroup(statusGroup);
        rbPaid.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        if (invoice.getStatus().equals("paid")) {
            rbPaid.setSelected(true);
        }
        
        statusBox.getChildren().addAll(rbUnpaid, rbPaid);
        statusSection.getChildren().addAll(statusTitle, statusBox);
        
        // Notes section (always visible and editable)
        VBox notesSection = new VBox(10);
        notesSection.setStyle(
            "-fx-background-color: #FFF3E0;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label notesTitle = new Label("Ghi Chú");
        notesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        TextArea txtAreaNotes = new TextArea(invoice.getNotes() != null ? invoice.getNotes() : "");
        txtAreaNotes.setPromptText("Nhập ghi chú hoặc thông tin thanh toán (ví dụ: Khách thanh toán trước 50%...)");
        txtAreaNotes.setPrefRowCount(3);
        txtAreaNotes.setWrapText(true);
        txtAreaNotes.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #ffe0b2;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 14px;"
        );
        UIUtils.setupIMEFix(txtAreaNotes);
        
        notesSection.getChildren().addAll(notesTitle, txtAreaNotes);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button btnClose = new Button("Đóng");
        btnClose.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #616161;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 32px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120;"
        );
        btnClose.setOnMouseEntered(e -> btnClose.setStyle(
            btnClose.getStyle() + "-fx-background-color: #e0e0e0;"
        ));
        btnClose.setOnMouseExited(e -> btnClose.setStyle(
            btnClose.getStyle().replace("-fx-background-color: #e0e0e0;", "-fx-background-color: #f5f5f5;")
        ));
        btnClose.setOnAction(e -> dialogStage.close());
        
        Button btnUpdate = new Button("Cập Nhật");
        btnUpdate.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 32px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120;"
        );
        btnUpdate.setOnMouseEntered(e -> btnUpdate.setOpacity(0.9));
        btnUpdate.setOnMouseExited(e -> btnUpdate.setOpacity(1.0));
        btnUpdate.setOnAction(e -> {
            // Get selected status
            RadioButton selected = (RadioButton) statusGroup.getSelectedToggle();
            String newStatus = selected == rbPaid ? "paid" : "nhap";
            String newNotes = txtAreaNotes.getText().trim();
            
            // Update in database
            Invoice inv = invoiceService.getInvoiceById(invoiceId);
            if (inv != null) {
                inv.setStatus(newStatus);
                inv.setNotes(newNotes);
                if (invoiceService.updateInvoice(inv)) {
                    showSuccessAlert("Thành công", "Cập nhật hóa đơn thành công!");
                    refreshInvoiceTable(tableRows);
                    dialogStage.close();
                } else {
                    showErrorAlert("Lỗi", "Không thể cập nhật hóa đơn!");
                }
            } else {
                showErrorAlert("Lỗi", "Không tìm thấy hóa đơn!");
            }
        });
        
        buttonBox.getChildren().addAll(btnClose, btnUpdate);
        
        content.getChildren().addAll(title, customerSection, itemsSection, paymentSection, statusSection, notesSection, buttonBox);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        
        Scene scene = new Scene(scrollPane, 650, 750);
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
    
    private void exportInvoiceToPDF(Invoice invoice) {
        try {
            // Load invoice items
            service.InvoiceItemService itemService = new service.InvoiceItemService();
            List<model.InvoiceItem> items = itemService.getItemsByInvoiceId(invoice.getId());
            
            // Create file chooser
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Lưu Phiếu Thi Công PDF");
            fileChooser.setInitialFileName("PhieuThiCong_" + String.format("%05d", invoice.getId()) + ".pdf");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            java.io.File file = fileChooser.showSaveDialog(mainLayout.getScene().getWindow());
            if (file == null) {
                return; // User cancelled
            }
            
            // Create PDF using iText
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf,
                com.itextpdf.kernel.geom.PageSize.A4);
            document.setMargins(30, 40, 30, 40);
            
            // Fonts
            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);
            
            System.out.println("=== XUẤT HÓA ĐƠN PDF ===");
            if (util.PDFFontHelper.testVietnameseSupport(font)) {
                System.out.println("✓ Font hỗ trợ tiếng Việt");
            } else {
                System.out.println("⚠ Font có thể không hỗ trợ đầy đủ tiếng Việt");
            }
            
            // Colors for document
            com.itextpdf.kernel.colors.Color blackColor = new com.itextpdf.kernel.colors.DeviceRgb(0, 0, 0);
            com.itextpdf.kernel.colors.Color redColor = new com.itextpdf.kernel.colors.DeviceRgb(218, 37, 29);
            
            // ===== HEADER: Company Name =====
            com.itextpdf.layout.element.Paragraph companyName = new com.itextpdf.layout.element.Paragraph(
                "CÔNG TY TNHH TM DV PHỤ TÙNG Ô TÔ MINH TÂM")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(redColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(2);
            document.add(companyName);
            
            // Address
            com.itextpdf.layout.element.Paragraph address = new com.itextpdf.layout.element.Paragraph(
                "Ngã tư Trương Định và An Dương Vương, P. Nghĩa Lộ, tỉnh Quảng Ngãi")
                .setFont(font)
                .setFontSize(10)
                .setFontColor(redColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(8);
            document.add(address);
            
            // Title: PHIẾU THI CÔNG
            com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph(
                "PHIẾU THI CÔNG")
                .setFont(boldFont)
                .setFontSize(18)
                .setFontColor(redColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(15);
            document.add(title);
            
            // ===== CUSTOMER INFO =====
            // Row 1: Tên khách hàng + SĐT (side by side)
            com.itextpdf.layout.element.Table customerRow1 = new com.itextpdf.layout.element.Table(
                com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{50, 50}));
            customerRow1.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            com.itextpdf.layout.element.Cell nameCell = new com.itextpdf.layout.element.Cell().setBorder(null);
            nameCell.add(new com.itextpdf.layout.element.Paragraph()
                .add(new com.itextpdf.layout.element.Text("Tên khách hàng: ").setFont(boldFont).setFontSize(11).setFontColor(redColor))
                .add(new com.itextpdf.layout.element.Text(invoice.getCustomerName() != null ? invoice.getCustomerName() : "").setFont(font).setFontSize(11).setFontColor(blackColor)));
            customerRow1.addCell(nameCell);
            
            com.itextpdf.layout.element.Cell phoneCell = new com.itextpdf.layout.element.Cell().setBorder(null);
            phoneCell.add(new com.itextpdf.layout.element.Paragraph()
                .add(new com.itextpdf.layout.element.Text("SĐT khách hàng: ").setFont(boldFont).setFontSize(11).setFontColor(redColor))
                .add(new com.itextpdf.layout.element.Text(invoice.getPhone() != null ? invoice.getPhone() : "").setFont(font).setFontSize(11).setFontColor(blackColor)));
            customerRow1.addCell(phoneCell);
            document.add(customerRow1);
            
            // Biển số xe
            com.itextpdf.layout.element.Paragraph plateInfo = new com.itextpdf.layout.element.Paragraph()
                .add(new com.itextpdf.layout.element.Text("Biển số xe: ").setFont(boldFont).setFontSize(11).setFontColor(redColor))
                .add(new com.itextpdf.layout.element.Text(invoice.getLicensePlate() != null ? invoice.getLicensePlate() : "").setFont(boldFont).setFontSize(12).setFontColor(blackColor))
                .setMarginBottom(2);
            document.add(plateInfo);
            
            // Địa chỉ
            com.itextpdf.layout.element.Paragraph addressInfo = new com.itextpdf.layout.element.Paragraph()
                .add(new com.itextpdf.layout.element.Text("Địa chỉ: ").setFont(boldFont).setFontSize(11).setFontColor(redColor))
                .add(new com.itextpdf.layout.element.Text(invoice.getAddress() != null ? invoice.getAddress() : "").setFont(font).setFontSize(11).setFontColor(blackColor))
                .setMarginBottom(2);
            document.add(addressInfo);
            
            // Thời gian nhận xe
            String createdAt = invoice.getCreatedAt() != null ? invoice.getCreatedAt() : "";
            com.itextpdf.layout.element.Paragraph timeInfo = new com.itextpdf.layout.element.Paragraph()
                .add(new com.itextpdf.layout.element.Text("Thời gian nhận xe: ").setFont(boldFont).setFontSize(11).setFontColor(redColor))
                .add(new com.itextpdf.layout.element.Text(createdAt).setFont(font).setFontSize(11).setFontColor(blackColor))
                .setMarginBottom(10);
            document.add(timeInfo);
            
            // ===== SERVICE TABLE =====
            // 6 columns: STT | TÊN DỊCH VỤ | ĐƠN GIÁ | GIẢM GIÁ | VAT(8%) | THÀNH TIỀN
            float[] columnWidths = {8f, 32f, 15f, 13f, 12f, 20f};
            com.itextpdf.layout.element.Table serviceTable = new com.itextpdf.layout.element.Table(
                com.itextpdf.layout.properties.UnitValue.createPercentArray(columnWidths));
            serviceTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Header row
            String[] headers = {"STT", "TÊN DỊCH VỤ", "ĐƠN GIÁ", "GIẢM GIÁ", "VAT(8%)", "THÀNH TIỀN"};
            for (String header : headers) {
                com.itextpdf.layout.element.Cell headerCell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(header)
                        .setFont(boldFont).setFontSize(10).setFontColor(redColor)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER))
                    .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setBorderLeft(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setBorderRight(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setPadding(5);
                serviceTable.addHeaderCell(headerCell);
            }
            
            // Data rows (always 9 rows)
            int totalRows = 9;
            double grandTotal = 0;
            
            for (int i = 0; i < totalRows; i++) {
                String stt = String.valueOf(i + 1);
                String serviceName = "";
                String unitPrice = "";
                String discount = "";
                String vat = "";
                String totalPrice = "";
                
                if (i < items.size()) {
                    model.InvoiceItem item = items.get(i);
                    serviceName = item.getItemName();
                    if (item.getQuantity() > 1) {
                        serviceName += " (x" + item.getQuantity() + ")";
                    }
                    unitPrice = String.format("%,.0f", item.getUnitPrice());
                    
                    // Discount percentage = ((originalTotal - totalPrice) / originalTotal) * 100
                    double originalTotal = item.getUnitPrice() * item.getQuantity();
                    double itemDiscount = originalTotal - item.getTotalPrice();
                    if (itemDiscount > 0 && originalTotal > 0) {
                        double pct = (itemDiscount / originalTotal) * 100.0;
                        if (Math.abs(pct - Math.round(pct)) < 0.01 && Math.round(pct) % 5 == 0) {
                            discount = String.format("%.0f%%", pct);
                        } else {
                            discount = String.format("%,.0f", itemDiscount);
                        }
                    } else {
                        discount = "";
                    }
                    
                    // VAT 8% on price after discount
                    double vatAmount = item.getTotalPrice() * 0.08;
                    vat = String.format("%,.0f", vatAmount);
                    
                    // Thành tiền = price after discount + VAT
                    double itemTotal = item.getTotalPrice() + vatAmount;
                    totalPrice = String.format("%,.0f", itemTotal);
                    grandTotal += itemTotal;
                }
                
                // STT cell
                com.itextpdf.layout.element.Cell sttCell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(stt).setFont(font).setFontSize(10).setFontColor(blackColor)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setPadding(4).setMinHeight(22);
                serviceTable.addCell(sttCell);
                
                // Service name cell
                com.itextpdf.layout.element.Cell nameServiceCell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(serviceName).setFont(font).setFontSize(10).setFontColor(blackColor))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setPadding(4).setMinHeight(22);
                serviceTable.addCell(nameServiceCell);
                
                // Unit price cell
                com.itextpdf.layout.element.Cell priceCell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(unitPrice).setFont(font).setFontSize(10).setFontColor(blackColor)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setPadding(4).setMinHeight(22);
                serviceTable.addCell(priceCell);
                
                // Discount cell
                com.itextpdf.layout.element.Cell discountCell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(discount).setFont(font).setFontSize(10).setFontColor(blackColor)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setPadding(4).setMinHeight(22);
                serviceTable.addCell(discountCell);
                
                // VAT cell
                com.itextpdf.layout.element.Cell vatCell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(vat).setFont(font).setFontSize(10).setFontColor(blackColor)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setPadding(4).setMinHeight(22);
                serviceTable.addCell(vatCell);
                
                // Total price cell
                com.itextpdf.layout.element.Cell totalPriceCell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(totalPrice).setFont(font).setFontSize(10).setFontColor(blackColor)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                    .setPadding(4).setMinHeight(22);
                serviceTable.addCell(totalPriceCell);
            }
            
            // ===== TOTAL ROW =====
            // TỔNG TIỀN spanning first 2 columns
            com.itextpdf.layout.element.Cell totalLabelCell = new com.itextpdf.layout.element.Cell(1, 2)
                .add(new com.itextpdf.layout.element.Paragraph("TỔNG TIỀN")
                    .setFont(boldFont).setFontSize(11).setFontColor(redColor)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                .setPadding(5);
            serviceTable.addCell(totalLabelCell);
            
            // Total value spanning last 4 columns
            double finalTotal = invoice.getTotalAmount();
            String totalText = String.format("%,.0f đ", finalTotal);
            
            com.itextpdf.layout.element.Cell totalValueCell = new com.itextpdf.layout.element.Cell(1, 4)
                .add(new com.itextpdf.layout.element.Paragraph()
                    .add(new com.itextpdf.layout.element.Text(totalText).setFont(boldFont).setFontSize(11).setFontColor(blackColor)))
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(redColor, 0.8f))
                .setPadding(5);
            serviceTable.addCell(totalValueCell);
            
            document.add(serviceTable);
            
            // ===== SIGNATURE SECTION =====
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            
            com.itextpdf.layout.element.Table signatureTable = new com.itextpdf.layout.element.Table(
                com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{50, 50}));
            signatureTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Column 1: Xác nhận của khách hàng
            com.itextpdf.layout.element.Cell sig1Cell = new com.itextpdf.layout.element.Cell().setBorder(null);
            sig1Cell.add(new com.itextpdf.layout.element.Paragraph("Xác nhận của khách hàng")
                .setFont(font).setFontSize(10).setFontColor(redColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            sig1Cell.add(new com.itextpdf.layout.element.Paragraph("\n\n\n\n"));
            signatureTable.addCell(sig1Cell);
            
            // Column 2: Chữ ký đại diện công ty
            com.itextpdf.layout.element.Cell sig2Cell = new com.itextpdf.layout.element.Cell().setBorder(null);
            sig2Cell.add(new com.itextpdf.layout.element.Paragraph("Chữ ký đại diện công ty")
                .setFont(font).setFontSize(10).setFontColor(redColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            sig2Cell.add(new com.itextpdf.layout.element.Paragraph("\n\n\n"));
            sig2Cell.add(new com.itextpdf.layout.element.Paragraph("Phạm Minh Tâm")
                .setFont(boldFont).setFontSize(11).setFontColor(redColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            signatureTable.addCell(sig2Cell);
            
            document.add(signatureTable);
            
            // ===== FOOTER =====
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            
            com.itextpdf.layout.element.Paragraph hotline = new com.itextpdf.layout.element.Paragraph(
                "Hotline: 038 442 4567 (Zalo)")
                .setFont(font).setFontSize(10).setFontColor(redColor)
                .setMarginBottom(1);
            document.add(hotline);
            
            document.close();
            
            showSuccessAlert("Thành công", "Xuất hóa đơn PDF thành công!\nĐã lưu tại: " + file.getAbsolutePath());
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Lỗi", "Không thể xuất PDF: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }

    private void showDeleteConfirmation(String itemType, String itemName, Runnable onConfirm) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Xác nhận xóa");
        
        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40, 50, 40, 50));
        content.setStyle("-fx-background-color: white;");
        
        // Warning icon with blue theme
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-background-radius: 50;" +
            "-fx-pref-width: 80;" +
            "-fx-pref-height: 80;"
        );
        
        Label iconLabel = new Label("?");
        iconLabel.setStyle(
            "-fx-font-size: 48px;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-font-weight: bold;"
        );
        iconContainer.getChildren().add(iconLabel);
        
        Label message = new Label("Xác nhận xóa " + itemType);
        message.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-text-fill: #212121;" +
            "-fx-font-weight: 600;"
        );
        
        Label subMessage = new Label("Bạn có chắc chắn muốn xóa " + itemType + ":");
        subMessage.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #757575;"
        );
        
        Label itemLabel = new Label("\"" + itemName + "\"");
        itemLabel.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-font-weight: 600;"
        );
        
        Label warningLabel = new Label("Hành động này không thể hoàn tác!");
        warningLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-font-style: italic;"
        );
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button btnCancel = new Button("Hủy");
        btnCancel.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #616161;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 32px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120;"
        );
        btnCancel.setOnMouseEntered(e -> btnCancel.setStyle(
            btnCancel.getStyle() + "-fx-background-color: #e0e0e0;"
        ));
        btnCancel.setOnMouseExited(e -> btnCancel.setStyle(
            btnCancel.getStyle().replace("-fx-background-color: #e0e0e0;", "-fx-background-color: #f5f5f5;")
        ));
        btnCancel.setOnAction(e -> dialogStage.close());
        
        Button btnDelete = new Button("Xác nhận");
        btnDelete.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 32px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120;"
        );
        btnDelete.setOnMouseEntered(e -> btnDelete.setOpacity(0.9));
        btnDelete.setOnMouseExited(e -> btnDelete.setOpacity(1.0));
        btnDelete.setOnAction(e -> {
            dialogStage.close();
            onConfirm.run();
        });
        
        buttonBox.getChildren().addAll(btnCancel, btnDelete);
        
        content.getChildren().addAll(iconContainer, message, subMessage, itemLabel, warningLabel, buttonBox);
        
        Scene scene = new Scene(content);
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
    
    private void showSuccessAlert(String title, String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(title);
        
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(35, 50, 35, 50));
        content.setStyle("-fx-background-color: white;");
        
        // Success icon with blue theme
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-background-radius: 50;" +
            "-fx-pref-width: 70;" +
            "-fx-pref-height: 70;"
        );
        
        Label iconLabel = new Label("✓");
        iconLabel.setStyle(
            "-fx-font-size: 42px;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-font-weight: bold;"
        );
        iconContainer.getChildren().add(iconLabel);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-text-fill: #212121;" +
            "-fx-font-weight: 600;"
        );
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #757575;"
        );
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);
        messageLabel.setAlignment(Pos.CENTER);
        
        Button btnOk = new Button("OK");
        btnOk.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 40px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120;"
        );
        btnOk.setOnMouseEntered(e -> btnOk.setOpacity(0.9));
        btnOk.setOnMouseExited(e -> btnOk.setOpacity(1.0));
        btnOk.setOnAction(e -> dialogStage.close());
        
        content.getChildren().addAll(iconContainer, titleLabel, messageLabel, btnOk);
        
        Scene scene = new Scene(content);
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
    
    private void showErrorAlert(String title, String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(title);
        
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(35, 50, 35, 50));
        content.setStyle("-fx-background-color: white;");
        
        // Error icon with orange/warning theme (not red)
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle(
            "-fx-background-color: #FFF3E0;" +
            "-fx-background-radius: 50;" +
            "-fx-pref-width: 70;" +
            "-fx-pref-height: 70;"
        );
        
        Label iconLabel = new Label("!");
        iconLabel.setStyle(
            "-fx-font-size: 42px;" +
            "-fx-text-fill: #FF9800;" +
            "-fx-font-weight: bold;"
        );
        iconContainer.getChildren().add(iconLabel);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-text-fill: #212121;" +
            "-fx-font-weight: 600;"
        );
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #757575;"
        );
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);
        messageLabel.setAlignment(Pos.CENTER);
        
        Button btnOk = new Button("OK");
        btnOk.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 40px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 120;"
        );
        btnOk.setOnMouseEntered(e -> btnOk.setOpacity(0.9));
        btnOk.setOnMouseExited(e -> btnOk.setOpacity(1.0));
        btnOk.setOnAction(e -> dialogStage.close());
        
        content.getChildren().addAll(iconContainer, titleLabel, messageLabel, btnOk);
        
        Scene scene = new Scene(content);
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    // ==========================================
    // ===== APPOINTMENT MANAGEMENT WORKFLOW =====
    // ==========================================

    private VBox appointmentTableRows;

    private void setupAppointmentNotificationTimeline() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(60),
                event -> checkAndShowAppointmentNotifications()
            )
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
        
        // Kiểm tra ngay sau khi khởi động
        javafx.application.Platform.runLater(() -> checkAndShowAppointmentNotifications());
    }

    private void checkAndShowAppointmentNotifications() {
        service.AppointmentService appointmentService = new service.AppointmentService();
        List<model.Appointment> upcoming = appointmentService.getUpcomingUnremindedAppointments();
        for (model.Appointment appt : upcoming) {
            showAppointmentReminderDialog(appt);
        }
    }

    private void showAppointmentReminderDialog(model.Appointment appt) {
        // Đánh dấu đã nhắc ngay lập tức để tránh trùng lặp
        appt.setReminded(1);
        new service.AppointmentService().updateAppointment(appt);

        javafx.application.Platform.runLater(() -> {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("🔔 Nhắc Nhở Lịch Hẹn Sắp Diễn Ra");
            
            VBox content = new VBox(20);
            content.setPadding(new Insets(25));
            content.setAlignment(Pos.CENTER);
            content.setStyle("-fx-background-color: white;");
            
            Label titleLabel = new Label("🔔 LỊCH HẸN SẮP DIỄN RA");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E65100;");
            
            GridPane grid = new GridPane();
            grid.setHgap(15);
            grid.setVgap(12);
            grid.setAlignment(Pos.CENTER);
            
            Label lblCust = new Label("Khách hàng:");
            lblCust.setStyle("-fx-font-weight: bold;");
            Label txtCust = new Label(appt.getCustomerName() + " (" + appt.getPhone() + ")");
            
            Label lblTime = new Label("Thời gian hẹn:");
            lblTime.setStyle("-fx-font-weight: bold;");
            Label txtTime = new Label(appt.getAppointmentTime() + " ngày " + appt.getAppointmentDate());
            
            Label lblVehicle = new Label("Phương tiện:");
            lblVehicle.setStyle("-fx-font-weight: bold;");
            Label txtVehicle = new Label(appt.getLicensePlate() + " (" + appt.getVehicleType() + ")");
            
            Label lblService = new Label("Dịch vụ đăng ký:");
            lblService.setStyle("-fx-font-weight: bold;");
            Label txtService = new Label(appt.getServiceName());
            
            grid.add(lblCust, 0, 0); grid.add(txtCust, 1, 0);
            grid.add(lblTime, 0, 1); grid.add(txtTime, 1, 1);
            grid.add(lblVehicle, 0, 2); grid.add(txtVehicle, 1, 2);
            grid.add(lblService, 0, 3); grid.add(txtService, 1, 3);
            
            Button btnAcknowledge = new Button("Xác nhận đã xem");
            btnAcknowledge.setStyle(
                "-fx-background-color: #2196F3;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10px 25px;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
            );
            btnAcknowledge.setOnAction(e -> dialogStage.close());
            
            content.getChildren().addAll(titleLabel, grid, btnAcknowledge);
            Scene scene = new Scene(content, 450, 280);
            dialogStage.setScene(scene);
            dialogStage.show();
        });
    }

    private VBox createTodayAppointmentsSection() {
        VBox section = new VBox(15);
        section.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 25;"
        );
        
        Label sectionTitle = new Label("📅 Lịch Hẹn Hôm Nay");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        service.AppointmentService appointmentService = new service.AppointmentService();
        List<model.Appointment> todayAppts = appointmentService.getAppointmentsByDate(LocalDate.now().toString());
        
        VBox listContainer = new VBox(10);
        if (todayAppts.isEmpty()) {
            Label emptyLabel = new Label("Không có lịch hẹn nào trong hôm nay.");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #9e9e9e; -fx-font-style: italic;");
            listContainer.getChildren().add(emptyLabel);
        } else {
            HBox header = new HBox(10);
            header.setStyle("-fx-background-color: #F9FAFB; -fx-padding: 10px 12px; -fx-background-radius: 6; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
            
            Label lblTimeHeader = new Label("Thời gian");
            lblTimeHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #374151;");
            lblTimeHeader.setPrefWidth(100); lblTimeHeader.setMinWidth(100); lblTimeHeader.setMaxWidth(100);
            
            Label lblCustomerHeader = new Label("Khách hàng");
            lblCustomerHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #374151;");
            lblCustomerHeader.setPrefWidth(160); lblCustomerHeader.setMinWidth(160); lblCustomerHeader.setMaxWidth(160);
            
            Label lblVehicleHeader = new Label("Xe");
            lblVehicleHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #374151;");
            lblVehicleHeader.setPrefWidth(120); lblVehicleHeader.setMinWidth(120); lblVehicleHeader.setMaxWidth(120);
            
            Label lblServiceHeader = new Label("Dịch vụ");
            lblServiceHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #374151;");
            lblServiceHeader.setPrefWidth(150); lblServiceHeader.setMinWidth(150); lblServiceHeader.setMaxWidth(Double.MAX_VALUE);
            
            Label lblStatusHeader = new Label("Trạng thái");
            lblStatusHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #374151;");
            lblStatusHeader.setPrefWidth(100); lblStatusHeader.setMinWidth(100); lblStatusHeader.setMaxWidth(100);
            lblStatusHeader.setAlignment(Pos.CENTER);
            
            header.getChildren().addAll(lblTimeHeader, lblCustomerHeader, lblVehicleHeader, lblServiceHeader, lblStatusHeader);
            HBox.setHgrow(lblServiceHeader, Priority.ALWAYS);
            listContainer.getChildren().add(header);
            
            for (model.Appointment appt : todayAppts) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 10px 12px; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-background-color: white;");
                
                row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 10px 12px; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-background-color: #f9fafb;"));
                row.setOnMouseExited(e -> row.setStyle("-fx-padding: 10px 12px; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-background-color: white;"));
                
                Label lblTime = new Label(appt.getAppointmentTime());
                lblTime.setStyle("-fx-font-size: 13px; -fx-text-fill: #212121; -fx-font-weight: bold;");
                lblTime.setPrefWidth(100); lblTime.setMinWidth(100); lblTime.setMaxWidth(100);
                
                Label lblCustomer = new Label(appt.getCustomerName() + "\n" + appt.getPhone());
                lblCustomer.setStyle("-fx-font-size: 13px; -fx-text-fill: #212121;");
                lblCustomer.setPrefWidth(160); lblCustomer.setMinWidth(160); lblCustomer.setMaxWidth(160);
                
                Label lblVehicle = new Label(appt.getLicensePlate() + "\n" + appt.getVehicleType());
                lblVehicle.setStyle("-fx-font-size: 13px; -fx-text-fill: #212121;");
                lblVehicle.setPrefWidth(120); lblVehicle.setMinWidth(120); lblVehicle.setMaxWidth(120);
                
                Label lblService = new Label(appt.getServiceName());
                lblService.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
                lblService.setPrefWidth(150); lblService.setMinWidth(150); lblService.setMaxWidth(Double.MAX_VALUE);
                lblService.setWrapText(true);
                
                HBox statusBox = new HBox(0);
                statusBox.setAlignment(Pos.CENTER);
                statusBox.setPrefWidth(100); statusBox.setMinWidth(100); statusBox.setMaxWidth(100);
                
                Label lblStatus = new Label(appt.getStatus());
                lblStatus.setStyle("-fx-padding: 4px 8px; -fx-background-radius: 4; -fx-text-fill: white; -fx-alignment: center; -fx-font-weight: bold; -fx-font-size: 12px;");
                if (appt.getStatus().equals("Chờ")) {
                    lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #ff9800;");
                } else if (appt.getStatus().equals("Đang thực hiện")) {
                    lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #2196F3;");
                } else if (appt.getStatus().equals("Đã hoàn thành")) {
                    lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #4CAF50;");
                } else {
                    lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #9e9e9e;");
                }
                statusBox.getChildren().add(lblStatus);
                
                row.getChildren().addAll(lblTime, lblCustomer, lblVehicle, lblService, statusBox);
                HBox.setHgrow(lblService, Priority.ALWAYS);
                listContainer.getChildren().add(row);
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setMaxHeight(250);
        
        section.getChildren().addAll(sectionTitle, scrollPane);
        return section;
    }

    private void showAppointmentManagement() {
        currentView = "appointment";
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📅 Quản Lý Lịch Hẹn");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnNew = new Button("+ Tạo Lịch Hẹn Mới");
        btnNew.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 24px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnNew.setOnMouseEntered(e -> btnNew.setOpacity(0.9));
        btnNew.setOnMouseExited(e -> btnNew.setOpacity(1.0));
        btnNew.setOnAction(e -> showCreateAppointmentDialog(null));
        
        header.getChildren().addAll(title, spacer, btnNew);

        // Search and filter bar
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(20));
        filterBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Tìm kiếm lịch hẹn...");
        searchField.setPrefWidth(200);
        searchField.setMinWidth(200);
        searchField.setMaxWidth(200);
        searchField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        
        ComboBox<String> cbStatusFilter = new ComboBox<>();
        cbStatusFilter.getItems().addAll("Tất cả trạng thái", "Chờ", "Đang thực hiện", "Đã hoàn thành", "Đã hủy");
        cbStatusFilter.setValue("Tất cả trạng thái");
        cbStatusFilter.setPrefWidth(150);
        cbStatusFilter.setMinWidth(150);
        cbStatusFilter.setMaxWidth(150);
        cbStatusFilter.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        ComboBox<String> cbPeriod = new ComboBox<>();
        cbPeriod.getItems().addAll("Tất cả mốc thời gian", "Tuần này", "Tháng này", "Quý này", "Năm nay");
        cbPeriod.setValue("Tất cả mốc thời gian");
        cbPeriod.setPrefWidth(180);
        cbPeriod.setMinWidth(180);
        cbPeriod.setMaxWidth(180);
        cbPeriod.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        ComboBox<String> cbMonth = new ComboBox<>();
        cbMonth.getItems().add("Tất cả các tháng");
        for (int m = 1; m <= 12; m++) {
            cbMonth.getItems().add("Tháng " + m);
        }
        cbMonth.setValue("Tất cả các tháng");
        cbMonth.setPrefWidth(150);
        cbMonth.setMinWidth(150);
        cbMonth.setMaxWidth(150);
        cbMonth.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        ComboBox<String> cbYear = new ComboBox<>();
        cbYear.getItems().add("Tất cả các năm");
        cbYear.getItems().addAll(getAvailableYears());
        cbYear.setValue("Tất cả các năm");
        cbYear.setPrefWidth(140);
        cbYear.setMinWidth(140);
        cbYear.setMaxWidth(140);
        cbYear.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        Runnable filterAction = () -> {
            String search = searchField.getText().trim();
            String status = cbStatusFilter.getValue();
            refreshAppointmentTableWithFilter(
                search,
                status,
                cbPeriod.getValue(),
                cbMonth.getValue(),
                cbYear.getValue()
            );
        };
        
        searchField.textProperty().addListener((obs, old, newVal) -> filterAction.run());
        cbStatusFilter.valueProperty().addListener((obs, old, newVal) -> filterAction.run());
        cbPeriod.valueProperty().addListener((obs, old, newVal) -> filterAction.run());
        cbMonth.valueProperty().addListener((obs, old, newVal) -> filterAction.run());
        cbYear.valueProperty().addListener((obs, old, newVal) -> filterAction.run());
        
        filterBar.getChildren().addAll(searchField, cbStatusFilter, cbPeriod, cbMonth, cbYear);

        // Appointment List Container
        VBox tableContainer = new VBox(0);
        tableContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 20;"
        );
        
        Label tableTitle = new Label("Danh Sách Lịch Hẹn");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0;");
        
        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 0, 12, 0));
        tableHeader.setStyle(
            "-fx-background-color: #F9FAFB;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        Label colId = new Label("Mã"); colId.setPrefWidth(50); colId.setMinWidth(50); colId.setMaxWidth(50); colId.setPadding(new Insets(0, 5, 0, 5)); colId.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;"); colId.setAlignment(Pos.CENTER_LEFT);
        Label colDateTime = new Label("Thời Gian"); colDateTime.setPrefWidth(120); colDateTime.setMinWidth(120); colDateTime.setMaxWidth(120); colDateTime.setPadding(new Insets(0, 5, 0, 5)); colDateTime.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;"); colDateTime.setAlignment(Pos.CENTER_LEFT);
        Label colCustomer = new Label("Khách Hàng"); colCustomer.setPrefWidth(160); colCustomer.setMinWidth(160); colCustomer.setMaxWidth(160); colCustomer.setPadding(new Insets(0, 5, 0, 5)); colCustomer.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;"); colCustomer.setAlignment(Pos.CENTER_LEFT);
        Label colVehicle = new Label("Xe"); colVehicle.setPrefWidth(110); colVehicle.setMinWidth(110); colVehicle.setMaxWidth(110); colVehicle.setPadding(new Insets(0, 5, 0, 5)); colVehicle.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;"); colVehicle.setAlignment(Pos.CENTER_LEFT);
        Label colService = new Label("Dịch Vụ"); colService.setPrefWidth(150); colService.setMinWidth(150); colService.setMaxWidth(Double.MAX_VALUE); colService.setPadding(new Insets(0, 5, 0, 5)); colService.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;"); colService.setAlignment(Pos.CENTER_LEFT);
        Label colStatus = new Label("Trạng Thái"); colStatus.setPrefWidth(110); colStatus.setMinWidth(110); colStatus.setMaxWidth(110); colStatus.setPadding(new Insets(0, 5, 0, 5)); colStatus.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;"); colStatus.setAlignment(Pos.CENTER);
        Label colActions = new Label("Thao Tác"); colActions.setPrefWidth(170); colActions.setMinWidth(170); colActions.setMaxWidth(170); colActions.setPadding(new Insets(0, 5, 0, 5)); colActions.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;"); colActions.setAlignment(Pos.CENTER);
        
        tableHeader.getChildren().addAll(colId, colDateTime, colCustomer, colVehicle, colService, colStatus, colActions);
        HBox.setHgrow(colService, Priority.ALWAYS);
        
        appointmentTableRows = new VBox(0);
        
        ScrollPane scrollPane = new ScrollPane(appointmentTableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        tableContainer.getChildren().addAll(tableTitle, tableHeader, scrollPane);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        view.getChildren().addAll(header, filterBar, tableContainer);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
        
        refreshAppointmentTableWithFilter("", "Tất cả trạng thái", "Tất cả mốc thời gian", "Tất cả các tháng", "Tất cả các năm");
    }

    private void refreshAppointmentTableWithFilter(String searchText, String status, String period, String month, String year) {
        appointmentTableRows.getChildren().clear();
        service.AppointmentService appointmentService = new service.AppointmentService();
        List<model.Appointment> list = appointmentService.getAllAppointments();
        
        // Filter status
        if (status != null && !status.trim().isEmpty() && !status.equals("Tất cả trạng thái")) {
            list = list.stream().filter(a -> a.getStatus().equals(status)).collect(java.util.stream.Collectors.toList());
        }
        // Filter by time filters
        list = list.stream()
            .filter(a -> matchesTimeFilters(a.getAppointmentDate(), period, month, year))
            .collect(java.util.stream.Collectors.toList());
        // Filter search
        if (searchText != null && !searchText.trim().isEmpty()) {
            String query = searchText.toLowerCase().trim();
            list = list.stream().filter(a -> 
                a.getCustomerName().toLowerCase().contains(query) ||
                a.getPhone().contains(query) ||
                a.getLicensePlate().toLowerCase().contains(query) ||
                a.getServiceName().toLowerCase().contains(query)
            ).collect(java.util.stream.Collectors.toList());
        }
        
        if (list.isEmpty()) {
            Label empty = new Label("Không tìm thấy lịch hẹn nào.");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #9e9e9e; -fx-padding: 30px;");
            appointmentTableRows.getChildren().add(empty);
        } else {
            for (model.Appointment appt : list) {
                appointmentTableRows.getChildren().add(createAppointmentRow(appt));
            }
        }
    }

    private HBox createAppointmentRow(model.Appointment appt) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0));
        row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #f9fafb;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        ));
        
        Label colId = new Label(String.valueOf(appt.getId()));
        colId.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121; -fx-font-weight: 500;");
        colId.setPrefWidth(50);
        colId.setMinWidth(50);
        colId.setMaxWidth(50);
        colId.setPadding(new Insets(12, 5, 12, 5));
        colId.setAlignment(Pos.CENTER_LEFT);
        
        Label colDateTime = new Label(appt.getAppointmentTime() + "\n" + appt.getAppointmentDate());
        colDateTime.setStyle("-fx-font-size: 13px; -fx-text-fill: #212121; -fx-font-weight: bold;");
        colDateTime.setPrefWidth(120);
        colDateTime.setMinWidth(120);
        colDateTime.setMaxWidth(120);
        colDateTime.setPadding(new Insets(12, 5, 12, 5));
        colDateTime.setAlignment(Pos.CENTER_LEFT);
        
        Label colCustomer = new Label(appt.getCustomerName() + "\n" + appt.getPhone());
        colCustomer.setStyle("-fx-font-size: 13px; -fx-text-fill: #212121;");
        colCustomer.setPrefWidth(160);
        colCustomer.setMinWidth(160);
        colCustomer.setMaxWidth(160);
        colCustomer.setPadding(new Insets(12, 5, 12, 5));
        colCustomer.setAlignment(Pos.CENTER_LEFT);
        
        Label colVehicle = new Label(appt.getLicensePlate() + "\n" + appt.getVehicleType());
        colVehicle.setStyle("-fx-font-size: 13px; -fx-text-fill: #212121;");
        colVehicle.setPrefWidth(110);
        colVehicle.setMinWidth(110);
        colVehicle.setMaxWidth(110);
        colVehicle.setPadding(new Insets(12, 5, 12, 5));
        colVehicle.setAlignment(Pos.CENTER_LEFT);
        
        Label colService = new Label(appt.getServiceName());
        colService.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        colService.setPrefWidth(150);
        colService.setMinWidth(150);
        colService.setMaxWidth(Double.MAX_VALUE);
        colService.setWrapText(true);
        colService.setPadding(new Insets(12, 5, 12, 5));
        colService.setAlignment(Pos.CENTER_LEFT);
        
        HBox statusBox = new HBox(0);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPrefWidth(110);
        statusBox.setMinWidth(110);
        statusBox.setMaxWidth(110);
        statusBox.setPadding(new Insets(12, 5, 12, 5));
        
        Label colStatus = new Label(appt.getStatus());
        colStatus.setStyle("-fx-padding: 4px 8px; -fx-background-radius: 4; -fx-text-fill: white; -fx-alignment: center; -fx-font-weight: bold; -fx-font-size: 12px;");
        if (appt.getStatus().equals("Chờ")) {
            colStatus.setStyle(colStatus.getStyle() + "-fx-background-color: #ff9800;");
        } else if (appt.getStatus().equals("Đang thực hiện")) {
            colStatus.setStyle(colStatus.getStyle() + "-fx-background-color: #2196F3;");
        } else if (appt.getStatus().equals("Đã hoàn thành")) {
            colStatus.setStyle(colStatus.getStyle() + "-fx-background-color: #4CAF50;");
        } else {
            colStatus.setStyle(colStatus.getStyle() + "-fx-background-color: #9e9e9e;");
        }
        statusBox.getChildren().add(colStatus);
        
        HBox actions = new HBox(8);
        actions.setPrefWidth(170);
        actions.setMinWidth(170);
        actions.setMaxWidth(170);
        actions.setPadding(new Insets(12, 5, 12, 5));
        actions.setAlignment(Pos.CENTER);
        
        if (appt.getStatus().equals("Chờ")) {
            Button btnStart = new Button("Nhận Xe");
            btnStart.setStyle(
                "-fx-background-color: #2196F3;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6px 12px;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
            );
            btnStart.setOnAction(e -> {
                appt.setStatus("Đang thực hiện");
                new service.AppointmentService().updateAppointment(appt);
                showAppointmentManagement();
            });
            
            Button btnEdit = new Button("✏");
            btnEdit.setStyle(
                "-fx-background-color: #E3F2FD;" +
                "-fx-text-fill: #2196F3;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 8px 10px;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-min-width: 32;" +
                "-fx-min-height: 32;"
            );
            btnEdit.setOnAction(e -> showCreateAppointmentDialog(appt));
            
            Button btnCancel = new Button("🗑");
            btnCancel.setStyle(
                "-fx-background-color: #FFEBEE;" +
                "-fx-text-fill: #f44336;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 8px 10px;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-min-width: 32;" +
                "-fx-min-height: 32;"
            );
            btnCancel.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn hủy lịch hẹn này?", ButtonType.YES, ButtonType.NO);
                alert.setTitle("Xác nhận hủy");
                alert.setHeaderText(null);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        appt.setStatus("Đã hủy");
                        new service.AppointmentService().updateAppointment(appt);
                        showAppointmentManagement();
                    }
                });
            });
            
            actions.getChildren().addAll(btnStart, btnEdit, btnCancel);
            
        } else if (appt.getStatus().equals("Đang thực hiện")) {
            Button btnInvoice = new Button("Lập Hóa Đơn");
            btnInvoice.setStyle(
                "-fx-background-color: #2196F3;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6px 12px;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
            );
            btnInvoice.setOnAction(e -> {
                CreateInvoiceForm form = new CreateInvoiceForm(() -> {
                    showAppointmentManagement();
                }, appt);
                form.show();
            });
            Button btnCancel = new Button("🗑");
            btnCancel.setStyle(
                "-fx-background-color: #FFEBEE;" +
                "-fx-text-fill: #f44336;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 8px 10px;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-min-width: 32;" +
                "-fx-min-height: 32;"
            );
            btnCancel.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn hủy lịch hẹn này?", ButtonType.YES, ButtonType.NO);
                alert.setTitle("Xác nhận hủy");
                alert.setHeaderText(null);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        appt.setStatus("Đã hủy");
                        new service.AppointmentService().updateAppointment(appt);
                        showAppointmentManagement();
                    }
                });
            });
            
            actions.getChildren().addAll(btnInvoice, btnCancel);
        } else {
            Label lblDone = new Label(appt.getStatus().equals("Đã hoàn thành") ? "✓ Đã Hoàn Thành" : "✗ Đã Hủy");
            lblDone.setStyle("-fx-text-fill: #757575; -fx-font-style: italic; -fx-font-size: 13px;");
            actions.getChildren().add(lblDone);
        }
        
        row.getChildren().addAll(colId, colDateTime, colCustomer, colVehicle, colService, statusBox, actions);
        HBox.setHgrow(colService, Priority.ALWAYS);
        return row;
    }

    private void showCreateAppointmentDialog(model.Appointment existing) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(existing != null ? "Sửa Lịch Hẹn" : "Tạo Lịch Hẹn Mới");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white;");
        
        Label title = new Label(existing != null ? "📅 Chỉnh Sửa Lịch Hẹn" : "📅 Tạo Lịch Hẹn Mới");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #212121;");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(140);
        col1.setPrefWidth(140);
        
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(300);
        col2.setPrefWidth(300);
        col2.setHgrow(Priority.ALWAYS);
        
        grid.getColumnConstraints().addAll(col1, col2);
        
        Label lblPhone = new Label("Số điện thoại *");
        lblPhone.setStyle("-fx-font-weight: 500;");
        TextField txtPhone = new TextField(existing != null ? existing.getPhone() : "");
        txtPhone.setPromptText("Nhập số điện thoại...");
        txtPhone.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");
        
        Label lblName = new Label("Tên khách hàng *");
        lblName.setStyle("-fx-font-weight: 500;");
        TextField txtName = new TextField(existing != null ? existing.getCustomerName() : "");
        txtName.setPromptText("Nhập tên khách hàng...");
        txtName.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");
        
        Label lblAddress = new Label("Địa chỉ");
        lblAddress.setStyle("-fx-font-weight: 500;");
        TextField txtAddress = new TextField(existing != null ? existing.getAddress() : "");
        txtAddress.setPromptText("Nhập địa chỉ...");
        txtAddress.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");
        
        Label lblPlate = new Label("Biển số xe *");
        lblPlate.setStyle("-fx-font-weight: 500;");
        TextField txtPlate = new TextField(existing != null ? existing.getLicensePlate() : "");
        txtPlate.setPromptText("Nhập biển số xe...");
        txtPlate.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");

        // Autocomplete search suggestions
        ListView<service.AppointmentService.CustomerInfo> suggestionList = new ListView<>();
        suggestionList.setPrefHeight(100);
        suggestionList.setVisible(false);
        suggestionList.setManaged(false);
        
        txtPhone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().length() >= 3) {
                List<service.AppointmentService.CustomerInfo> matches = new service.AppointmentService().searchCustomers(newVal);
                if (!matches.isEmpty()) {
                    suggestionList.getItems().setAll(matches);
                    suggestionList.setVisible(true);
                    suggestionList.setManaged(true);
                } else {
                    suggestionList.setVisible(false);
                    suggestionList.setManaged(false);
                }
            } else {
                suggestionList.setVisible(false);
                suggestionList.setManaged(false);
            }
        });
        
        suggestionList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(service.AppointmentService.CustomerInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getPhone() + " (" + item.getLicensePlate() + ")");
                }
            }
        });
        
        suggestionList.setOnMouseClicked(event -> {
            service.AppointmentService.CustomerInfo selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtName.setText(selected.getName());
                txtPhone.setText(selected.getPhone());
                txtAddress.setText(selected.getAddress() != null ? selected.getAddress() : "");
                txtPlate.setText(selected.getLicensePlate());
                suggestionList.setVisible(false);
                suggestionList.setManaged(false);
            }
        });
        
        Label lblVehicleType = new Label("Loại xe *");
        lblVehicleType.setStyle("-fx-font-weight: 500;");
        ComboBox<String> cbVehicleType = new ComboBox<>();
        cbVehicleType.getItems().addAll("Mini", "Sedan", "CUV", "SUV", "MPV", "Pickup");
        cbVehicleType.setValue(existing != null ? existing.getVehicleType() : "Sedan");
        cbVehicleType.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 6;");
        
        Label lblService = new Label("Dịch vụ *");
        lblService.setStyle("-fx-font-weight: 500;");
        
        ComboBox<String> cbService = new ComboBox<>();
        cbService.setMaxWidth(Double.MAX_VALUE);
        cbService.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 6;");
        
        List<String> allServices = new java.util.ArrayList<>();
        new service.ServiceService().getAllServices().forEach(s -> allServices.add(s.getName()));
        new service.PackageService().getAllPackages().forEach(p -> allServices.add(p.getName()));
        cbService.getItems().addAll(allServices);
        
        TextField txtSearchService = new TextField();
        txtSearchService.setPromptText("🔍 Nhập từ khóa để lọc dịch vụ lẻ/gói...");
        txtSearchService.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 8px; -fx-background-radius: 6;");
        txtSearchService.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal.trim().toLowerCase();
            if (query.isEmpty()) {
                cbService.getItems().setAll(allServices);
            } else {
                List<String> filtered = new java.util.ArrayList<>();
                for (String s : allServices) {
                    if (s.toLowerCase().contains(query)) {
                        filtered.add(s);
                    }
                }
                cbService.getItems().setAll(filtered);
                if (!filtered.isEmpty()) {
                    cbService.setValue(filtered.get(0));
                }
            }
        });
        
        VBox serviceSelectBox = new VBox(5);
        serviceSelectBox.getChildren().addAll(txtSearchService, cbService);
        
        if (existing != null) {
            cbService.setValue(existing.getServiceName());
        } else if (!cbService.getItems().isEmpty()) {
            cbService.setValue(cbService.getItems().get(0));
        }
        
        Label lblDate = new Label("Ngày hẹn *");
        lblDate.setStyle("-fx-font-weight: 500;");
        DatePicker dpDate = new DatePicker(existing != null ? LocalDate.parse(existing.getAppointmentDate()) : LocalDate.now());
        dpDate.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 6;");
        
        Label lblTime = new Label("Giờ hẹn *");
        lblTime.setStyle("-fx-font-weight: 500;");
        HBox timeBox = new HBox(5);
        timeBox.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> cbHour = new ComboBox<>();
        for (int i = 8; i <= 17; i++) {
            cbHour.getItems().add(String.format("%02d", i));
        }
        ComboBox<String> cbMin = new ComboBox<>();
        cbMin.getItems().addAll("00", "15", "30", "45");
        
        if (existing != null) {
            String[] parts = existing.getAppointmentTime().split(":");
            cbHour.setValue(parts[0]);
            cbMin.setValue(parts[1]);
        } else {
            cbHour.setValue("09");
            cbMin.setValue("00");
        }
        cbHour.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 6;");
        cbMin.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 6;");
        timeBox.getChildren().addAll(cbHour, new Label(":"), cbMin);
        
        Label lblDuration = new Label("Dự kiến hoàn thành");
        lblDuration.setStyle("-fx-font-weight: 500;");
        TextField txtDuration = new TextField(existing != null ? existing.getExpectedCompletion() : "2 giờ");
        txtDuration.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");
        
        Label lblNotes = new Label("Ghi chú");
        lblNotes.setStyle("-fx-font-weight: 500;");
        TextArea txtNotes = new TextArea(existing != null ? existing.getNotes() : "");
        txtNotes.setPrefRowCount(3);
        txtNotes.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 6;");
        txtNotes.setPromptText("Ghi chú nếu có...");
        
        grid.add(lblPhone, 0, 0); grid.add(txtPhone, 1, 0);
        grid.add(lblName, 0, 1); grid.add(txtName, 1, 1);
        grid.add(lblAddress, 0, 2); grid.add(txtAddress, 1, 2);
        grid.add(lblPlate, 0, 3); grid.add(txtPlate, 1, 3);
        grid.add(lblVehicleType, 0, 4); grid.add(cbVehicleType, 1, 4);
        grid.add(lblService, 0, 5); grid.add(serviceSelectBox, 1, 5);
        grid.add(lblDate, 0, 6); grid.add(dpDate, 1, 6);
        grid.add(lblTime, 0, 7); grid.add(timeBox, 1, 7);
        grid.add(lblDuration, 0, 8); grid.add(txtDuration, 1, 8);
        grid.add(lblNotes, 0, 9); grid.add(txtNotes, 1, 9);
        
        VBox mainForm = new VBox(10);
        mainForm.getChildren().addAll(grid, suggestionList);

        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancel = new Button("Hủy");
        btnCancel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 6; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> dialogStage.close());
        
        Button btnSave = new Button("Lưu Lịch Hẹn");
        btnSave.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 25px; -fx-background-radius: 6; -fx-cursor: hand;");
        btnSave.setOnAction(e -> {
            if (txtPhone.getText().trim().isEmpty() || txtName.getText().trim().isEmpty() || txtPlate.getText().trim().isEmpty() || cbService.getValue() == null) {
                showErrorAlert("Lỗi nhập liệu", "Vui lòng điền đầy đủ các thông tin bắt buộc (*)");
                return;
            }
            
            String timeStr = cbHour.getValue() + ":" + cbMin.getValue();
            String dateStr = dpDate.getValue().toString();
            String plate = txtPlate.getText().trim();
            int currentId = existing != null ? existing.getId() : -1;
            
            service.AppointmentService appointmentService = new service.AppointmentService();
            String warning = appointmentService.checkConflictOrOverload(dateStr, timeStr, plate, currentId);
            if (warning != null) {
                List<String> alternates = appointmentService.suggestAlternativeTimes(dateStr, timeStr, plate, currentId);
                showConflictWarningDialog(warning, alternates, chosenTime -> {
                    String[] tParts = chosenTime.split(":");
                    cbHour.setValue(tParts[0]);
                    cbMin.setValue(tParts[1]);
                });
                return;
            }
            
            model.Appointment appt = existing != null ? existing : new model.Appointment();
            appt.setPhone(txtPhone.getText().trim());
            appt.setCustomerName(txtName.getText().trim());
            appt.setAddress(txtAddress.getText().trim());
            appt.setLicensePlate(plate);
            appt.setVehicleType(cbVehicleType.getValue());
            appt.setServiceName(cbService.getValue());
            appt.setAppointmentDate(dateStr);
            appt.setAppointmentTime(timeStr);
            appt.setExpectedCompletion(txtDuration.getText().trim());
            appt.setNotes(txtNotes.getText().trim());
            appt.setStatus(existing != null ? existing.getStatus() : "Chờ");
            appt.setReminded(existing != null ? existing.getReminded() : 0);
            
            boolean ok;
            if (existing != null) {
                ok = appointmentService.updateAppointment(appt);
            } else {
                ok = appointmentService.addAppointment(appt);
            }
            
            if (ok) {
                showSuccessAlert("Thành công", "Lưu lịch hẹn thành công!");
                dialogStage.close();
                if ("appointment".equals(currentView)) {
                    showAppointmentManagement();
                } else {
                    showDashboard();
                }
            } else {
                showErrorAlert("Lỗi", "Không thể lưu lịch hẹn!");
            }
        });
        
        btnBox.getChildren().addAll(btnCancel, btnSave);
        root.getChildren().addAll(title, mainForm, btnBox);
        
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setPrefHeight(600);
        Scene scene = new Scene(sp, 600, 660);
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showConflictWarningDialog(String warningMsg, List<String> alternates, java.util.function.Consumer<String> onTimeSelected) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("⚠ Cảnh Báo Trùng / Quá Tải Lịch Hẹn");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: white;");
        
        Label iconLabel = new Label("⚠");
        iconLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: #E65100;");
        
        Label titleLabel = new Label("Trùng Hoặc Quá Tải Lịch Hẹn");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #212121;");
        
        Label msgLabel = new Label(warningMsg);
        msgLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
        msgLabel.setWrapText(true);
        msgLabel.setAlignment(Pos.CENTER);
        
        Label suggestLabel = new Label("Đề xuất các khung giờ phù hợp khác trong ngày:");
        suggestLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #757575;");
        
        VBox alternatesBox = new VBox(8);
        alternatesBox.setAlignment(Pos.CENTER);
        for (String altTime : alternates) {
            Button btnAlt = new Button(altTime);
            btnAlt.setStyle(
                "-fx-background-color: #E3F2FD;" +
                "-fx-text-fill: #1976D2;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px 25px;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-min-width: 200;"
            );
            btnAlt.setOnAction(e -> {
                String cleanTime = altTime.split(" ")[0];
                onTimeSelected.accept(cleanTime);
                dialogStage.close();
            });
            alternatesBox.getChildren().add(btnAlt);
        }
        
        Button btnClose = new Button("Đóng để chọn thủ công");
        btnClose.setStyle(
            "-fx-background-color: #e0e0e0;" +
            "-fx-text-fill: #424242;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        btnClose.setOnAction(e -> dialogStage.close());
        
        content.getChildren().addAll(iconLabel, titleLabel, msgLabel, suggestLabel, alternatesBox, btnClose);
        Scene scene = new Scene(content, 450, 480);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private void showHRManagement() {
        currentView = "hr";
        HRHelper.showHRManagement(this, contentArea);
    }

    private VBox createEmployeeTab() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20, 0, 20, 0));
        root.setStyle("-fx-background-color: transparent;");

        // Filter & Add bar
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Tìm kiếm nhân viên (Tên hoặc mã)...");
        searchField.setPrefWidth(350);
        searchField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAdd = new Button("+ Thêm Nhân Viên Mới");
        btnAdd.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        topBar.getChildren().addAll(searchField, spacer, btnAdd);

        // Table container
        VBox tableContainer = new VBox(0);
        tableContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 5);"
        );

        // Table Header
        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(15, 20, 15, 20));
        tableHeader.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 12 12 0 0; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        Label colCode = new Label("Mã NV");
        colCode.setPrefWidth(90);
        colCode.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 13px;");

        Label colName = new Label("Họ Tên");
        colName.setPrefWidth(180);
        colName.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 13px;");

        Label colPhone = new Label("Số Điện Thoại");
        colPhone.setPrefWidth(120);
        colPhone.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 13px;");

        Label colPosition = new Label("Chức Vụ");
        colPosition.setPrefWidth(140);
        colPosition.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 13px;");

        Label colSalary = new Label("Lương Cơ Bản");
        colSalary.setPrefWidth(130);
        colSalary.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 13px;");

        Label colStartDate = new Label("Ngày Vào Làm");
        colStartDate.setPrefWidth(120);
        colStartDate.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 13px;");

        Label colAction = new Label("Thao Tác");
        colAction.setPrefWidth(100);
        colAction.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 13px;");

        tableHeader.getChildren().addAll(colCode, colName, colPhone, colPosition, colSalary, colStartDate, colAction);

        VBox tableRows = new VBox(0);
        ScrollPane scrollPane = new ScrollPane(tableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        root.getChildren().addAll(topBar, tableContainer);

        Runnable refreshList = () -> {
            tableRows.getChildren().clear();
            EmployeeService empService = new EmployeeService();
            List<Employee> list = empService.getAllEmployees();
            String query = searchField.getText().toLowerCase().trim();
            for (Employee emp : list) {
                if (query.isEmpty() || emp.getName().toLowerCase().contains(query) || emp.getEmployeeCode().toLowerCase().contains(query)) {
                    tableRows.getChildren().add(createEmployeeRow(emp, () -> {
                        // Refresh after action
                        searchField.setText(searchField.getText());
                    }));
                }
            }
        };

        searchField.textProperty().addListener((obs, old, val) -> refreshList.run());
        btnAdd.setOnAction(e -> showEmployeeDialog(null, refreshList));

        refreshList.run();
        return root;
    }

    private HBox createEmployeeRow(Employee emp, Runnable onRefresh) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 20, 12, 20));
        row.setStyle("-fx-background-color: white; -fx-border-color: #f1f3f9; -fx-border-width: 0 0 1 0;");

        Label colCode = new Label(emp.getEmployeeCode());
        colCode.setPrefWidth(90);
        colCode.setStyle("-fx-font-weight: bold; -fx-text-fill: #1976D2;");

        Label colName = new Label(emp.getName());
        colName.setPrefWidth(180);
        colName.setStyle("-fx-font-weight: 500; -fx-text-fill: #212121;");

        Label colPhone = new Label(emp.getPhone() != null ? emp.getPhone() : "-");
        colPhone.setPrefWidth(120);
        colPhone.setStyle("-fx-text-fill: #424242;");

        Label colPosition = new Label(emp.getPosition() != null ? emp.getPosition() : "-");
        colPosition.setPrefWidth(140);
        colPosition.setStyle("-fx-text-fill: #424242;");

        Label colSalary = new Label(String.format("%,.0f đ", emp.getBasicSalary()));
        colSalary.setPrefWidth(130);
        colSalary.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");

        Label colStartDate = new Label(emp.getStartDate() != null ? emp.getStartDate() : "-");
        colStartDate.setPrefWidth(120);
        colStartDate.setStyle("-fx-text-fill: #616161;");

        HBox actions = new HBox(8);
        actions.setPrefWidth(100);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button btnEdit = new Button("✏");
        btnEdit.setStyle("-fx-background-color: #E3F2FD; -fx-text-fill: #1976D2; -fx-font-size: 13px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> showEmployeeDialog(emp, onRefresh));

        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; -fx-font-size: 13px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa nhân viên " + emp.getName() + "?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    EmployeeService empService = new EmployeeService();
                    empService.deleteEmployee(emp.getId());
                    onRefresh.run();
                }
            });
        });

        actions.getChildren().addAll(btnEdit, btnDelete);

        row.getChildren().addAll(colCode, colName, colPhone, colPosition, colSalary, colStartDate, actions);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #f1f3f9; -fx-border-width: 0 0 1 0;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: white; -fx-border-color: #f1f3f9; -fx-border-width: 0 0 1 0;"));

        return row;
    }

    private void showEmployeeDialog(Employee emp, Runnable onSaved) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(emp != null ? "Chỉnh Sửa Nhân Viên" : "Thêm Nhân Viên Mới");

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white;");

        Label title = new Label(emp != null ? "👥 Chỉnh Sửa Nhân Viên" : "👥 Thêm Nhân Viên Mới");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #212121;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(140);
        col1.setPrefWidth(140);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(300);
        col2.setPrefWidth(300);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        Label lblCode = new Label("Mã nhân viên *");
        lblCode.setStyle("-fx-font-weight: 500;");
        TextField txtCode = new TextField(emp != null ? emp.getEmployeeCode() : "");
        txtCode.setPromptText("Ví dụ: NV001");
        txtCode.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");

        Label lblName = new Label("Họ tên *");
        lblName.setStyle("-fx-font-weight: 500;");
        TextField txtName = new TextField(emp != null ? emp.getName() : "");
        txtName.setPromptText("Nhập họ tên...");
        txtName.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");

        Label lblPhone = new Label("Số điện thoại");
        lblPhone.setStyle("-fx-font-weight: 500;");
        TextField txtPhone = new TextField(emp != null ? emp.getPhone() : "");
        txtPhone.setPromptText("Nhập số điện thoại...");
        txtPhone.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");

        Label lblAddress = new Label("Địa chỉ");
        lblAddress.setStyle("-fx-font-weight: 500;");
        TextField txtAddress = new TextField(emp != null ? emp.getAddress() : "");
        txtAddress.setPromptText("Nhập địa chỉ...");
        txtAddress.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");

        Label lblDob = new Label("Ngày sinh");
        lblDob.setStyle("-fx-font-weight: 500;");
        DatePicker dpDob = new DatePicker();
        dpDob.setStyle("-fx-pref-width: 300px; -fx-font-size: 13px;");
        if (emp != null && emp.getDob() != null && !emp.getDob().isEmpty()) {
            try { dpDob.setValue(LocalDate.parse(emp.getDob())); } catch (Exception ex) {}
        }

        Label lblGender = new Label("Giới tính");
        lblGender.setStyle("-fx-font-weight: 500;");
        ComboBox<String> cbGender = new ComboBox<>();
        cbGender.getItems().addAll("Nam", "Nữ", "Khác");
        cbGender.setValue(emp != null && emp.getGender() != null ? emp.getGender() : "Nam");
        cbGender.setStyle("-fx-pref-width: 300px; -fx-background-color: #f5f5f5; -fx-background-radius: 6;");

        Label lblStartDate = new Label("Ngày vào làm");
        lblStartDate.setStyle("-fx-font-weight: 500;");
        DatePicker dpStartDate = new DatePicker();
        dpStartDate.setStyle("-fx-pref-width: 300px; -fx-font-size: 13px;");
        if (emp != null && emp.getStartDate() != null && !emp.getStartDate().isEmpty()) {
            try { dpStartDate.setValue(LocalDate.parse(emp.getStartDate())); } catch (Exception ex) {}
        } else {
            dpStartDate.setValue(LocalDate.now());
        }

        Label lblPosition = new Label("Chức vụ *");
        lblPosition.setStyle("-fx-font-weight: 500;");
        ComboBox<String> cbPosition = new ComboBox<>();
        cbPosition.getItems().addAll("Kỹ thuật điện", "Kỹ thuật viên", "Học viên", "Quản lý", "Khác");
        cbPosition.setValue(emp != null && emp.getPosition() != null ? emp.getPosition() : "Kỹ thuật viên");
        cbPosition.setEditable(true);
        cbPosition.setStyle("-fx-pref-width: 300px; -fx-background-color: #f5f5f5; -fx-background-radius: 6;");

        Label lblSalary = new Label("Lương cơ bản *");
        lblSalary.setStyle("-fx-font-weight: 500;");
        TextField txtSalary = new TextField(emp != null ? String.format("%.0f", emp.getBasicSalary()) : "0");
        txtSalary.setPromptText("Nhập mức lương cơ bản...");
        txtSalary.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-background-radius: 6;");

        grid.add(lblCode, 0, 0); grid.add(txtCode, 1, 0);
        grid.add(lblName, 0, 1); grid.add(txtName, 1, 1);
        grid.add(lblPhone, 0, 2); grid.add(txtPhone, 1, 2);
        grid.add(lblAddress, 0, 3); grid.add(txtAddress, 1, 3);
        grid.add(lblDob, 0, 4); grid.add(dpDob, 1, 4);
        grid.add(lblGender, 0, 5); grid.add(cbGender, 1, 5);
        grid.add(lblStartDate, 0, 6); grid.add(dpStartDate, 1, 6);
        grid.add(lblPosition, 0, 7); grid.add(cbPosition, 1, 7);
        grid.add(lblSalary, 0, 8); grid.add(txtSalary, 1, 8);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancel = new Button("Hủy");
        btnCancel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 8; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> dialogStage.close());

        Button btnSave = new Button("Lưu");
        btnSave.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 30px; -fx-background-radius: 8; -fx-cursor: hand;");
        btnSave.setOnAction(e -> {
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            String position = cbPosition.getValue() != null ? cbPosition.getValue().trim() : "";
            double salary = parseDoubleSafe(txtSalary.getText());

            if (code.isEmpty() || name.isEmpty() || position.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ Mã NV, Họ Tên và Chức Vụ!");
                alert.setHeaderText(null);
                alert.show();
                return;
            }

            EmployeeService empService = new EmployeeService();
            Employee saveEmp = (emp != null) ? emp : new Employee();
            saveEmp.setEmployeeCode(code);
            saveEmp.setName(name);
            saveEmp.setPhone(txtPhone.getText().trim());
            saveEmp.setAddress(txtAddress.getText().trim());
            saveEmp.setDob(dpDob.getValue() != null ? dpDob.getValue().toString() : "");
            saveEmp.setGender(cbGender.getValue());
            saveEmp.setStartDate(dpStartDate.getValue() != null ? dpStartDate.getValue().toString() : "");
            saveEmp.setPosition(position);
            saveEmp.setBasicSalary(salary);

            boolean success;
            if (emp != null) {
                success = empService.updateEmployee(saveEmp);
            } else {
                success = empService.addEmployee(saveEmp);
            }

            if (success) {
                onSaved.run();
                dialogStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Mã nhân viên đã tồn tại hoặc có lỗi xảy ra!");
                alert.setHeaderText(null);
                alert.show();
            }
        });

        buttons.getChildren().addAll(btnCancel, btnSave);
        root.getChildren().addAll(title, grid, buttons);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private double parseDoubleSafe(String str) {
        if (str == null || str.trim().isEmpty()) return 0;
        try {
            return Double.parseDouble(str.replaceAll("[^0-9.-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
