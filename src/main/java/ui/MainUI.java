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
import util.DatabaseManager;

public class MainUI extends Application {

    private BorderPane mainLayout;
    private StackPane contentArea;
    private VBox sidebar;

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

        btnNewInvoice.setOnAction(e -> showInvoiceManagement());
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
        VBox view = createModernView("🧾 Quản Lý Hóa Đơn", "Tạo và quản lý hóa đơn cho khách hàng");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    private void showServiceManagement() {
        VBox view = createModernView("🔧 Quản Lý Dịch Vụ", "Danh sách dịch vụ rửa xe với giá theo loại xe");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    private void showPackageManagement() {
        VBox view = createModernView("📦 Quản Lý Gói Dịch Vụ", "Các gói combo dịch vụ tiết kiệm");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    private void showProductManagement() {
        VBox view = createModernView("🛒 Quản Lý Sản Phẩm", "Sản phẩm bán kèm và quản lý tồn kho");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    private void showReport() {
        VBox view = createModernView("📈 Báo Cáo & Thống Kê", "Phân tích doanh thu và hiệu suất kinh doanh");
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
}
