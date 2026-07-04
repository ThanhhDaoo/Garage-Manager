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
    private CheckBox chkMini;
    private CheckBox chkSedan;
    private CheckBox chkCuv;
    private CheckBox chkSuv;
    private CheckBox chkMpv;
    private CheckBox chkPickup;
    private TextField txtPriceMini;
    private TextField txtPriceSedan;
    private TextField txtPriceCuv;
    private TextField txtPriceSuv;
    private TextField txtPriceMpv;
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
        UIUtils.setupIMEFix(txtName);
        
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
        UIUtils.setupIMEFix(txtDesc);
        
        // Price section header
        Label lblPriceHeader = new Label("💰 Giá Gói Theo Loại Xe (Tích chọn để nhập giá)");
        lblPriceHeader.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976D2; -fx-font-weight: 700; -fx-padding: 10 0 5 0; -fx-font-family: 'Times New Roman';");
        
        // Price list container
        VBox priceListContainer = new VBox(15);
        priceListContainer.setStyle("-fx-background-color: #FAFAFA; -fx-padding: 20; -fx-background-radius: 10; -fx-border-color: #E0E0E0; -fx-border-radius: 10;");
        
        // Price for Mini
        chkMini = new CheckBox("Mini");
        chkMini.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 120; -fx-font-family: 'Times New Roman';");
        txtPriceMini = new TextField();
        txtPriceMini.setPromptText("96000");
        txtPriceMini.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 10px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-font-family: 'Times New Roman';");
        txtPriceMini.setPrefWidth(300);
        txtPriceMini.visibleProperty().bind(chkMini.selectedProperty());
        txtPriceMini.managedProperty().bind(chkMini.selectedProperty());
        txtPriceMini.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtPriceMini.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtPriceMini.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtPriceMini);
        HBox miniBox = new HBox(15);
        miniBox.setAlignment(Pos.CENTER_LEFT);
        miniBox.getChildren().addAll(chkMini, txtPriceMini);
        if (isEdit && existingPackage != null && existingPackage.getPriceMini() > 0) {
            chkMini.setSelected(true);
            txtPriceMini.setText(String.format("%.0f", existingPackage.getPriceMini()));
        } else {
            chkMini.setSelected(false);
        }
        
        // Price for Sedan
        chkSedan = new CheckBox("Sedan");
        chkSedan.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 120; -fx-font-family: 'Times New Roman';");
        txtPriceSedan = new TextField();
        txtPriceSedan.setPromptText("120000");
        txtPriceSedan.setStyle("-fx-background-color: #E8F5E9; -fx-padding: 10px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-font-family: 'Times New Roman';");
        txtPriceSedan.setPrefWidth(300);
        txtPriceSedan.visibleProperty().bind(chkSedan.selectedProperty());
        txtPriceSedan.managedProperty().bind(chkSedan.selectedProperty());
        txtPriceSedan.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtPriceSedan.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtPriceSedan.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtPriceSedan);
        HBox sedanBox = new HBox(15);
        sedanBox.setAlignment(Pos.CENTER_LEFT);
        sedanBox.getChildren().addAll(chkSedan, txtPriceSedan);
        if (isEdit && existingPackage != null && existingPackage.getPriceSedan() > 0) {
            chkSedan.setSelected(true);
            txtPriceSedan.setText(String.format("%.0f", existingPackage.getPriceSedan()));
        } else {
            chkSedan.setSelected(false);
        }
        
        // Price for CUV
        chkCuv = new CheckBox("CUV");
        chkCuv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 120; -fx-font-family: 'Times New Roman';");
        txtPriceCuv = new TextField();
        txtPriceCuv.setPromptText("180000");
        txtPriceCuv.setStyle("-fx-background-color: #FFF3E0; -fx-padding: 10px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-font-family: 'Times New Roman';");
        txtPriceCuv.setPrefWidth(300);
        txtPriceCuv.visibleProperty().bind(chkCuv.selectedProperty());
        txtPriceCuv.managedProperty().bind(chkCuv.selectedProperty());
        txtPriceCuv.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtPriceCuv.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtPriceCuv.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtPriceCuv);
        HBox cuvBox = new HBox(15);
        cuvBox.setAlignment(Pos.CENTER_LEFT);
        cuvBox.getChildren().addAll(chkCuv, txtPriceCuv);
        if (isEdit && existingPackage != null && existingPackage.getPriceCuv() > 0) {
            chkCuv.setSelected(true);
            txtPriceCuv.setText(String.format("%.0f", existingPackage.getPriceCuv()));
        } else {
            chkCuv.setSelected(false);
        }
        
        // Price for SUV
        chkSuv = new CheckBox("SUV");
        chkSuv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 120; -fx-font-family: 'Times New Roman';");
        txtPriceSuv = new TextField();
        txtPriceSuv.setPromptText("240000");
        txtPriceSuv.setStyle("-fx-background-color: #FCE4EC; -fx-padding: 10px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-font-family: 'Times New Roman';");
        txtPriceSuv.setPrefWidth(300);
        txtPriceSuv.visibleProperty().bind(chkSuv.selectedProperty());
        txtPriceSuv.managedProperty().bind(chkSuv.selectedProperty());
        txtPriceSuv.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtPriceSuv.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtPriceSuv.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtPriceSuv);
        HBox suvBox = new HBox(15);
        suvBox.setAlignment(Pos.CENTER_LEFT);
        suvBox.getChildren().addAll(chkSuv, txtPriceSuv);
        if (isEdit && existingPackage != null && existingPackage.getPriceSuv() > 0) {
            chkSuv.setSelected(true);
            txtPriceSuv.setText(String.format("%.0f", existingPackage.getPriceSuv()));
        } else {
            chkSuv.setSelected(false);
        }
        
        // Price for MPV
        chkMpv = new CheckBox("MPV");
        chkMpv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 120; -fx-font-family: 'Times New Roman';");
        txtPriceMpv = new TextField();
        txtPriceMpv.setPromptText("252000");
        txtPriceMpv.setStyle("-fx-background-color: #E0F7FA; -fx-padding: 10px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-font-family: 'Times New Roman';");
        txtPriceMpv.setPrefWidth(300);
        txtPriceMpv.visibleProperty().bind(chkMpv.selectedProperty());
        txtPriceMpv.managedProperty().bind(chkMpv.selectedProperty());
        txtPriceMpv.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtPriceMpv.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtPriceMpv.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtPriceMpv);
        HBox mpvBox = new HBox(15);
        mpvBox.setAlignment(Pos.CENTER_LEFT);
        mpvBox.getChildren().addAll(chkMpv, txtPriceMpv);
        if (isEdit && existingPackage != null && existingPackage.getPriceMpv() > 0) {
            chkMpv.setSelected(true);
            txtPriceMpv.setText(String.format("%.0f", existingPackage.getPriceMpv()));
        } else {
            chkMpv.setSelected(false);
        }
        
        // Price for Pickup
        chkPickup = new CheckBox("Pickup");
        chkPickup.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 120; -fx-font-family: 'Times New Roman';");
        txtPricePickup = new TextField();
        txtPricePickup.setPromptText("264000");
        txtPricePickup.setStyle("-fx-background-color: #F3E5F5; -fx-padding: 10px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-font-family: 'Times New Roman';");
        txtPricePickup.setPrefWidth(300);
        txtPricePickup.visibleProperty().bind(chkPickup.selectedProperty());
        txtPricePickup.managedProperty().bind(chkPickup.selectedProperty());
        txtPricePickup.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtPricePickup.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtPricePickup.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtPricePickup);
        HBox pickupBox = new HBox(15);
        pickupBox.setAlignment(Pos.CENTER_LEFT);
        pickupBox.getChildren().addAll(chkPickup, txtPricePickup);
        if (isEdit && existingPackage != null && existingPackage.getPricePickup() > 0) {
            chkPickup.setSelected(true);
            txtPricePickup.setText(String.format("%.0f", existingPackage.getPricePickup()));
        } else {
            chkPickup.setSelected(false);
        }
        
        priceListContainer.getChildren().addAll(miniBox, sedanBox, cuvBox, suvBox, mpvBox, pickupBox);
        
        // Status
        Label lblStatus = new Label("Trạng thái");
        lblStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        
        HBox statusBox = new HBox(15);
        ToggleGroup statusGroup = new ToggleGroup();
        
        rbActive = new RadioButton("Hoạt động");
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
        grid.add(priceListContainer, 0, row++);
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
            
            if (!chkMini.isSelected() && !chkSedan.isSelected() && !chkCuv.isSelected() &&
                !chkSuv.isSelected() && !chkMpv.isSelected() && !chkPickup.isSelected()) {
                showAlert("Cảnh báo", "Vui lòng chọn ít nhất một loại xe áp dụng!", Alert.AlertType.WARNING);
                return;
            }
            
            double priceMini = 0;
            if (chkMini.isSelected()) {
                String val = txtPriceMini.getText().trim();
                if (val.isEmpty()) {
                    showAlert("Cảnh báo", "Vui lòng nhập giá cho xe Mini!", Alert.AlertType.WARNING);
                    return;
                }
                try {
                    priceMini = Double.parseDouble(val);
                } catch (NumberFormatException ex) {
                    showAlert("Lỗi", "Giá xe Mini phải là số hợp lệ!", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            double priceSedan = 0;
            if (chkSedan.isSelected()) {
                String val = txtPriceSedan.getText().trim();
                if (val.isEmpty()) {
                    showAlert("Cảnh báo", "Vui lòng nhập giá cho xe Sedan!", Alert.AlertType.WARNING);
                    return;
                }
                try {
                    priceSedan = Double.parseDouble(val);
                } catch (NumberFormatException ex) {
                    showAlert("Lỗi", "Giá xe Sedan phải là số hợp lệ!", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            double priceCuv = 0;
            if (chkCuv.isSelected()) {
                String val = txtPriceCuv.getText().trim();
                if (val.isEmpty()) {
                    showAlert("Cảnh báo", "Vui lòng nhập giá cho xe CUV!", Alert.AlertType.WARNING);
                    return;
                }
                try {
                    priceCuv = Double.parseDouble(val);
                } catch (NumberFormatException ex) {
                    showAlert("Lỗi", "Giá xe CUV phải là số hợp lệ!", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            double priceSuv = 0;
            if (chkSuv.isSelected()) {
                String val = txtPriceSuv.getText().trim();
                if (val.isEmpty()) {
                    showAlert("Cảnh báo", "Vui lòng nhập giá cho xe SUV!", Alert.AlertType.WARNING);
                    return;
                }
                try {
                    priceSuv = Double.parseDouble(val);
                } catch (NumberFormatException ex) {
                    showAlert("Lỗi", "Giá xe SUV phải là số hợp lệ!", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            double priceMpv = 0;
            if (chkMpv.isSelected()) {
                String val = txtPriceMpv.getText().trim();
                if (val.isEmpty()) {
                    showAlert("Cảnh báo", "Vui lòng nhập giá cho xe MPV!", Alert.AlertType.WARNING);
                    return;
                }
                try {
                    priceMpv = Double.parseDouble(val);
                } catch (NumberFormatException ex) {
                    showAlert("Lỗi", "Giá xe MPV phải là số hợp lệ!", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            double pricePickup = 0;
            if (chkPickup.isSelected()) {
                String val = txtPricePickup.getText().trim();
                if (val.isEmpty()) {
                    showAlert("Cảnh báo", "Vui lòng nhập giá cho xe Pickup!", Alert.AlertType.WARNING);
                    return;
                }
                try {
                    pricePickup = Double.parseDouble(val);
                } catch (NumberFormatException ex) {
                    showAlert("Lỗi", "Giá xe Pickup phải là số hợp lệ!", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            try {
                String name = txtName.getText().trim();
                String description = txtDesc.getText().trim();
                
                // Set avgSavings to 0 since we're not tracking individual services
                double avgSavings = 0;
                
                String status = rbActive.isSelected() ? "Đang bán" : "Tạm dừng";
                
                PackageService packageService = new PackageService();
                boolean success;
                
                if (isEdit) {
                    success = packageService.updatePackage(packageId, name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, avgSavings, status);
                } else {
                    success = packageService.addPackage(name, description, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, avgSavings, status);
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
            } catch (Exception ex) {
                showAlert("Lỗi", "Đã xảy ra lỗi khi lưu gói dịch vụ: " + ex.getMessage(), Alert.AlertType.ERROR);
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
