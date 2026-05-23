package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.PackageService;
import model.Package;

public class PackageForm {
    
    private Stage stage;
    private boolean isEdit;
    private int packageId;
    private Package existingPackage;

    private RadioButton rbActive;
    private RadioButton rbInactive;
    private TextField txtName;
    private TextArea txtDesc;
    private TextField txtPriceMini;
    private TextField txtPriceSedan;
    private TextField txtPriceCuv;
    private TextField txtPriceSuv;
    private TextField txtPricePickup;
    private Runnable onSave;
    
    public PackageForm() {
        this.isEdit = false;
    }
    
    public PackageForm(Runnable onSave) {
        this.isEdit = false;
        this.onSave = onSave;
    }
    
    public PackageForm(int id, Package pkg, Runnable onSave) {
        this.isEdit = true;
        this.packageId = id;
        this.existingPackage = pkg;
        this.onSave = onSave;
    }
    
    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(isEdit ? "Sửa Gói Dịch Vụ" : "Thêm Gói Dịch Vụ Mới");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");
        
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");
        
        // Header
        Label title = new Label(isEdit ? "✏ Sửa Gói Dịch Vụ" : "📦 Thêm Gói Dịch Vụ Mới");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-font-family: 'Times New Roman';");
        
        // Form Section
        VBox formSection = createFormSection();
        
        // Action Buttons
        HBox actionButtons = createActionButtons();
        
        mainContent.getChildren().addAll(title, formSection, actionButtons);
        scrollPane.setContent(mainContent);
        
