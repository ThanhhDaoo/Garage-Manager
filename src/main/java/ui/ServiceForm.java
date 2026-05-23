package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Service;
import service.ServiceService;

public class ServiceForm {
    
    private Stage stage;
    private boolean isEdit;
    private int serviceId;
    private Service existingService;
    private Runnable onSave;
    
    // Form fields
    private TextField txtName;
    private TextArea txtDesc;
    private TextField txtPriceMini;
    private TextField txtPriceSedan;
    private TextField txtPriceCuv;
    private TextField txtPriceSuv;
    private TextField txtPricePickup;
    
    public ServiceForm() {
        this.isEdit = false;
    }
    
    public ServiceForm(Runnable onSave) {
        this.isEdit = false;
        this.onSave = onSave;
    }
    
    public ServiceForm(int serviceId, Service service, Runnable onSave) {
        this.isEdit = true;
        this.serviceId = serviceId;
        this.existingService = service;
        this.onSave = onSave;
    }
    
    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(isEdit ? "Sửa Dịch Vụ" : "Thêm Dịch Vụ Mới");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");
        
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");
        
        // Header
        Label title = new Label(isEdit ? "✏ Sửa Dịch Vụ" : "➕ Thêm Dịch Vụ Mới");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        // Form Section
        VBox formSection = createFormSection();
        
        // Action Buttons
        HBox actionButtons = createActionButtons();
        
        mainContent.getChildren().addAll(title, formSection, actionButtons);
        scrollPane.setContent(mainContent);
        
        Scene scene = new Scene(scrollPane, 750, 800);
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
        
        // Service name
        Label lblName = new Label("Tên dịch vụ *");
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtName = new TextField();
        txtName.setPromptText("Nhập tên dịch vụ");
        txtName.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtName.setPrefWidth(400);
        if (isEdit && existingService != null) {
            txtName.setText(existingService.getName());
        }
        
        // Description
        Label lblDesc = new Label("Mô tả");
        lblDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtDesc = new TextArea();
        txtDesc.setPromptText("Nhập mô tả dịch vụ");
        txtDesc.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtDesc.setPrefRowCount(3);
        txtDesc.setPrefWidth(400);
        if (isEdit && existingService != null) {
            txtDesc.setText(existingService.getDescription());
        }
        
        // Price section header
        Label lblPriceHeader = new Label("💰 Bảng Giá Theo Loại Xe");
        lblPriceHeader.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976D2; -fx-font-weight: 700; -fx-padding: 10 0 5 0;");
        
        // Price for Mini
        Label lblPriceMini = new Label("Giá cho xe Mini *");
        lblPriceMini.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtPriceMini = new TextField();
        txtPriceMini.setPromptText("Nhập giá (VD: 40000)");
        txtPriceMini.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPriceMini.setPrefWidth(400);
        if (isEdit && existingService != null) {
            txtPriceMini.setText(String.format("%.0f", existingService.getPriceMini()));
        }
        
