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
import model.Service;
import model.Package;
import model.Product;
import model.Invoice;
import java.util.List;

public class MainUI extends Application {

    private BorderPane mainLayout;
    private StackPane contentArea;
    private VBox sidebar;
    private VBox invoiceTableRows;

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
        
        stage.setScene(scene);
        stage.setTitle("🚗 MTProAuto - Quản Lý Chuyên Nghiệp");
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

        Circle logoCircle = new Circle(22);
        logoCircle.setFill(Color.web("#2196F3"));
        
        StackPane logoContainer = new StackPane();
        Label logoText = new Label("■");
        logoText.setStyle("-fx-font-size: 28px; -fx-text-fill: white;");
        logoContainer.getChildren().addAll(logoCircle, logoText);

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

        header.getChildren().addAll(logoContainer, titleBox, spacer, searchBox, userBox);
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

        Label menuLabel = new Label("MENU CHÍNH");
        menuLabel.setStyle(
            "-fx-text-fill: #9e9e9e;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 8px 12px 5px 12px;"
        );

        Button btnDashboard = createModernMenuButton("■", "Trang Chủ", "#2196F3", true);
        Button btnInvoice = createModernMenuButton("📄", "Hóa Đơn", "#2196F3", false);
        Button btnService = createModernMenuButton("⚙", "Dịch Vụ", "#2196F3", false);
        Button btnPackage = createModernMenuButton("📦", "Gói Dịch Vụ", "#2196F3", false);
        Button btnProduct = createModernMenuButton("🛒", "Sản Phẩm", "#2196F3", false);
        Button btnReport = createModernMenuButton("�", "Báo Cáo", "#2196F3", false);

        btnDashboard.setOnAction(e -> {
            resetMenuButtons();
            setActiveButton(btnDashboard);
            showDashboard();
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

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = createModernMenuButton("→", "Đăng Xuất", "#2196F3", false);
        btnLogout.setStyle(
            btnLogout.getStyle() + 
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1 0 0 0;" +
            "-fx-padding: 12px 12px 8px 12px;"
        );
        
        btnLogout.setOnAction(e -> handleLogout());

        sidebar.getChildren().addAll(
            menuLabel, btnDashboard, btnInvoice, btnService, 
            btnPackage, btnProduct, btnReport, spacer, btnLogout
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

    private void showDashboard() {
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

        dashboard.getChildren().addAll(title, statsGrid, quickTitle, quickActions);
        
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
        searchField.setPrefWidth(300);
        searchField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        
        // Filter buttons
        HBox filterButtons = new HBox(8);
        Button btnAll = createFilterButton("Tất cả", true);
        Button btnPaid = createFilterButton("Đã thanh toán", false);
        Button btnUnpaid = createFilterButton("Chưa thanh toán", false);
        
        // Store current filter
        final String[] currentStatus = {""};
        
        // Add search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            refreshInvoiceTableWithFilter(invoiceTableRows, newVal, currentStatus[0]);
        });
        
        // Add filter button actions
        btnAll.setOnAction(e -> {
            currentStatus[0] = "";
            updateFilterButtonStyles(btnAll, btnPaid, btnUnpaid);
            refreshInvoiceTableWithFilter(invoiceTableRows, searchField.getText(), "");
        });
        
        btnPaid.setOnAction(e -> {
            currentStatus[0] = "paid";
            updateFilterButtonStyles(btnPaid, btnAll, btnUnpaid);
            refreshInvoiceTableWithFilter(invoiceTableRows, searchField.getText(), "paid");
        });
        
        btnUnpaid.setOnAction(e -> {
            currentStatus[0] = "nhap";
            updateFilterButtonStyles(btnUnpaid, btnAll, btnPaid);
            refreshInvoiceTableWithFilter(invoiceTableRows, searchField.getText(), "nhap");
        });
        
        filterButtons.getChildren().addAll(btnAll, btnPaid, btnUnpaid);
        
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Chọn ngày");
        datePicker.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 14px;"
        );
        
        filterBar.getChildren().addAll(searchField, filterButtons, datePicker);

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
        hMaHD.setPrefWidth(100);
        hMaHD.setMinWidth(100);
        hMaHD.setMaxWidth(100);
        hMaHD.setPadding(new Insets(0, 15, 0, 15));
        hMaHD.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hMaHD.setAlignment(Pos.CENTER_LEFT);
        
        Label hKhachHang = new Label("Khách Hàng");
        hKhachHang.setPrefWidth(200);
        hKhachHang.setMinWidth(200);
        hKhachHang.setMaxWidth(200);
        hKhachHang.setPadding(new Insets(0, 15, 0, 15));
        hKhachHang.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hKhachHang.setAlignment(Pos.CENTER_LEFT);
        
        Label hDichVu = new Label("Dịch Vụ");
        hDichVu.setPrefWidth(200);
        hDichVu.setMinWidth(200);
        hDichVu.setMaxWidth(200);
        hDichVu.setPadding(new Insets(0, 15, 0, 15));
        hDichVu.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hDichVu.setAlignment(Pos.CENTER_LEFT);
        
        Label hTongTien = new Label("Tổng Tiền");
        hTongTien.setPrefWidth(150);
        hTongTien.setMinWidth(150);
        hTongTien.setMaxWidth(150);
        hTongTien.setPadding(new Insets(0, 15, 0, 15));
        hTongTien.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hTongTien.setAlignment(Pos.CENTER_RIGHT);
        
        Label hTrangThai = new Label("Trạng Thái");
        hTrangThai.setPrefWidth(120);
        hTrangThai.setMinWidth(120);
        hTrangThai.setMaxWidth(120);
        hTrangThai.setPadding(new Insets(0, 15, 0, 15));
        hTrangThai.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hTrangThai.setAlignment(Pos.CENTER);
        
        Label hThaoTac = new Label("Thao Tác");
        hThaoTac.setPrefWidth(180);
        hThaoTac.setMinWidth(180);
        hThaoTac.setMaxWidth(180);
        hThaoTac.setPadding(new Insets(0, 15, 0, 15));
        hThaoTac.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hThaoTac.setAlignment(Pos.CENTER);
        
        tableHeader.getChildren().addAll(hMaHD, hKhachHang, hDichVu, hTongTien, hTrangThai, hThaoTac);
        
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
        refreshInvoiceTableWithFilter(tableRows, "", "");
    }
    