        Scene scene = new Scene(scrollPane, 800, 950);
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
        stage.setScene(scene);
        stage.show();
    }
    
    private VBox createFormSection() {
        VBox section = new VBox(20);
        section.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 30;"
        );
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        // Package name
        Label lblName = new Label("Tên gói dịch vụ *");
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        txtName = new TextField();
        txtName.setPromptText("Nhập tên gói (VD: Gói VIP 1)");
        txtName.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Times New Roman';"
        );
        txtName.setPrefWidth(500);
        if (isEdit && existingPackage != null) {
            txtName.setText(existingPackage.getName());
        }
        
        // Description
        Label lblDesc = new Label("Mô tả gói *");
        lblDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        txtDesc = new TextArea();
        txtDesc.setPromptText("Nhập mô tả gói (VD: Rửa xe cơ bản + Hút bụi nội thất + Đánh bóng)");
        txtDesc.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Times New Roman';"
        );
        txtDesc.setPrefRowCount(3);
        txtDesc.setPrefWidth(500);
        if (isEdit && existingPackage != null) {
            txtDesc.setText(existingPackage.getDescription());
        }
        
        // Price section header
        Label lblPriceHeader = new Label("💰 Giá Gói Theo Loại Xe");
        lblPriceHeader.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976D2; -fx-font-weight: 700; -fx-padding: 10 0 5 0; -fx-font-family: 'Times New Roman';");
        
        // Create price fields for 5 vehicle types
        GridPane priceGrid = new GridPane();
        priceGrid.setHgap(15);
        priceGrid.setVgap(12);
        
        // Mini
        Label lblMini = new Label("Mini *");
        lblMini.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        txtPriceMini = new TextField();
        txtPriceMini.setPromptText("96000");
        txtPriceMini.setPrefWidth(140);
        txtPriceMini.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-padding: 10px 12px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Times New Roman';"
        );
        if (isEdit && existingPackage != null) {
            txtPriceMini.setText(String.format("%.0f", existingPackage.getPriceMini()));
        }
        
        // Sedan
        Label lblSedan = new Label("Sedan *");
        lblSedan.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        txtPriceSedan = new TextField();
        txtPriceSedan.setPromptText("120000");
        txtPriceSedan.setPrefWidth(140);
        txtPriceSedan.setStyle(
            "-fx-background-color: #E8F5E9;" +
            "-fx-padding: 10px 12px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Times New Roman';"
        );
        if (isEdit && existingPackage != null) {
            txtPriceSedan.setText(String.format("%.0f", existingPackage.getPriceSedan()));
        }
        
        // CUV
        Label lblCuv = new Label("CUV *");
        lblCuv.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        txtPriceCuv = new TextField();
        txtPriceCuv.setPromptText("180000");
        txtPriceCuv.setPrefWidth(140);
        txtPriceCuv.setStyle(
            "-fx-background-color: #FFF3E0;" +
            "-fx-padding: 10px 12px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Times New Roman';"
        );
        if (isEdit && existingPackage != null) {
            txtPriceCuv.setText(String.format("%.0f", existingPackage.getPriceCuv()));
        }
        
        // SUV
        Label lblSuv = new Label("SUV *");
        lblSuv.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        txtPriceSuv = new TextField();
        txtPriceSuv.setPromptText("240000");
        txtPriceSuv.setPrefWidth(140);
        txtPriceSuv.setStyle(
            "-fx-background-color: #FCE4EC;" +
            "-fx-padding: 10px 12px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Times New Roman';"
        );
        if (isEdit && existingPackage != null) {
            txtPriceSuv.setText(String.format("%.0f", existingPackage.getPriceSuv()));
        }
        
        // Pickup
        Label lblPickup = new Label("Pickup *");
        lblPickup.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        txtPricePickup = new TextField();
        txtPricePickup.setPromptText("264000");
        txtPricePickup.setPrefWidth(140);
        txtPricePickup.setStyle(
            "-fx-background-color: #F3E5F5;" +
            "-fx-padding: 10px 12px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Times New Roman';"
        );
        if (isEdit && existingPackage != null) {
            txtPricePickup.setText(String.format("%.0f", existingPackage.getPricePickup()));
        }
        
        // Add to price grid
        priceGrid.add(lblMini, 0, 0);
        priceGrid.add(txtPriceMini, 0, 1);
        priceGrid.add(lblSedan, 1, 0);
        priceGrid.add(txtPriceSedan, 1, 1);
        priceGrid.add(lblCuv, 2, 0);
        priceGrid.add(txtPriceCuv, 2, 1);
        priceGrid.add(lblSuv, 0, 2);
        priceGrid.add(txtPriceSuv, 0, 3);
        priceGrid.add(lblPickup, 1, 2);
        priceGrid.add(txtPricePickup, 1, 3);
        
        // Status
        Label lblStatus = new Label("Trạng thái");
        lblStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        
        HBox statusBox = new HBox(15);
        ToggleGroup statusGroup = new ToggleGroup();
        
        rbActive = new RadioButton("Đang bán");
        rbActive.setToggleGroup(statusGroup);
        rbActive.setSelected(true);
        rbActive.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-family: 'Times New Roman';");
        
        rbInactive = new RadioButton("Tạm dừng");
        rbInactive.setToggleGroup(statusGroup);
        rbInactive.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-family: 'Times New Roman';");
        
        statusBox.getChildren().addAll(rbActive, rbInactive);
        
        int row = 0;
        grid.add(lblName, 0, row++);
        grid.add(txtName, 0, row++);
        grid.add(lblDesc, 0, row++);
        grid.add(txtDesc, 0, row++);
        grid.add(lblPriceHeader, 0, row++);
        grid.add(priceGrid, 0, row++);
        grid.add(lblStatus, 0, row++);
        grid.add(statusBox, 0, row++);
        
        section.getChildren().add(grid);
        return section;
    }
    
    private HBox createActionButtons() {
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancel = new Button("Hủy");
        btnCancel.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #616161;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 30px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Times New Roman';"
        );
        btnCancel.setOnAction(e -> stage.close());
        
        Button btnSave = new Button(isEdit ? "Cập Nhật" : "Thêm Mới");
        btnSave.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 30px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Times New Roman';"
        );
        btnSave.setOnAction(e -> {
            // Validate
            if (txtName.getText().trim().isEmpty()) {
                showAlert("Cảnh báo", "Vui lòng nhập tên gói dịch vụ!", Alert.AlertType.WARNING);
                return;
            }
            
            if (txtDesc.getText().trim().isEmpty()) {
                showAlert("Cảnh báo", "Vui lòng nhập mô tả gói!", Alert.AlertType.WARNING);
                return;
            }
            

            
            if (txtPriceMini.getText().trim().isEmpty() || txtPriceSedan.getText().trim().isEmpty() ||
                txtPriceCuv.getText().trim().isEmpty() || txtPriceSuv.getText().trim().isEmpty() ||
                txtPricePickup.getText().trim().isEmpty()) {
                showAlert("Cảnh báo", "Vui lòng nhập giá cho tất cả loại xe!", Alert.AlertType.WARNING);
                return;
            }
            
            try {
                String name = txtName.getText().trim();
                String description = txtDesc.getText().trim();
                double priceMini = Double.parseDouble(txtPriceMini.getText().trim());
                double priceSedan = Double.parseDouble(txtPriceSedan.getText().trim());
                double priceCuv = Double.parseDouble(txtPriceCuv.getText().trim());
                double priceSuv = Double.parseDouble(txtPriceSuv.getText().trim());
                double pricePickup = Double.parseDouble(txtPricePickup.getText().trim());
                
                // Set avgSavings to 0 since we're not tracking individual services
                double avgSavings = 0;
                
                String status = rbActive.isSelected() ? "Đang bán" : "Tạm dừng";
                
                PackageService packageService = new PackageService();
                boolean success;
                
                if (isEdit) {
                    success = packageService.updatePackage(packageId, name, description, priceMini, priceSedan, priceCuv, priceSuv, pricePickup, avgSavings, status);
                } else {
                    success = packageService.addPackage(name, description, priceMini, priceSedan, priceCuv, priceSuv, pricePickup, avgSavings, status);
                }
                
                if (success) {
                    showAlert("Thành công", 
                             isEdit ? "Cập nhật gói dịch vụ thành công!" : "Thêm gói dịch vụ mới thành công!", 
                             Alert.AlertType.INFORMATION);
                    stage.close();
                    if (onSave != null) {
                        onSave.run();
                    }
                } else {
                    showAlert("Lỗi", "Không thể lưu gói dịch vụ!", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Giá phải là số hợp lệ!", Alert.AlertType.ERROR);
            }
        });
        
        buttons.getChildren().addAll(btnCancel, btnSave);
        return buttons;
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
