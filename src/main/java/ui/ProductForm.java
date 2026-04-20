package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.ProductService;

public class ProductForm {
    
    private Stage stage;
    private boolean isEdit;
    private int productId;
    private String productName;
    private String productCategory;
    private String productPrice;
    private String productStock;
    private TextField txtName;
    private ToggleGroup categoryGroup;
    private TextArea txtDesc;
    private TextField txtPrice;
    private TextField txtCostPrice;
    private TextField txtStock;
    private TextField txtUnit;
    private TextField txtMinStock;
    private ToggleGroup statusGroup;
    private Runnable onSave;
    
    public ProductForm() {
        this.isEdit = false;
    }
    
    public ProductForm(Runnable onSave) {
        this.isEdit = false;
        this.onSave = onSave;
    }
    
    public ProductForm(int id, String productName, String category, String price, String stock, Runnable onSave) {
        this.isEdit = true;
        this.productId = id;
        this.productName = productName;
        this.productCategory = category;
        this.productPrice = price;
        this.productStock = stock;
        this.onSave = onSave;
    }
    
    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(isEdit ? "Sửa Sản Phẩm" : "Thêm Sản Phẩm Mới");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");
        
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");
        
        // Header
        Label title = new Label(isEdit ? "✏ Sửa Sản Phẩm" : "🛒 Thêm Sản Phẩm Mới");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        // Form Section
        VBox formSection = createFormSection();
        
        // Action Buttons
        HBox actionButtons = createActionButtons();
        
        mainContent.getChildren().addAll(title, formSection, actionButtons);
        scrollPane.setContent(mainContent);
        
        Scene scene = new Scene(scrollPane, 700, 800);
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
        
        // Product name
        Label lblName = new Label("Tên sản phẩm *");
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtName = new TextField();
        txtName.setPromptText("Nhập tên sản phẩm");
        txtName.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtName.setPrefWidth(400);
        if (isEdit && productName != null) {
            txtName.setText(productName);
        }
        
        // Category
        Label lblCategory = new Label("Danh mục *");
        lblCategory.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        HBox categoryBox = new HBox(12);
        categoryGroup = new ToggleGroup();
        
        RadioButton rbWater = new RadioButton("Nước rửa xe");
        rbWater.setToggleGroup(categoryGroup);
        rbWater.setSelected(true);
        rbWater.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        RadioButton rbSolution = new RadioButton("Dung dịch");
        rbSolution.setToggleGroup(categoryGroup);
        rbSolution.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        RadioButton rbAccessory = new RadioButton("Phụ kiện");
        rbAccessory.setToggleGroup(categoryGroup);
        rbAccessory.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        // Set selected category if editing
        if (isEdit && productCategory != null) {
            if (productCategory.equals("Dung dịch")) {
                rbSolution.setSelected(true);
            } else if (productCategory.equals("Phụ kiện")) {
                rbAccessory.setSelected(true);
            } else {
                rbWater.setSelected(true);
            }
        }
        
        categoryBox.getChildren().addAll(rbWater, rbSolution, rbAccessory);
        
        // Description
        Label lblDesc = new Label("Mô tả");
        lblDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtDesc = new TextArea();
        txtDesc.setPromptText("Nhập mô tả sản phẩm");
        txtDesc.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtDesc.setPrefRowCount(3);
        txtDesc.setPrefWidth(400);
        
        // Price
        Label lblPrice = new Label("Giá bán *");
        lblPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtPrice = new TextField();
        txtPrice.setPromptText("Nhập giá bán (VD: 150000)");
        txtPrice.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPrice.setPrefWidth(400);
        if (isEdit && productPrice != null) {
            txtPrice.setText(productPrice.replace("đ", "").trim());
        }
        
        // Cost price
        Label lblCostPrice = new Label("Giá nhập");
        lblCostPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtCostPrice = new TextField();
        txtCostPrice.setPromptText("Nhập giá nhập (VD: 100000)");
        txtCostPrice.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtCostPrice.setPrefWidth(400);
        
