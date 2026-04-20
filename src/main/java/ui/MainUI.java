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

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        VBox card1 = createCleanStatCard("Doanh Thu Hôm Nay", "Doanh Thu Hôm Nay", "0 đ", "#2196F3");
        VBox card2 = createCleanStatCard("Hóa Đơn", "Hóa Đơn", "0", "#2196F3");
        VBox card3 = createCleanStatCard("Xe Đang Rửa", "Xe Đang Rửa", "0", "#2196F3");
        VBox card4 = createCleanStatCard("Khách Hàng", "Khách Hàng", "0", "#2196F3");

        statsGrid.add(card1, 0, 0);
        statsGrid.add(card2, 1, 0);
        statsGrid.add(card3, 2, 0);
        statsGrid.add(card4, 3, 0);

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
        hTrangThai.setPrefWidth(150);
        hTrangThai.setMinWidth(150);
        hTrangThai.setMaxWidth(150);
        hTrangThai.setPadding(new Insets(0, 15, 0, 15));
        hTrangThai.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        hTrangThai.setAlignment(Pos.CENTER);
        
        Label hThaoTac = new Label("Thao Tác");
        hThaoTac.setPrefWidth(150);
        hThaoTac.setMinWidth(150);
        hThaoTac.setMaxWidth(150);
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
        tableRows.getChildren().clear();
        InvoiceService invoiceService = new InvoiceService();
        List<Invoice> invoices = invoiceService.getAllInvoices();
        
        if (invoices.isEmpty()) {
            Label emptyState = new Label("Chưa có hóa đơn nào");
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
        lblStatus.setPrefWidth(150);
        lblStatus.setMinWidth(150);
        lblStatus.setMaxWidth(150);
        lblStatus.setAlignment(Pos.CENTER);
        
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWidth(150);
        actions.setMinWidth(150);
        actions.setMaxWidth(150);
        actions.setPadding(new Insets(12, 15, 12, 15));
        
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
        
        actions.getChildren().addAll(btnView, btnDelete);
        
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
        tableRows.getChildren().clear();
        ServiceService serviceService = new ServiceService();
        List<Service> services = serviceService.getAllServices();
        
        if (services.isEmpty()) {
            Label emptyState = new Label("Chưa có dịch vụ nào");
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
        tableRows.getChildren().clear();
        PackageService packageService = new PackageService();
        List<Package> packages = packageService.getAllPackages();
        
        if (packages.isEmpty()) {
            Label emptyState = new Label("Chưa có gói dịch vụ nào");
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
        tableRows.getChildren().clear();
        ProductService productService = new ProductService();
        List<Product> products = productService.getAllProducts();
        
        if (products.isEmpty()) {
            Label emptyState = new Label("Chưa có sản phẩm nào");
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

    private void showReport() {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));

        Label title = new Label("📈 Báo Cáo & Thống Kê");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");

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
        
        Button btnExport = new Button("� Xuất Báo Cáo");
        btnExport.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        dateRange.getChildren().addAll(lblFrom, dateFrom, lblTo, dateTo, btnExport);

        // Stats overview
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        VBox stat1 = createReportStatCard("Tổng Doanh Thu", "0đ", "+0%");
        VBox stat2 = createReportStatCard("Tổng Hóa Đơn", "0", "+0%");
        VBox stat3 = createReportStatCard("Khách Hàng Mới", "0", "+0%");
        VBox stat4 = createReportStatCard("Dịch Vụ Phổ Biến", "N/A", "");

        statsGrid.add(stat1, 0, 0);
        statsGrid.add(stat2, 1, 0);
        statsGrid.add(stat3, 2, 0);
        statsGrid.add(stat4, 3, 0);

        // Chart placeholder
        VBox chartContainer = new VBox(15);
        chartContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 25;"
        );
        chartContainer.setPrefHeight(350);
        
        Label chartTitle = new Label("Biểu Đồ Doanh Thu Theo Tháng");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Label chartPlaceholder = new Label("📊 Biểu đồ sẽ hiển thị tại đây");
        chartPlaceholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #9e9e9e;");
        VBox.setVgrow(chartPlaceholder, Priority.ALWAYS);
        chartPlaceholder.setMaxHeight(Double.MAX_VALUE);
        chartPlaceholder.setAlignment(Pos.CENTER);
        
        chartContainer.getChildren().addAll(chartTitle, chartPlaceholder);

        view.getChildren().addAll(title, dateRange, statsGrid, chartContainer);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
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
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
}
