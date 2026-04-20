package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.ServiceService;
import service.PackageService;
import model.Service;
import java.util.List;
import java.util.ArrayList;

public class PackageForm {
    
    private Stage stage;
    private boolean isEdit;
    private int packageId;
    private String packageName;
    private String packageDescription;
    private String packagePrice;
    private String packageSavings;
    private List<CheckBox> serviceCheckBoxes;
    private RadioButton rbActive;
    private RadioButton rbInactive;
    private TextField txtName;
    private TextArea txtDesc;
    private TextField txtPriceSedan;
    private TextField txtPriceSUV;
    private Runnable onSave;
    
    public PackageForm() {
        this.isEdit = false;
        this.serviceCheckBoxes = new ArrayList<>();
    }
    
    public PackageForm(Runnable onSave) {
        this.isEdit = false;
        this.serviceCheckBoxes = new ArrayList<>();
        this.onSave = onSave;
    }
    
    public PackageForm(int id, String packageName, String description, String price, String savings, Runnable onSave) {
        this.isEdit = true;
        this.packageId = id;
        this.packageName = packageName;
        this.packageDescription = description;
        this.packagePrice = price;
        this.packageSavings = savings;
        this.serviceCheckBoxes = new ArrayList<>();
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
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        // Form Section
        VBox formSection = createFormSection();
        
        // Action Buttons
        HBox actionButtons = createActionButtons();
        
        mainContent.getChildren().addAll(title, formSection, actionButtons);
        scrollPane.setContent(mainContent);
        
        Scene scene = new Scene(scrollPane, 700, 850);
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
        grid.setVgap(20);
        
        // Package name
        Label lblName = new Label("Tên gói dịch vụ *");
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtName = new TextField();
        txtName.setPromptText("Nhập tên gói (VD: Gói VIP 1)");
        txtName.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtName.setPrefWidth(400);
        if (isEdit && packageName != null) {
            txtName.setText(packageName);
        }
        
        // Description
        Label lblDesc = new Label("Mô tả gói *");
        lblDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtDesc = new TextArea();
        txtDesc.setPromptText("Nhập mô tả gói (VD: Rửa xe cơ bản + Hút bụi nội thất + Đánh bóng)");
        txtDesc.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtDesc.setPrefRowCount(3);
        txtDesc.setPrefWidth(400);
        if (isEdit && packageDescription != null) {
            txtDesc.setText(packageDescription);
        }
        
        // Services selection
        Label lblServices = new Label("Chọn dịch vụ trong gói *");
        lblServices.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        VBox servicesBox = new VBox(8);
        servicesBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 15;" +
            "-fx-max-height: 200;"
        );
        
        // Load services from database
        ServiceService serviceService = new ServiceService();
        List<Service> services = serviceService.getAllServices();
        
        if (services.isEmpty()) {
            Label emptyLabel = new Label("Chưa có dịch vụ nào. Vui lòng thêm dịch vụ trước.");
            emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #f44336;");
            servicesBox.getChildren().add(emptyLabel);
        } else {
            for (Service service : services) {
                String displayText = String.format("%s (%.0fđ - %.0fđ)", 
                    service.getName(), 
                    service.getPriceSmall(), 
                    service.getPriceLarge()
                );
                CheckBox cb = new CheckBox(displayText);
                cb.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242;");
                cb.setUserData(service); // Store service object for later use
                serviceCheckBoxes.add(cb);
                servicesBox.getChildren().add(cb);
            }
        }
        
        // Wrap in ScrollPane if there are many services
        ScrollPane servicesScrollPane = new ScrollPane(servicesBox);
        servicesScrollPane.setFitToWidth(true);
        servicesScrollPane.setMaxHeight(200);
        servicesScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        // Car type pricing
        Label lblCarType = new Label("Giá theo loại xe");
        lblCarType.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        HBox priceBox = new HBox(20);
        
        VBox sedanBox = new VBox(8);
        Label lblSedan = new Label("Giá cho xe Sedan *");
        lblSedan.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242; -fx-font-weight: 500;");
        txtPriceSedan = new TextField();
        txtPriceSedan.setPromptText("120000");
        txtPriceSedan.setPrefWidth(180);
        txtPriceSedan.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        if (isEdit && packagePrice != null) {
            txtPriceSedan.setText(packagePrice.replace("đ", "").trim());
        }
        sedanBox.getChildren().addAll(lblSedan, txtPriceSedan);
        
        VBox suvBox = new VBox(8);
        Label lblSUV = new Label("Giá cho xe SUV *");
        lblSUV.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242; -fx-font-weight: 500;");
        txtPriceSUV = new TextField();
        txtPriceSUV.setPromptText("180000");
        txtPriceSUV.setPrefWidth(180);
        txtPriceSUV.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        if (isEdit && packagePrice != null) {
            txtPriceSUV.setText(packagePrice.replace("đ", "").trim());
        }
        suvBox.getChildren().addAll(lblSUV, txtPriceSUV);
        
        priceBox.getChildren().addAll(sedanBox, suvBox);
        
        // Savings calculation
        Label lblSavings = new Label("Tiết kiệm so với giá lẻ");
        lblSavings.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        HBox savingsBox = new HBox(20);
        Label lblSedanSavings = new Label("Sedan: 0đ");
        lblSedanSavings.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50; -fx-font-weight: 600;");
        