        // Stock quantity
        Label lblStock = new Label("Số lượng tồn kho *");
        lblStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        HBox stockBox = new HBox(10);
        txtStock = new TextField();
        txtStock.setPromptText("0");
        txtStock.setPrefWidth(150);
        txtStock.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        if (isEdit && productStock != null) {
            txtStock.setText(productStock);
        }
        
        Label lblUnit = new Label("đơn vị");
        lblUnit.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        
        txtUnit = new TextField();
        txtUnit.setPromptText("chai, hộp, cái...");
        txtUnit.setPrefWidth(150);
        txtUnit.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        
        stockBox.getChildren().addAll(txtStock, lblUnit, txtUnit);
        
        // Min stock alert
        Label lblMinStock = new Label("Cảnh báo tồn kho tối thiểu");
        lblMinStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtMinStock = new TextField();
        txtMinStock.setPromptText("Nhập số lượng tối thiểu (VD: 5)");
        txtMinStock.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtMinStock.setPrefWidth(400);
        
        // Status
        Label lblStatus = new Label("Trạng thái");
        lblStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        HBox statusBox = new HBox(15);
        statusGroup = new ToggleGroup();
        
        RadioButton rbActive = new RadioButton("Đang bán");
        rbActive.setToggleGroup(statusGroup);
        rbActive.setSelected(true);
        rbActive.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        RadioButton rbInactive = new RadioButton("Tạm dừng");
        rbInactive.setToggleGroup(statusGroup);
        rbInactive.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        RadioButton rbOutOfStock = new RadioButton("Hết hàng");
        rbOutOfStock.setToggleGroup(statusGroup);
        rbOutOfStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        statusBox.getChildren().addAll(rbActive, rbInactive, rbOutOfStock);
        
        grid.add(lblName, 0, 0);
        grid.add(txtName, 0, 1);
        grid.add(lblCategory, 0, 2);
        grid.add(categoryBox, 0, 3);
        grid.add(lblDesc, 0, 4);
        grid.add(txtDesc, 0, 5);
        grid.add(lblPrice, 0, 6);
        grid.add(txtPrice, 0, 7);
        grid.add(lblCostPrice, 0, 8);
        grid.add(txtCostPrice, 0, 9);
        grid.add(lblStock, 0, 10);
        grid.add(stockBox, 0, 11);
        grid.add(lblMinStock, 0, 12);
        grid.add(txtMinStock, 0, 13);
        grid.add(lblStatus, 0, 14);
        grid.add(statusBox, 0, 15);
        
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
                alert.setContentText("Vui lòng nhập tên sản phẩm!");
                alert.showAndWait();
                return;
            }
            
            if (txtPrice.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập giá bán!");
                alert.showAndWait();
                return;
            }
            
            if (txtStock.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập số lượng tồn kho!");
                alert.showAndWait();
                return;
            }
            
            try {
                String name = txtName.getText().trim();
                
                // Get selected category
                RadioButton selectedCategory = (RadioButton) categoryGroup.getSelectedToggle();
                String category = selectedCategory != null ? selectedCategory.getText() : "Khác";
                
                double price = Double.parseDouble(txtPrice.getText().trim());
                int stock = Integer.parseInt(txtStock.getText().trim());
                
                // Get status
                RadioButton selectedStatus = (RadioButton) statusGroup.getSelectedToggle();
                String status = selectedStatus != null ? selectedStatus.getText() : "Còn hàng";
                
                // Save to database
                ProductService productService = new ProductService();
                boolean success;
                
                if (isEdit) {
                    success = productService.updateProduct(productId, name, category, price, stock, status);
                } else {
                    success = productService.addProduct(name, category, price, stock, status);
                }
                
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText(null);
                    alert.setContentText(isEdit ? "Cập nhật sản phẩm thành công!" : "Thêm sản phẩm mới thành công!");
                    alert.showAndWait();
                    
                    stage.close();
                    
                    if (onSave != null) {
                        onSave.run();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText("Không thể lưu sản phẩm!");
                    alert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Giá và số lượng phải là số!");
                alert.showAndWait();
            }
        });
        
        buttons.getChildren().addAll(btnCancel, btnSave);
        return buttons;
    }
}