        // Price for Sedan
        Label lblPriceSedan = new Label("Giá cho xe Sedan *");
        lblPriceSedan.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtPriceSedan = new TextField();
        txtPriceSedan.setPromptText("Nhập giá (VD: 50000)");
        txtPriceSedan.setStyle(
            "-fx-background-color: #E8F5E9;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPriceSedan.setPrefWidth(400);
        if (isEdit && existingService != null) {
            txtPriceSedan.setText(String.format("%.0f", existingService.getPriceSedan()));
        }
        
        // Price for CUV
        Label lblPriceCuv = new Label("Giá cho xe CUV *");
        lblPriceCuv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtPriceCuv = new TextField();
        txtPriceCuv.setPromptText("Nhập giá (VD: 75000)");
        txtPriceCuv.setStyle(
            "-fx-background-color: #FFF3E0;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPriceCuv.setPrefWidth(400);
        if (isEdit && existingService != null) {
            txtPriceCuv.setText(String.format("%.0f", existingService.getPriceCuv()));
        }
        
        // Price for SUV
        Label lblPriceSuv = new Label("Giá cho xe SUV *");
        lblPriceSuv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtPriceSuv = new TextField();
        txtPriceSuv.setPromptText("Nhập giá (VD: 100000)");
        txtPriceSuv.setStyle(
            "-fx-background-color: #FCE4EC;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPriceSuv.setPrefWidth(400);
        if (isEdit && existingService != null) {
            txtPriceSuv.setText(String.format("%.0f", existingService.getPriceSuv()));
        }
        
        // Price for Pickup
        Label lblPricePickup = new Label("Giá cho xe Pickup *");
        lblPricePickup.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtPricePickup = new TextField();
        txtPricePickup.setPromptText("Nhập giá (VD: 110000)");
        txtPricePickup.setStyle(
            "-fx-background-color: #F3E5F5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPricePickup.setPrefWidth(400);
        if (isEdit && existingService != null) {
            txtPricePickup.setText(String.format("%.0f", existingService.getPricePickup()));
        }
        
        // Status
        Label lblStatus = new Label("Trạng thái");
        lblStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        HBox statusBox = new HBox(15);
        ToggleGroup statusGroup = new ToggleGroup();
        
        RadioButton rbActive = new RadioButton("Đang hoạt động");
        rbActive.setToggleGroup(statusGroup);
        rbActive.setSelected(true);
        rbActive.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        RadioButton rbInactive = new RadioButton("Tạm dừng");
        rbInactive.setToggleGroup(statusGroup);
        rbInactive.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        statusBox.getChildren().addAll(rbActive, rbInactive);
        
        int row = 0;
        grid.add(lblName, 0, row++);
        grid.add(txtName, 0, row++);
        grid.add(lblDesc, 0, row++);
        grid.add(txtDesc, 0, row++);
        grid.add(lblPriceHeader, 0, row++);
        grid.add(lblPriceMini, 0, row++);
        grid.add(txtPriceMini, 0, row++);
        grid.add(lblPriceSedan, 0, row++);
        grid.add(txtPriceSedan, 0, row++);
        grid.add(lblPriceCuv, 0, row++);
        grid.add(txtPriceCuv, 0, row++);
        grid.add(lblPriceSuv, 0, row++);
        grid.add(txtPriceSuv, 0, row++);
        grid.add(lblPricePickup, 0, row++);
        grid.add(txtPricePickup, 0, row++);
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
            "-fx-cursor: hand;"
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
            "-fx-cursor: hand;"
        );
        btnSave.setOnAction(e -> {
            // Validate input
            if (txtName.getText().trim().isEmpty()) {
                showAlert("Cảnh báo", "Vui lòng nhập tên dịch vụ!", Alert.AlertType.WARNING);
                return;
            }
            
            if (txtPriceMini.getText().trim().isEmpty()) {
                showAlert("Cảnh báo", "Vui lòng nhập giá cho xe Mini!", Alert.AlertType.WARNING);
                return;
            }
            
            if (txtPriceSedan.getText().trim().isEmpty()) {
                showAlert("Cảnh báo", "Vui lòng nhập giá cho xe Sedan!", Alert.AlertType.WARNING);
                return;
            }
            
            if (txtPriceCuv.getText().trim().isEmpty()) {
                showAlert("Cảnh báo", "Vui lòng nhập giá cho xe CUV!", Alert.AlertType.WARNING);
                return;
            }
            
            if (txtPriceSuv.getText().trim().isEmpty()) {
                showAlert("Cảnh báo", "Vui lòng nhập giá cho xe SUV!", Alert.AlertType.WARNING);
                return;
            }
            
            if (txtPricePickup.getText().trim().isEmpty()) {
                showAlert("Cảnh báo", "Vui lòng nhập giá cho xe Pickup!", Alert.AlertType.WARNING);
                return;
            }
            
            try {
                String name = txtName.getText().trim();
                String desc = txtDesc.getText().trim();
                double priceMini = Double.parseDouble(txtPriceMini.getText().trim());
                double priceSedan = Double.parseDouble(txtPriceSedan.getText().trim());
                double priceCuv = Double.parseDouble(txtPriceCuv.getText().trim());
                double priceSuv = Double.parseDouble(txtPriceSuv.getText().trim());
                double pricePickup = Double.parseDouble(txtPricePickup.getText().trim());
                
                ServiceService serviceService = new ServiceService();
                boolean success;
                
                if (isEdit) {
                    success = serviceService.updateService(serviceId, name, desc, priceMini, priceSedan, priceCuv, priceSuv, pricePickup);
                } else {
                    success = serviceService.addService(name, desc, priceMini, priceSedan, priceCuv, priceSuv, pricePickup);
                }
                
                if (success) {
                    showAlert("Thành công", 
                             isEdit ? "Cập nhật dịch vụ thành công!" : "Thêm dịch vụ mới thành công!", 
                             Alert.AlertType.INFORMATION);
                    stage.close();
                    if (onSave != null) {
                        onSave.run();
                    }
                } else {
                    showAlert("Lỗi", "Không thể lưu dịch vụ! Kiểm tra console để xem chi tiết.", Alert.AlertType.ERROR);
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
