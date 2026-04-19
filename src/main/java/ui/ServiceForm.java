package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.ServiceService;

public class ServiceForm {
    
    private Stage stage;
    private boolean isEdit;
    private int serviceId;
    private String serviceName;
    private String description;
    private String priceSedan;
    private String priceSUV;
    private Runnable onSave;
    
    // Form fields
    private TextField txtName;
    private TextArea txtDesc;
    private TextField txtPriceSedan;
    private TextField txtPriceSUV;
    
    public ServiceForm() {
        this.isEdit = false;
    }
    
    public ServiceForm(String serviceName, String description, String priceSedan, String priceSUV) {
        this.isEdit = true;
        this.serviceName = serviceName;
        this.description = description;
        this.priceSedan = priceSedan;
        this.priceSUV = priceSUV;
    }
    
    public ServiceForm(Runnable onSave) {
        this.isEdit = false;
        this.onSave = onSave;
    }
    
    public ServiceForm(String serviceName, String description, String priceSedan, String priceSUV, Runnable onSave) {
        this.isEdit = true;
        this.serviceName = serviceName;
        this.description = description;
        this.priceSedan = priceSedan;
        this.priceSUV = priceSUV;
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
        
        Scene scene = new Scene(scrollPane, 700, 650);
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
        grid.setVgap(20);
        
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
        if (isEdit && serviceName != null) {
            txtName.setText(serviceName);
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
        if (isEdit && description != null) {
            txtDesc.setText(description);
        }
        
        // Price for Sedan
        Label lblPriceSedan = new Label("Giá cho xe Sedan *");
        lblPriceSedan.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtPriceSedan = new TextField();
        txtPriceSedan.setPromptText("Nhập giá (VD: 50000)");
        txtPriceSedan.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPriceSedan.setPrefWidth(400);
        if (isEdit && priceSedan != null) {
            txtPriceSedan.setText(priceSedan.replace("đ", "").trim());
        }
        
        // Price for SUV
        Label lblPriceSUV = new Label("Giá cho xe SUV *");
        lblPriceSUV.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtPriceSUV = new TextField();
        txtPriceSUV.setPromptText("Nhập giá (VD: 100000)");
        txtPriceSUV.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPriceSUV.setPrefWidth(400);
        if (isEdit && priceSUV != null) {
            txtPriceSUV.setText(priceSUV.replace("đ", "").trim());
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
        
        grid.add(lblName, 0, 0);
        grid.add(txtName, 0, 1);
        grid.add(lblDesc, 0, 2);
        grid.add(txtDesc, 0, 3);
        grid.add(lblPriceSedan, 0, 4);
        grid.add(txtPriceSedan, 0, 5);
        grid.add(lblPriceSUV, 0, 6);
        grid.add(txtPriceSUV, 0, 7);
        grid.add(lblStatus, 0, 8);
        grid.add(statusBox, 0, 9);
        
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
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập tên dịch vụ!");
                alert.showAndWait();
                return;
            }
            
            if (txtPriceSedan.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập giá cho xe Sedan!");
                alert.showAndWait();
                return;
            }
            
            if (txtPriceSUV.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập giá cho xe SUV!");
                alert.showAndWait();
                return;
            }
            
            try {
                String name = txtName.getText().trim();
                String desc = txtDesc.getText().trim();
                double priceSmall = Double.parseDouble(txtPriceSedan.getText().trim());
                double priceLarge = Double.parseDouble(txtPriceSUV.getText().trim());
                
                ServiceService serviceService = new ServiceService();
                boolean success;
                
                if (isEdit) {
                    // Update existing service
                    success = serviceService.updateService(serviceId, name, desc, priceSmall, priceLarge);
                } else {
                    // Add new service
                    success = serviceService.addService(name, desc, priceSmall, priceLarge);
                }
                
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText(null);
                    alert.setContentText(isEdit ? "Cập nhật dịch vụ thành công!" : "Thêm dịch vụ mới thành công!");
                    alert.showAndWait();
                    
                    stage.close();
                    
                    // Gọi callback sau khi đóng form để refresh UI
                    if (onSave != null) {
                        onSave.run();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText("Không thể lưu dịch vụ! Kiểm tra console để xem chi tiết.");
                    alert.showAndWait();
                    System.err.println("DEBUG: addService/updateService trả về false");
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Giá phải là số!");
                alert.showAndWait();
            }
        });
        
        buttons.getChildren().addAll(btnCancel, btnSave);
        return buttons;
    }
}