    private void refreshInvoiceTableWithFilter(VBox tableRows, String searchText, String status) {
        tableRows.getChildren().clear();
        InvoiceService invoiceService = new InvoiceService();
        List<Invoice> invoices = invoiceService.getAllInvoices();
        
        // Filter by status
        if (status != null && !status.trim().isEmpty()) {
            invoices = invoices.stream()
                .filter(i -> i.getStatus().equals(status))
                .collect(java.util.stream.Collectors.toList());
        }
        
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
            Label emptyState = new Label((searchText != null && !searchText.trim().isEmpty()) || 
                                        (status != null && !status.trim().isEmpty()) ? 
                "Không tìm thấy hóa đơn nào" : "Chưa có hóa đơn nào");
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
                    tableRows
                ));
            }
        }
    }
    
    private HBox createInvoiceRow(int id, String customerName, String service, String totalAmount, String status, VBox tableRows) {
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
        
        Label lblId = new Label(String.valueOf(id));
        lblId.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121; -fx-font-weight: 500;");
        lblId.setPrefWidth(100);
        lblId.setMinWidth(100);
        lblId.setMaxWidth(100);
        lblId.setPadding(new Insets(12, 15, 12, 15));
        lblId.setAlignment(Pos.CENTER_LEFT);
        
        Label lblCustomer = new Label(customerName);
        lblCustomer.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121;");
        lblCustomer.setPrefWidth(200);
        lblCustomer.setMinWidth(200);
        lblCustomer.setMaxWidth(200);
        lblCustomer.setPadding(new Insets(12, 15, 12, 15));
        lblCustomer.setAlignment(Pos.CENTER_LEFT);
        
        Label lblService = new Label(service);
        lblService.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        lblService.setPrefWidth(200);
        lblService.setMinWidth(200);
        lblService.setMaxWidth(200);
        lblService.setPadding(new Insets(12, 15, 12, 15));
        lblService.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTotal = new Label(totalAmount);
        lblTotal.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        lblTotal.setPrefWidth(150);
        lblTotal.setMinWidth(150);
        lblTotal.setMaxWidth(150);
        lblTotal.setPadding(new Insets(12, 15, 12, 15));
        lblTotal.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblStatus = new Label(status.equals("nhap") ? "Chưa thanh toán" : "Đã thanh toán");
        lblStatus.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + (status.equals("nhap") ? "#f44336" : "#4CAF50") + ";" +
            "-fx-background-color: " + (status.equals("nhap") ? "#FFEBEE" : "#E8F5E9") + ";" +
            "-fx-padding: 4px 10px;" +
            "-fx-background-radius: 6;"
        );
        lblStatus.setPrefWidth(120);
        lblStatus.setMinWidth(120);
        lblStatus.setMaxWidth(120);
        lblStatus.setAlignment(Pos.CENTER);
        
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWidth(180);
        actions.setMinWidth(180);
        actions.setMaxWidth(180);
        actions.setPadding(new Insets(12, 15, 12, 15));
        
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
            showDeleteConfirmation("hóa đơn", "HĐ #" + id, () -> {
                InvoiceService invoiceService = new InvoiceService();
                if (invoiceService.deleteInvoice(id)) {
                    refreshInvoiceTable(tableRows);
                } else {
                    showErrorAlert("Lỗi", "Không thể xóa hóa đơn!");
                }
            });
        });
        
        actions.getChildren().addAll(btnPDF, btnView, btnDelete);
        
        row.getChildren().addAll(lblId, lblCustomer, lblService, lblTotal, lblStatus, actions);
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
        hName.setPrefWidth(200);
        hName.setMinWidth(200);
        hName.setPadding(new Insets(0, 15, 0, 15));
        hName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hName.setAlignment(Pos.CENTER_LEFT);
        
        Label hDesc = new Label("Mô Tả");
        hDesc.setPrefWidth(450);
        hDesc.setMinWidth(100);
        hDesc.setPadding(new Insets(0, 15, 0, 15));
        hDesc.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hDesc.setAlignment(Pos.CENTER_LEFT);
        
        Label hPriceSmall = new Label("Giá (Xe Nhỏ)");
        hPriceSmall.setPrefWidth(150);
        hPriceSmall.setMinWidth(150);
        hPriceSmall.setPadding(new Insets(0, 15, 0, 15));
        hPriceSmall.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hPriceSmall.setAlignment(Pos.CENTER_RIGHT);
        
        Label hPriceLarge = new Label("Giá (Xe Lớn)");
        hPriceLarge.setPrefWidth(150);
        hPriceLarge.setMinWidth(150);
        hPriceLarge.setPadding(new Insets(0, 15, 0, 15));
        hPriceLarge.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hPriceLarge.setAlignment(Pos.CENTER_RIGHT);
        
        Label hAction = new Label("Thao Tác");
        hAction.setPrefWidth(150);
        hAction.setMinWidth(150);
        hAction.setPadding(new Insets(0, 15, 0, 15));
        hAction.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hAction.setAlignment(Pos.CENTER);
        
        tableHeader.getChildren().addAll(hName, hDesc, hPriceSmall, hPriceLarge, hAction);
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
                tableRows.getChildren().add(createServiceRow(
                    service.getId(),
                    service.getName(),
                    service.getDescription(),
                    String.format("%.0f đ", service.getPriceSmall()),
                    String.format("%.0f đ", service.getPriceLarge()),
                    tableRows
                ));
            }
        }
    }

    private HBox createServiceRow(int id, String name, String description, String priceSmall, String priceLarge, VBox tableRows) {
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
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121; -fx-font-weight: 500;");
        lblName.setPrefWidth(200);
        lblName.setMinWidth(200);
        lblName.setPadding(new Insets(12, 15, 12, 15));
        lblName.setAlignment(Pos.CENTER_LEFT);
        
        // Description cell
        Label lblDesc = new Label(description);
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        lblDesc.setPrefWidth(450);
        lblDesc.setMinWidth(100);
        lblDesc.setPadding(new Insets(12, 15, 12, 15));
        lblDesc.setAlignment(Pos.CENTER_LEFT);
        lblDesc.setWrapText(true);
        
        // Price small cell
        Label lblPriceSmall = new Label(priceSmall);
        lblPriceSmall.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        lblPriceSmall.setPrefWidth(150);
        lblPriceSmall.setMinWidth(150);
        lblPriceSmall.setPadding(new Insets(12, 15, 12, 15));
        lblPriceSmall.setAlignment(Pos.CENTER_RIGHT);
        
        // Price large cell
        Label lblPriceLarge = new Label(priceLarge);
        lblPriceLarge.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        lblPriceLarge.setPrefWidth(150);
        lblPriceLarge.setMinWidth(150);
        lblPriceLarge.setPadding(new Insets(12, 15, 12, 15));
        lblPriceLarge.setAlignment(Pos.CENTER_RIGHT);
        
        // Actions cell
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWidth(150);
        actions.setMinWidth(150);
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
            ServiceForm form = new ServiceForm(id, name, description, priceSmall, priceLarge, () -> refreshServiceTable(tableRows));
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
            showDeleteConfirmation("dịch vụ", name, () -> {
                ServiceService serviceService = new ServiceService();
                if (serviceService.deleteService(id)) {
                    refreshServiceTable(tableRows);
                } else {
                    showErrorAlert("Lỗi", "Không thể xóa dịch vụ!");
                }
            });
        });
        
        actions.getChildren().addAll(btnEdit, btnDelete);
        
        row.getChildren().addAll(lblName, lblDesc, lblPriceSmall, lblPriceLarge, actions);
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
        hName.setPrefWidth(180);
        hName.setMinWidth(180);
        hName.setPadding(new Insets(0, 15, 0, 15));
        hName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hName.setAlignment(Pos.CENTER_LEFT);
        
        Label hDesc = new Label("Mô Tả");
        hDesc.setPrefWidth(420);
        hDesc.setMinWidth(100);
        hDesc.setPadding(new Insets(0, 15, 0, 15));
        hDesc.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hDesc.setAlignment(Pos.CENTER_LEFT);
        
        Label hPrice = new Label("Giá Gói");
        hPrice.setPrefWidth(120);
        hPrice.setMinWidth(120);
        hPrice.setPadding(new Insets(0, 15, 0, 15));
        hPrice.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hPrice.setAlignment(Pos.CENTER_RIGHT);
        
        Label hSavings = new Label("Tiết Kiệm");
        hSavings.setPrefWidth(120);
        hSavings.setMinWidth(120);
        hSavings.setPadding(new Insets(0, 15, 0, 15));
        hSavings.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hSavings.setAlignment(Pos.CENTER_RIGHT);
        
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
        
        tableHeader.getChildren().addAll(hName, hDesc, hPrice, hSavings, hStatus, hAction);
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
                tableRows.getChildren().add(createPackageRow(
                    pkg.getId(),
                    pkg.getName(),
                    pkg.getDescription(),
                    String.format("%.0f đ", pkg.getPrice()),
                    String.format("%.0f đ", pkg.getSavings()),
                    pkg.getStatus(),
                    tableRows
                ));
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
        searchField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        
        // Category filter buttons
        HBox categoryButtons = new HBox(8);
        Button btnAllCat = createFilterButton("Tất cả", true);
        Button btnWater = createFilterButton("Nước rửa xe", false);
        Button btnSolution = createFilterButton("Dung dịch", false);
        Button btnAccessory = createFilterButton("Phụ kiện", false);
        
        // Store current filter
        final String[] currentCategory = {""};
        
        // Add search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            refreshProductTableWithFilter(tableRows, newVal, currentCategory[0]);
        });
        
        // Add filter button actions
        btnAllCat.setOnAction(e -> {
            currentCategory[0] = "";
            updateFilterButtonStyles(btnAllCat, btnWater, btnSolution, btnAccessory);
            refreshProductTableWithFilter(tableRows, searchField.getText(), "");
        });
        
        btnWater.setOnAction(e -> {
            currentCategory[0] = "Nước rửa xe";
            updateFilterButtonStyles(btnWater, btnAllCat, btnSolution, btnAccessory);
            refreshProductTableWithFilter(tableRows, searchField.getText(), "Nước rửa xe");
        });
        
        btnSolution.setOnAction(e -> {
            currentCategory[0] = "Dung dịch";
            updateFilterButtonStyles(btnSolution, btnAllCat, btnWater, btnAccessory);
            refreshProductTableWithFilter(tableRows, searchField.getText(), "Dung dịch");
        });
        
        btnAccessory.setOnAction(e -> {
            currentCategory[0] = "Phụ kiện";
            updateFilterButtonStyles(btnAccessory, btnAllCat, btnWater, btnSolution);
            refreshProductTableWithFilter(tableRows, searchField.getText(), "Phụ kiện");
        });
        
        categoryButtons.getChildren().addAll(btnAllCat, btnWater, btnSolution, btnAccessory);
        
        searchBar.getChildren().addAll(searchField, categoryButtons);

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
        refreshProductTableWithFilter(tableRows, "", "");
    }
    
    private void refreshProductTableWithFilter(VBox tableRows, String searchText, String category) {
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
        
        if (products.isEmpty()) {
            Label emptyState = new Label((searchText != null && !searchText.trim().isEmpty()) || 
                                        (category != null && !category.trim().isEmpty()) ? 
                "Không tìm thấy sản phẩm nào" : "Chưa có sản phẩm nào");
            emptyState.setStyle("-fx-font-size: 14px; -fx-text-fill: #9e9e9e; -fx-padding: 40px;");
            tableRows.getChildren().add(emptyState);
        } else {
            for (Product product : products) {
                tableRows.getChildren().add(createProductRow(
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    String.format("%.0f đ", product.getPrice()),
                    String.valueOf(product.getStock()),
                    product.getStatus(),
                    tableRows
                ));
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
        
        Label lblFrom = new Label("Từ ngày:");
        lblFrom.setStyle("-fx-font-size: 14px; -fx-text-fill: #616161;");
        DatePicker dateFrom = new DatePicker();
        
        Label lblTo = new Label("Đến ngày:");
        lblTo.setStyle("-fx-font-size: 14px; -fx-text-fill: #616161;");
        DatePicker dateTo = new DatePicker();

        // Stats overview - declare first for use in lambda
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
        
        // Filter button action
        btnFilter.setOnAction(e -> {
            List<Invoice> filtered = ReportHelper.filterInvoicesByDateRange(allInvoices, dateFrom.getValue(), dateTo.getValue());
            updateReportStats(filtered, statsGrid);
        });
        
        // Export button action
        btnExport.setOnAction(e -> {
            List<Invoice> filtered = ReportHelper.filterInvoicesByDateRange(allInvoices, dateFrom.getValue(), dateTo.getValue());
            ReportHelper.exportReportToPDF(filtered, dateFrom.getValue(), dateTo.getValue(), mainLayout.getScene().getWindow());
        });
        
        dateRange.getChildren().clear();
        dateRange.getChildren().addAll(lblFrom, dateFrom, lblTo, dateTo, btnFilter, btnExport);

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


    private HBox createProductRow(int id, String name, String category, String price, String stock, String status, VBox tableRows) {
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
        lblStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121;");
        lblStock.setPrefWidth(100);
        lblStock.setMinWidth(100);
        lblStock.setMaxWidth(100);
        lblStock.setPadding(new Insets(12, 15, 12, 15));
        lblStock.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblStatus = new Label(status);
        lblStatus.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #4CAF50;" +
            "-fx-background-color: #E8F5E9;" +
            "-fx-padding: 4px 10px;" +
            "-fx-background-radius: 6;"
        );
        lblStatus.setPrefWidth(100);
        lblStatus.setMinWidth(100);
        lblStatus.setMaxWidth(100);
        lblStatus.setAlignment(Pos.CENTER);
        
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
            ProductForm form = new ProductForm(id, name, category, price, stock, () -> refreshProductTable(tableRows));
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

    private HBox createPackageRow(int id, String name, String description, String price, String savings, String status, VBox tableRows) {
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
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #212121; -fx-font-weight: 600;");
        lblName.setPrefWidth(180);
        lblName.setMinWidth(180);
        lblName.setPadding(new Insets(12, 15, 12, 15));
        lblName.setAlignment(Pos.CENTER_LEFT);
        
        Label lblDesc = new Label(description);
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        lblDesc.setPrefWidth(420);
        lblDesc.setMinWidth(100);
        lblDesc.setPadding(new Insets(12, 15, 12, 15));
        lblDesc.setAlignment(Pos.CENTER_LEFT);
        
        Label lblPrice = new Label(price);
        lblPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        lblPrice.setPrefWidth(150);
        lblPrice.setMinWidth(150);
        lblPrice.setMaxWidth(150);
        lblPrice.setPadding(new Insets(12, 15, 12, 15));
        lblPrice.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblSavings = new Label(savings);
        lblSavings.setStyle("-fx-font-size: 13px; -fx-text-fill: #4CAF50; -fx-font-weight: 600;");
        lblSavings.setPrefWidth(120);
        lblSavings.setMinWidth(120);
        lblSavings.setMaxWidth(120);
        lblSavings.setPadding(new Insets(12, 15, 12, 15));
        lblSavings.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblStatus = new Label(status);
        lblStatus.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #2196F3;" +
            "-fx-background-color: #E3F2FD;" +
            "-fx-padding: 4px 10px;" +
            "-fx-background-radius: 6;"
        );
        lblStatus.setPrefWidth(100);
        lblStatus.setMinWidth(100);
        lblStatus.setMaxWidth(100);
        lblStatus.setAlignment(Pos.CENTER);
        
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
            PackageForm form = new PackageForm(id, name, description, price, savings, () -> refreshPackageTable(tableRows));
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
            showDeleteConfirmation("gói dịch vụ", name, () -> {
                PackageService packageService = new PackageService();
                if (packageService.deletePackage(id)) {
                    refreshPackageTable(tableRows);
                } else {
                    showErrorAlert("Lỗi", "Không thể xóa gói dịch vụ!");
                }
            });
        });
        
        actions.getChildren().addAll(btnEdit, btnDelete);
        
        row.getChildren().addAll(lblName, lblDesc, lblPrice, lblSavings, lblStatus, actions);
        HBox.setHgrow(lblDesc, Priority.ALWAYS);
        return row;
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
        dialogStage.setTitle("Chi Tiết Hóa Đơn #" + invoiceId);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");
        
        // Header
        Label title = new Label("📋 Chi Tiết Hóa Đơn #" + invoiceId);
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
            VBox itemsList = new VBox(8);
            for (model.InvoiceItem item : items) {
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
                lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
                lblName.setPrefWidth(250);
                
                Label lblQty = new Label("x" + item.getQuantity());
                lblQty.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
                lblQty.setPrefWidth(40);
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                Label lblPrice = new Label(String.format("%,.0f đ", item.getTotalPrice()));
                lblPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
                
                itemRow.getChildren().addAll(lblIcon, lblName, lblQty, spacer, lblPrice);
                itemsList.getChildren().add(itemRow);
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
        
        Label lblTotal = new Label(String.format("Tổng tiền: %,.0f đ", invoice.getTotalAmount()));
        lblTotal.setStyle("-fx-font-size: 18px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        
        Label lblDiscount = new Label(String.format("Giảm giá: %,.0f đ", invoice.getDiscount()));
        lblDiscount.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        Label lblFinal = new Label(String.format("Thành tiền: %,.0f đ", invoice.getTotalAmount() - invoice.getDiscount()));
        lblFinal.setStyle("-fx-font-size: 20px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        
        paymentSection.getChildren().addAll(paymentTitle, lblTotal, lblDiscount, lblFinal);
        
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
        
        // Notes section
        if (invoice.getNotes() != null && !invoice.getNotes().trim().isEmpty()) {
            VBox notesSection = new VBox(10);
            notesSection.setStyle(
                "-fx-background-color: #FFF3E0;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 20;"
            );
            
            Label notesTitle = new Label("Ghi Chú");
            notesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #212121;");
            
            Label lblNotes = new Label(invoice.getNotes());
            lblNotes.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
            lblNotes.setWrapText(true);
            
            notesSection.getChildren().addAll(notesTitle, lblNotes);
            content.getChildren().add(notesSection);
        }
        
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
            
            // Update status in database
            if (invoiceService.updateInvoiceStatus(invoiceId, newStatus)) {
                showSuccessAlert("Thành công", "Cập nhật trạng thái thành công!");
                refreshInvoiceTable(tableRows);
                dialogStage.close();
            } else {
                showErrorAlert("Lỗi", "Không thể cập nhật trạng thái!");
            }
        });
        
        buttonBox.getChildren().addAll(btnClose, btnUpdate);
        
        content.getChildren().addAll(title, customerSection, itemsSection, paymentSection, statusSection, buttonBox);
        
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
            fileChooser.setTitle("Lưu Hóa Đơn PDF");
            fileChooser.setInitialFileName("HoaDon_" + invoice.getId() + ".pdf");
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
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);
            
            // Sử dụng font hỗ trợ tiếng Việt từ PDFFontHelper
            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);
            
            // In thông tin font để debug (có thể xóa sau)
            System.out.println("=== XUẤT HÓA ĐƠN PDF ===");
            if (util.PDFFontHelper.testVietnameseSupport(font)) {
                System.out.println("✓ Font hỗ trợ tiếng Việt");
            } else {
                System.out.println("⚠ Font có thể không hỗ trợ đầy đủ tiếng Việt");
            }
            
            // Title với font đậm
            com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph("HÓA ĐƠN DỊCH VỤ")
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
            document.add(title);
            
            // Company info với font đậm
            com.itextpdf.layout.element.Paragraph companyInfo = new com.itextpdf.layout.element.Paragraph("MTProAuto - Hệ Thống Quản Lý")
                .setFont(boldFont)
                .setFontSize(12)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
            document.add(companyInfo);
            
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            
            // Invoice number and date
            document.add(new com.itextpdf.layout.element.Paragraph("Mã hóa đơn: #" + invoice.getId())
                .setFont(font)
                .setFontSize(12));
            document.add(new com.itextpdf.layout.element.Paragraph("Ngày tạo: " + 
                (invoice.getCreatedAt() != null ? invoice.getCreatedAt() : "N/A"))
                .setFont(font)
                .setFontSize(12));
            
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            
            // Customer information với font đậm cho tiêu đề
            document.add(new com.itextpdf.layout.element.Paragraph("THÔNG TIN KHÁCH HÀNG")
                .setFont(boldFont)
                .setFontSize(14));
            document.add(new com.itextpdf.layout.element.Paragraph("Tên khách hàng: " + invoice.getCustomerName())
                .setFont(font)
                .setFontSize(12));
            document.add(new com.itextpdf.layout.element.Paragraph("Số điện thoại: " + 
                (invoice.getPhone() != null ? invoice.getPhone() : "N/A"))
                .setFont(font)
                .setFontSize(12));
            document.add(new com.itextpdf.layout.element.Paragraph("Biển số xe: " + 
                (invoice.getLicensePlate() != null ? invoice.getLicensePlate() : "N/A"))
                .setFont(font)
                .setFontSize(12));
            document.add(new com.itextpdf.layout.element.Paragraph("Loại xe: " + 
                (invoice.getVehicleType() != null ? invoice.getVehicleType() : "N/A"))
                .setFont(font)
                .setFontSize(12));
            
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            
            // Invoice items với font đậm cho tiêu đề
            if (!items.isEmpty()) {
                document.add(new com.itextpdf.layout.element.Paragraph("CHI TIẾT DỊCH VỤ & SẢN PHẨM")
                    .setFont(boldFont)
                    .setFontSize(14));
                
                // Create items table
                com.itextpdf.layout.element.Table itemsTable = new com.itextpdf.layout.element.Table(4);
                itemsTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
                
                // Header row với font đậm
                itemsTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Tên").setFont(boldFont)));
                itemsTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Loại").setFont(boldFont)));
                itemsTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("SL").setFont(boldFont)));
                itemsTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Thành tiền").setFont(boldFont)));
                
                // Data rows
                for (model.InvoiceItem item : items) {
                    String itemTypeText = "";
                    if (item.getItemType().equals("service")) {
                        itemTypeText = "Dịch vụ";
                    } else if (item.getItemType().equals("package")) {
                        itemTypeText = "Gói";
                    } else if (item.getItemType().equals("product")) {
                        itemTypeText = "Sản phẩm";
                    }
                    
                    itemsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                        new com.itextpdf.layout.element.Paragraph(item.getItemName()).setFont(font)));
                    itemsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                        new com.itextpdf.layout.element.Paragraph(itemTypeText).setFont(font)));
                    itemsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                        new com.itextpdf.layout.element.Paragraph(String.valueOf(item.getQuantity())).setFont(font)));
                    itemsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                        new com.itextpdf.layout.element.Paragraph(String.format("%,.0f VNĐ", item.getTotalPrice())).setFont(font)));
                }
                
                document.add(itemsTable);
                document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            }
            
            // Payment details với font đậm cho tiêu đề
            document.add(new com.itextpdf.layout.element.Paragraph("TỔNG KẾT THANH TOÁN")
                .setFont(boldFont)
                .setFontSize(14));
            
            // Create table
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(2);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Add rows
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Tổng tiền:").setFont(font)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(String.format("%,.0f VNĐ", invoice.getTotalAmount())).setFont(font)));
            
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Giảm giá:").setFont(font)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(String.format("%,.0f VNĐ", invoice.getDiscount())).setFont(font)));
            
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Thành tiền:").setFont(boldFont)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(String.format("%,.0f VNĐ", 
                    invoice.getTotalAmount() - invoice.getDiscount())).setFont(boldFont)));
            
            document.add(table);
            
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            
            // Status với font đậm
            String statusText = invoice.getStatus().equals("paid") ? "Đã thanh toán" : "Chưa thanh toán";
            document.add(new com.itextpdf.layout.element.Paragraph("Trạng thái: " + statusText)
                .setFont(boldFont)
                .setFontSize(12));
            
            // Notes
            if (invoice.getNotes() != null && !invoice.getNotes().trim().isEmpty()) {
                document.add(new com.itextpdf.layout.element.Paragraph("\n"));
                document.add(new com.itextpdf.layout.element.Paragraph("Ghi chú: " + invoice.getNotes())
                    .setFont(font)
                    .setFontSize(12));
            }
            
            // Footer with QR code and signature
            document.add(new com.itextpdf.layout.element.Paragraph("\n\n"));
            
            // Create table for footer (2 columns)
            com.itextpdf.layout.element.Table footerTable = new com.itextpdf.layout.element.Table(2);
            footerTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Left column - QR Code and bank info
            com.itextpdf.layout.element.Cell leftCell = new com.itextpdf.layout.element.Cell();
            leftCell.setBorder(null);
            
            try {
                // Generate QR code for VietQR
                String qrContent = "TRAN THANH DAO\n0362625218\nMB Bank";
                com.google.zxing.BarcodeFormat format = com.google.zxing.BarcodeFormat.QR_CODE;
                com.google.zxing.qrcode.QRCodeWriter qrWriter = new com.google.zxing.qrcode.QRCodeWriter();
                com.google.zxing.common.BitMatrix bitMatrix = qrWriter.encode(qrContent, format, 150, 150);
                
                // Convert BitMatrix to BufferedImage
                java.awt.image.BufferedImage qrImage = new java.awt.image.BufferedImage(150, 150, java.awt.image.BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < 150; x++) {
                    for (int y = 0; y < 150; y++) {
                        qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                    }
                }
                
                // Convert to iText Image
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                javax.imageio.ImageIO.write(qrImage, "png", baos);
                com.itextpdf.io.image.ImageData imageData = com.itextpdf.io.image.ImageDataFactory.create(baos.toByteArray());
                com.itextpdf.layout.element.Image qrImg = new com.itextpdf.layout.element.Image(imageData);
                qrImg.setWidth(100);
                qrImg.setHeight(100);
                
                leftCell.add(qrImg);
                leftCell.add(new com.itextpdf.layout.element.Paragraph("\nThông tin chuyển khoản:")
                    .setFont(boldFont).setFontSize(10));
                leftCell.add(new com.itextpdf.layout.element.Paragraph("Tên: TRẦN THÀNH ĐẠO")
                    .setFont(font).setFontSize(9));
                leftCell.add(new com.itextpdf.layout.element.Paragraph("STK: 0362625218")
                    .setFont(font).setFontSize(9));
                leftCell.add(new com.itextpdf.layout.element.Paragraph("Ngân hàng: MB Bank")
                    .setFont(font).setFontSize(9));
            } catch (Exception e) {
                leftCell.add(new com.itextpdf.layout.element.Paragraph("Thông tin thanh toán:\nTRẦN THÀNH ĐẠO\n0362625218\nMB Bank")
                    .setFont(font).setFontSize(10));
            }
            
            // Right column - Date and creator
            com.itextpdf.layout.element.Cell rightCell = new com.itextpdf.layout.element.Cell();
            rightCell.setBorder(null);
            rightCell.setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT);
            
            java.time.LocalDate today = java.time.LocalDate.now();
            rightCell.add(new com.itextpdf.layout.element.Paragraph(
                String.format("Ngày %d tháng %d năm %d", today.getDayOfMonth(), today.getMonthValue(), today.getYear()))
                .setFont(font).setFontSize(11));
            rightCell.add(new com.itextpdf.layout.element.Paragraph("\n\n"));
            rightCell.add(new com.itextpdf.layout.element.Paragraph("Người tạo hóa đơn")
                .setFont(boldFont).setFontSize(10));
            rightCell.add(new com.itextpdf.layout.element.Paragraph("\n\n\n"));
            rightCell.add(new com.itextpdf.layout.element.Paragraph("(Ký và ghi rõ họ tên)")
                .setFont(font).setFontSize(9));
            
            footerTable.addCell(leftCell);
            footerTable.addCell(rightCell);
            document.add(footerTable);
            
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
}