        Label lblSUVSavings = new Label("SUV: 0đ");
        lblSUVSavings.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50; -fx-font-weight: 600;");
        
        savingsBox.getChildren().addAll(lblSedanSavings, lblSUVSavings);
        
        // Add listener to calculate savings when services are selected or prices change
        Runnable calculateSavings = () -> {
            double totalSedanPrice = 0;
            double totalSUVPrice = 0;
            
            for (CheckBox cb : serviceCheckBoxes) {
                if (cb.isSelected() && cb.getUserData() instanceof Service) {
                    Service service = (Service) cb.getUserData();
                    totalSedanPrice += service.getPriceSmall();
                    totalSUVPrice += service.getPriceLarge();
                }
            }
            
            try {
                double packageSedanPrice = Double.parseDouble(txtPriceSedan.getText().trim());
                double packageSUVPrice = Double.parseDouble(txtPriceSUV.getText().trim());
                
                double sedanSavings = totalSedanPrice - packageSedanPrice;
                double suvSavings = totalSUVPrice - packageSUVPrice;
                
                lblSedanSavings.setText(String.format("Sedan: %.0fđ", Math.max(0, sedanSavings)));
                lblSUVSavings.setText(String.format("SUV: %.0fđ", Math.max(0, suvSavings)));
            } catch (NumberFormatException e) {
                lblSedanSavings.setText("Sedan: 0đ");
                lblSUVSavings.setText("SUV: 0đ");
            }
        };
        
        // Add listeners to checkboxes and price fields
        for (CheckBox cb : serviceCheckBoxes) {
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> calculateSavings.run());
        }
        txtPriceSedan.textProperty().addListener((obs, oldVal, newVal) -> calculateSavings.run());
        txtPriceSUV.textProperty().addListener((obs, oldVal, newVal) -> calculateSavings.run());
        
        // Status
        Label lblStatus = new Label("Trạng thái");
        lblStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        HBox statusBox = new HBox(15);
        ToggleGroup statusGroup = new ToggleGroup();
        
        rbActive = new RadioButton("Đang bán");
        rbActive.setToggleGroup(statusGroup);
        rbActive.setSelected(true);
        rbActive.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        rbInactive = new RadioButton("Tạm dừng");
        rbInactive.setToggleGroup(statusGroup);
        rbInactive.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        statusBox.getChildren().addAll(rbActive, rbInactive);
        
        grid.add(lblName, 0, 0);
        grid.add(txtName, 0, 1);
        grid.add(lblDesc, 0, 2);
        grid.add(txtDesc, 0, 3);
        grid.add(lblServices, 0, 4);
        grid.add(servicesScrollPane, 0, 5);
        grid.add(lblCarType, 0, 6);
        grid.add(priceBox, 0, 7);
        grid.add(lblSavings, 0, 8);
        grid.add(savingsBox, 0, 9);
        grid.add(lblStatus, 0, 10);
        grid.add(statusBox, 0, 11);
        
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
                alert.setContentText("Vui lòng nhập tên gói dịch vụ!");
                alert.showAndWait();
                return;
            }
            
            if (txtDesc.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập mô tả gói!");
                alert.showAndWait();
                return;
            }
            
            // Check if at least one service is selected
            boolean hasSelectedService = false;
            for (CheckBox cb : serviceCheckBoxes) {
                if (cb.isSelected()) {
                    hasSelectedService = true;
                    break;
                }
            }
            
            if (!hasSelectedService) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng chọn ít nhất một dịch vụ!");
                alert.showAndWait();
                return;
            }
            
            if (txtPriceSedan.getText().trim().isEmpty() || txtPriceSUV.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập giá cho cả Sedan và SUV!");
                alert.showAndWait();
                return;
            }
            
            try {
                String name = txtName.getText().trim();
                String description = txtDesc.getText().trim();
                double priceSedan = Double.parseDouble(txtPriceSedan.getText().trim());
                double priceSUV = Double.parseDouble(txtPriceSUV.getText().trim());
                
                // Calculate average price for the package
                double avgPrice = (priceSedan + priceSUV) / 2;
                
                // Calculate total retail price
                double totalRetailPrice = 0;
                for (CheckBox cb : serviceCheckBoxes) {
                    if (cb.isSelected() && cb.getUserData() instanceof Service) {
                        Service service = (Service) cb.getUserData();
                        totalRetailPrice += (service.getPriceSmall() + service.getPriceLarge()) / 2;
                    }
                }
                
                double savings = Math.max(0, totalRetailPrice - avgPrice);
                
                // Get status
                String status = rbActive.isSelected() ? "Đang bán" : "Tạm dừng";
                
                // Save to database
                PackageService packageService = new PackageService();
                boolean success;
                
                if (isEdit) {
                    success = packageService.updatePackage(packageId, name, description, avgPrice, savings, status);
                } else {
                    success = packageService.addPackage(name, description, avgPrice, savings, status);
                }
                
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText(null);
                    alert.setContentText(isEdit ? "Cập nhật gói dịch vụ thành công!" : "Thêm gói dịch vụ mới thành công!");
                    alert.showAndWait();
                    
                    stage.close();
                    
                    if (onSave != null) {
                        onSave.run();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText("Không thể lưu gói dịch vụ!");
                    alert.showAndWait();
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