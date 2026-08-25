package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Service;
import model.Product;
import dao.ProductDAO;
import service.ServiceService;
import java.util.List;

public class ServiceForm {
    
    private Stage stage;
    private boolean isEdit;
    private int serviceId;
    private Service existingService;
    private Runnable onSave;
    
    // Form fields
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
    private TextField txtCostMini;
    private TextField txtCostSedan;
    private TextField txtCostCuv;
    private TextField txtCostSuv;
    private TextField txtCostMpv;
    private TextField txtCostPickup;
    private ComboBox<String> cbCategory;
    private ComboBox<ProductWrapper> cbLinkedProduct;
    private TextField txtLinkedQty;
    private TextField txtSearchLinkedProduct;

    
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
        if (MainUI.getMainStage() != null && MainUI.getMainStage().getScene() != null && MainUI.getMainStage().getScene().getRoot() != null) {
            MainUI.getMainStage().getScene().getRoot().requestFocus();
        }
        stage = new Stage();
        if (MainUI.getMainStage() != null) {
            stage.initOwner(MainUI.getMainStage());
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(isEdit ? "Sửa Dịch Vụ" : "Thêm Dịch Vụ Mới");
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

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
        
        mainContent.getChildren().addAll(title, formSection);
        scrollPane.setContent(mainContent);
        
        // Action Buttons (Fixed at bottom)
        HBox actionButtons = createActionButtons();
        actionButtons.setPadding(new Insets(15, 30, 15, 30));
        actionButtons.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1 0 0 0;"
        );

        root.setCenter(scrollPane);
        root.setBottom(actionButtons);

        Scene scene = new Scene(root, 750, 750);
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
        stage.setScene(scene);
        stage.showAndWait();
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
        
        ColumnConstraints col = new ColumnConstraints();
        col.setHgrow(Priority.ALWAYS);
        col.setFillWidth(true);
        grid.getColumnConstraints().add(col);
        
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
        txtName.setMaxWidth(Double.MAX_VALUE);
        if (isEdit && existingService != null) {
            txtName.setText(existingService.getName());
        }
        UIUtils.setupIMEFix(txtName);
        
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
        txtDesc.setMaxWidth(Double.MAX_VALUE);
        if (isEdit && existingService != null) {
            txtDesc.setText(existingService.getDescription());
        }
        UIUtils.setupIMEFix(txtDesc);
        
        // Category ComboBox
        Label lblCategory = new Label("Phân loại dịch vụ *");
        lblCategory.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        cbCategory = new ComboBox<>();
        cbCategory.getItems().addAll("rửa xe", "chăm sóc", "phụ kiện", "sơn");
        cbCategory.setValue(isEdit && existingService != null && existingService.getCategory() != null ? existingService.getCategory() : "rửa xe");
        cbCategory.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;" +
            "-fx-pref-height: 44px;" +
            "-fx-pref-width: 400px;"
        );
        cbCategory.setMaxWidth(Double.MAX_VALUE);

        // Linked Product ComboBox
        Label lblLinkedProduct = new Label("Vật tư/Sản phẩm tiêu hao đi kèm");
        lblLinkedProduct.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        txtSearchLinkedProduct = new TextField();
        txtSearchLinkedProduct.setPromptText("🔍 Tìm vật tư...");
        txtSearchLinkedProduct.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 14px;"
        );
        txtSearchLinkedProduct.setPrefWidth(180);
        UIUtils.setupIMEFix(txtSearchLinkedProduct);

        cbLinkedProduct = new ComboBox<>();
        cbLinkedProduct.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;" +
            "-fx-pref-height: 44px;" +
            "-fx-pref-width: 400px;"
        );
        cbLinkedProduct.setMaxWidth(Double.MAX_VALUE);
        
        HBox linkedProductSearchBox = new HBox(10);
        linkedProductSearchBox.setAlignment(Pos.CENTER_LEFT);
        linkedProductSearchBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cbLinkedProduct, Priority.ALWAYS);
        linkedProductSearchBox.getChildren().addAll(txtSearchLinkedProduct, cbLinkedProduct);
        
        // Populate products
        List<ProductWrapper> allWrappers = new java.util.ArrayList<>();
        ProductWrapper defaultWrapper = new ProductWrapper(null, "Không liên kết");
        allWrappers.add(defaultWrapper);
        
        List<Product> products = new ProductDAO().getAllProducts();
        ProductWrapper selectedWrapper = defaultWrapper;
        for (Product p : products) {
            ProductWrapper wrapper = new ProductWrapper(p.getId(), p.getName() + " (" + p.getUnit() + ")");
            allWrappers.add(wrapper);
            if (isEdit && existingService != null && existingService.getLinkedProductId() != null 
                    && existingService.getLinkedProductId().equals(p.getId())) {
                selectedWrapper = wrapper;
            }
        }
        cbLinkedProduct.getItems().setAll(allWrappers);
        cbLinkedProduct.setValue(selectedWrapper);

        // Bind filter logic
        ProductWrapper finalDefaultWrapper = defaultWrapper;
        txtSearchLinkedProduct.textProperty().addListener((obs, oldVal, newVal) -> {
            ProductWrapper currentSelection = cbLinkedProduct.getValue();
            if (newVal == null || newVal.trim().isEmpty()) {
                cbLinkedProduct.getItems().setAll(allWrappers);
            } else {
                String search = newVal.toLowerCase().trim();
                List<ProductWrapper> filtered = new java.util.ArrayList<>();
                filtered.add(finalDefaultWrapper); // Keep "Không liên kết" at the top
                for (int i = 1; i < allWrappers.size(); i++) {
                    ProductWrapper w = allWrappers.get(i);
                    if (w.toString().toLowerCase().contains(search)) {
                        filtered.add(w);
                    }
                }
                cbLinkedProduct.getItems().setAll(filtered);
                if (!cbLinkedProduct.isShowing() && txtSearchLinkedProduct.isFocused()) {
                    cbLinkedProduct.show();
                }
            }
            // Restore selection if it's still in the list, otherwise select default
            if (currentSelection != null && cbLinkedProduct.getItems().contains(currentSelection)) {
                cbLinkedProduct.setValue(currentSelection);
            } else {
                cbLinkedProduct.setValue(finalDefaultWrapper);
            }
        });

        txtSearchLinkedProduct.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.DOWN) {
                cbLinkedProduct.requestFocus();
                cbLinkedProduct.show();
            }
        });

        cbLinkedProduct.setOnHiding(event -> {
            txtSearchLinkedProduct.clear();
        });

        // Linked Product Qty
        Label lblLinkedQty = new Label("Định mức tiêu hao mặc định");
        lblLinkedQty.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        txtLinkedQty = new TextField();
        txtLinkedQty.setPromptText("Nhập số lượng định mức (VD: 0.1 hoặc 100)");
        txtLinkedQty.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtLinkedQty.setPrefWidth(400);
        txtLinkedQty.setMaxWidth(Double.MAX_VALUE);
        if (isEdit && existingService != null && existingService.getLinkedProductQty() != null) {
            txtLinkedQty.setText(new java.text.DecimalFormat("#.####").format(existingService.getLinkedProductQty()));
        } else {
            txtLinkedQty.setText("0");
        }
        UIUtils.setupIMEFix(txtLinkedQty);
        
        // Hide/show quantity text field based on selected product wrapper
        txtLinkedQty.visibleProperty().bind(javafx.beans.binding.Bindings.createBooleanBinding(() -> 
            cbLinkedProduct.getValue() != null && cbLinkedProduct.getValue().getId() != null,
            cbLinkedProduct.valueProperty()
        ));
        txtLinkedQty.managedProperty().bind(txtLinkedQty.visibleProperty());
        lblLinkedQty.visibleProperty().bind(txtLinkedQty.visibleProperty());
        lblLinkedQty.managedProperty().bind(txtLinkedQty.visibleProperty());
        
        // Price section header
        Label lblPriceHeader = new Label("💰 Bảng Giá Theo Loại Xe (Tích chọn để nhập giá bán & chi phí vật tư)");
        lblPriceHeader.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976D2; -fx-font-weight: 700; -fx-padding: 10 0 5 0;");
        
        // Price list container
        VBox priceListContainer = new VBox(15);
        priceListContainer.setStyle("-fx-background-color: #FAFAFA; -fx-padding: 20; -fx-background-radius: 10; -fx-border-color: #E0E0E0; -fx-border-radius: 10;");
        priceListContainer.setMaxWidth(Double.MAX_VALUE);
        
        // Table Header Row for Vehicle Pricing
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setStyle("-fx-padding: 0 0 10 0; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label colVehicle = new Label("Loại Xe");
        colVehicle.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #424242; -fx-min-width: 90;");

        Label colPrice = new Label("Giá Bán Dịch Vụ (VNĐ)");
        colPrice.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1976D2;");
        HBox.setHgrow(colPrice, Priority.ALWAYS);
        colPrice.setMaxWidth(Double.MAX_VALUE);

        Label colCost = new Label("Chi Phí Vật Tư / Giá Vốn (VNĐ)");
        colCost.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #D84315;");
        HBox.setHgrow(colCost, Priority.ALWAYS);
        colCost.setMaxWidth(Double.MAX_VALUE);

        headerRow.getChildren().addAll(colVehicle, colPrice, colCost);
        priceListContainer.getChildren().add(headerRow);
        
        // Price & Cost for Mini
        chkMini = new CheckBox("Mini");
        chkMini.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90;");
        txtPriceMini = new TextField();
        txtPriceMini.setPromptText("Giá bán (VD: 40000)");
        txtPriceMini.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
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

        txtCostMini = new TextField();
        txtCostMini.setPromptText("Chi phí vật tư (VD: 10000)");
        txtCostMini.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
        txtCostMini.visibleProperty().bind(chkMini.selectedProperty());
        txtCostMini.managedProperty().bind(chkMini.selectedProperty());
        txtCostMini.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtCostMini.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtCostMini.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtCostMini);

        HBox miniBox = new HBox(12);
        miniBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtPriceMini, Priority.ALWAYS);
        HBox.setHgrow(txtCostMini, Priority.ALWAYS);
        miniBox.getChildren().addAll(chkMini, txtPriceMini, txtCostMini);
        if (isEdit && existingService != null && existingService.getPriceMini() > 0) {
            chkMini.setSelected(true);
            txtPriceMini.setText(String.format("%.0f", existingService.getPriceMini()));
            txtCostMini.setText(String.format("%.0f", existingService.getCostPriceMini() > 0 ? existingService.getCostPriceMini() : existingService.getCostPrice()));
        } else {
            chkMini.setSelected(false);
            txtCostMini.setText("0");
        }
        
        // Price & Cost for Sedan
        chkSedan = new CheckBox("Sedan");
        chkSedan.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90;");
        txtPriceSedan = new TextField();
        txtPriceSedan.setPromptText("Giá bán (VD: 50000)");
        txtPriceSedan.setStyle("-fx-background-color: #E8F5E9; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
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

        txtCostSedan = new TextField();
        txtCostSedan.setPromptText("Chi phí vật tư (VD: 15000)");
        txtCostSedan.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
        txtCostSedan.visibleProperty().bind(chkSedan.selectedProperty());
        txtCostSedan.managedProperty().bind(chkSedan.selectedProperty());
        txtCostSedan.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtCostSedan.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtCostSedan.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtCostSedan);

        HBox sedanBox = new HBox(12);
        sedanBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtPriceSedan, Priority.ALWAYS);
        HBox.setHgrow(txtCostSedan, Priority.ALWAYS);
        sedanBox.getChildren().addAll(chkSedan, txtPriceSedan, txtCostSedan);
        if (isEdit && existingService != null && existingService.getPriceSedan() > 0) {
            chkSedan.setSelected(true);
            txtPriceSedan.setText(String.format("%.0f", existingService.getPriceSedan()));
            txtCostSedan.setText(String.format("%.0f", existingService.getCostPriceSedan() > 0 ? existingService.getCostPriceSedan() : existingService.getCostPrice()));
        } else {
            chkSedan.setSelected(false);
            txtCostSedan.setText("0");
        }
        
        // Price & Cost for CUV
        chkCuv = new CheckBox("CUV");
        chkCuv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90;");
        txtPriceCuv = new TextField();
        txtPriceCuv.setPromptText("Giá bán (VD: 75000)");
        txtPriceCuv.setStyle("-fx-background-color: #FFF3E0; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
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

        txtCostCuv = new TextField();
        txtCostCuv.setPromptText("Chi phí vật tư (VD: 20000)");
        txtCostCuv.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
        txtCostCuv.visibleProperty().bind(chkCuv.selectedProperty());
        txtCostCuv.managedProperty().bind(chkCuv.selectedProperty());
        txtCostCuv.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtCostCuv.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtCostCuv.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtCostCuv);

        HBox cuvBox = new HBox(12);
        cuvBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtPriceCuv, Priority.ALWAYS);
        HBox.setHgrow(txtCostCuv, Priority.ALWAYS);
        cuvBox.getChildren().addAll(chkCuv, txtPriceCuv, txtCostCuv);
        if (isEdit && existingService != null && existingService.getPriceCuv() > 0) {
            chkCuv.setSelected(true);
            txtPriceCuv.setText(String.format("%.0f", existingService.getPriceCuv()));
            txtCostCuv.setText(String.format("%.0f", existingService.getCostPriceCuv() > 0 ? existingService.getCostPriceCuv() : existingService.getCostPrice()));
        } else {
            chkCuv.setSelected(false);
            txtCostCuv.setText("0");
        }
        
        // Price & Cost for SUV
        chkSuv = new CheckBox("SUV");
        chkSuv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90;");
        txtPriceSuv = new TextField();
        txtPriceSuv.setPromptText("Giá bán (VD: 100000)");
        txtPriceSuv.setStyle("-fx-background-color: #FCE4EC; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
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

        txtCostSuv = new TextField();
        txtCostSuv.setPromptText("Chi phí vật tư (VD: 25000)");
        txtCostSuv.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
        txtCostSuv.visibleProperty().bind(chkSuv.selectedProperty());
        txtCostSuv.managedProperty().bind(chkSuv.selectedProperty());
        txtCostSuv.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtCostSuv.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtCostSuv.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtCostSuv);

        HBox suvBox = new HBox(12);
        suvBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtPriceSuv, Priority.ALWAYS);
        HBox.setHgrow(txtCostSuv, Priority.ALWAYS);
        suvBox.getChildren().addAll(chkSuv, txtPriceSuv, txtCostSuv);
        if (isEdit && existingService != null && existingService.getPriceSuv() > 0) {
            chkSuv.setSelected(true);
            txtPriceSuv.setText(String.format("%.0f", existingService.getPriceSuv()));
            txtCostSuv.setText(String.format("%.0f", existingService.getCostPriceSuv() > 0 ? existingService.getCostPriceSuv() : existingService.getCostPrice()));
        } else {
            chkSuv.setSelected(false);
            txtCostSuv.setText("0");
        }
        
        // Price & Cost for MPV
        chkMpv = new CheckBox("MPV");
        chkMpv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90;");
        txtPriceMpv = new TextField();
        txtPriceMpv.setPromptText("Giá bán (VD: 105000)");
        txtPriceMpv.setStyle("-fx-background-color: #E0F7FA; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
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

        txtCostMpv = new TextField();
        txtCostMpv.setPromptText("Chi phí vật tư (VD: 28000)");
        txtCostMpv.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
        txtCostMpv.visibleProperty().bind(chkMpv.selectedProperty());
        txtCostMpv.managedProperty().bind(chkMpv.selectedProperty());
        txtCostMpv.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtCostMpv.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtCostMpv.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtCostMpv);

        HBox mpvBox = new HBox(12);
        mpvBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtPriceMpv, Priority.ALWAYS);
        HBox.setHgrow(txtCostMpv, Priority.ALWAYS);
        mpvBox.getChildren().addAll(chkMpv, txtPriceMpv, txtCostMpv);
        if (isEdit && existingService != null && existingService.getPriceMpv() > 0) {
            chkMpv.setSelected(true);
            txtPriceMpv.setText(String.format("%.0f", existingService.getPriceMpv()));
            txtCostMpv.setText(String.format("%.0f", existingService.getCostPriceMpv() > 0 ? existingService.getCostPriceMpv() : existingService.getCostPrice()));
        } else {
            chkMpv.setSelected(false);
            txtCostMpv.setText("0");
        }
        
        // Price & Cost for Pickup
        chkPickup = new CheckBox("Pickup");
        chkPickup.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90;");
        txtPricePickup = new TextField();
        txtPricePickup.setPromptText("Giá bán (VD: 110000)");
        txtPricePickup.setStyle("-fx-background-color: #F3E5F5; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
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

        txtCostPickup = new TextField();
        txtCostPickup.setPromptText("Chi phí vật tư (VD: 30000)");
        txtCostPickup.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px;");
        txtCostPickup.visibleProperty().bind(chkPickup.selectedProperty());
        txtCostPickup.managedProperty().bind(chkPickup.selectedProperty());
        txtCostPickup.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = txtCostPickup.getText().trim();
                if (!text.isEmpty()) {
                    String clean = text.replaceAll("[^\\d]", "");
                    if (!text.equals(clean)) {
                        txtCostPickup.setText(clean);
                    }
                }
            }
        });
        UIUtils.setupIMEFix(txtCostPickup);

        HBox pickupBox = new HBox(12);
        pickupBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtPricePickup, Priority.ALWAYS);
        HBox.setHgrow(txtCostPickup, Priority.ALWAYS);
        pickupBox.getChildren().addAll(chkPickup, txtPricePickup, txtCostPickup);
        if (isEdit && existingService != null && existingService.getPricePickup() > 0) {
            chkPickup.setSelected(true);
            txtPricePickup.setText(String.format("%.0f", existingService.getPricePickup()));
            txtCostPickup.setText(String.format("%.0f", existingService.getCostPricePickup() > 0 ? existingService.getCostPricePickup() : existingService.getCostPrice()));
        } else {
            chkPickup.setSelected(false);
            txtCostPickup.setText("0");
        }
        
        priceListContainer.getChildren().addAll(miniBox, sedanBox, cuvBox, suvBox, mpvBox, pickupBox);
        
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
        grid.add(lblCategory, 0, row++);
        grid.add(cbCategory, 0, row++);
        grid.add(lblLinkedProduct, 0, row++);
        grid.add(linkedProductSearchBox, 0, row++);
        grid.add(lblLinkedQty, 0, row++);
        grid.add(txtLinkedQty, 0, row++);
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
            
            if (!chkMini.isSelected() && !chkSedan.isSelected() && !chkCuv.isSelected() &&
                !chkSuv.isSelected() && !chkMpv.isSelected() && !chkPickup.isSelected()) {
                showAlert("Cảnh báo", "Vui lòng chọn ít nhất một loại xe áp dụng!", Alert.AlertType.WARNING);
                return;
            }
            
            double priceMini = 0;
            double costMini = 0;
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
                String costVal = txtCostMini.getText().trim().replaceAll("[^\\d.]", "");
                if (!costVal.isEmpty()) {
                    try { costMini = Double.parseDouble(costVal); } catch (Exception ex) {}
                }
            }
            
            double priceSedan = 0;
            double costSedan = 0;
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
                String costVal = txtCostSedan.getText().trim().replaceAll("[^\\d.]", "");
                if (!costVal.isEmpty()) {
                    try { costSedan = Double.parseDouble(costVal); } catch (Exception ex) {}
                }
            }
            
            double priceCuv = 0;
            double costCuv = 0;
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
                String costVal = txtCostCuv.getText().trim().replaceAll("[^\\d.]", "");
                if (!costVal.isEmpty()) {
                    try { costCuv = Double.parseDouble(costVal); } catch (Exception ex) {}
                }
            }
            
            double priceSuv = 0;
            double costSuv = 0;
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
                String costVal = txtCostSuv.getText().trim().replaceAll("[^\\d.]", "");
                if (!costVal.isEmpty()) {
                    try { costSuv = Double.parseDouble(costVal); } catch (Exception ex) {}
                }
            }
            
            double priceMpv = 0;
            double costMpv = 0;
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
                String costVal = txtCostMpv.getText().trim().replaceAll("[^\\d.]", "");
                if (!costVal.isEmpty()) {
                    try { costMpv = Double.parseDouble(costVal); } catch (Exception ex) {}
                }
            }
            
            double pricePickup = 0;
            double costPickup = 0;
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
                String costVal = txtCostPickup.getText().trim().replaceAll("[^\\d.]", "");
                if (!costVal.isEmpty()) {
                    try { costPickup = Double.parseDouble(costVal); } catch (Exception ex) {}
                }
            }
            
            try {
                String name = txtName.getText().trim();
                String desc = txtDesc.getText().trim();
                String category = cbCategory.getValue() != null ? cbCategory.getValue().trim() : "rửa xe";
                double defaultCostPrice = costSedan > 0 ? costSedan : (costMini > 0 ? costMini : (costCuv > 0 ? costCuv : (costSuv > 0 ? costSuv : (costMpv > 0 ? costMpv : costPickup))));
                
                Integer linkedProductId = null;
                double linkedProductQty = 0.0;
                if (cbLinkedProduct.getValue() != null) {
                    linkedProductId = cbLinkedProduct.getValue().getId();
                }
                if (linkedProductId != null) {
                    try {
                        linkedProductQty = Double.parseDouble(txtLinkedQty.getText().trim());
                    } catch (Exception ex) {
                        showAlert("Lỗi", "Số lượng định mức không hợp lệ!", Alert.AlertType.ERROR);
                        return;
                    }
                }
                
                ServiceService serviceService = new ServiceService();
                boolean success;
                
                if (isEdit) {
                    success = serviceService.updateService(serviceId, name, desc, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, costMini, costSedan, costCuv, costSuv, costMpv, costPickup, category, defaultCostPrice, linkedProductId, linkedProductQty);
                } else {
                    success = serviceService.addService(name, desc, priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup, costMini, costSedan, costCuv, costSuv, costMpv, costPickup, category, defaultCostPrice, linkedProductId, linkedProductQty);
                }
                
                if (success) {
                    showAlert("Thành công", 
                             isEdit ? "Cập nhật dịch vụ thành công!" : "Thêm dịch vụ mới thành công!", 
                             Alert.AlertType.INFORMATION);
                    stage.close();
                    if (onSave != null) {
                        javafx.application.Platform.runLater(onSave);
                    }
                } else {
                    showAlert("Lỗi", "Không thể lưu dịch vụ! Kiểm tra console để xem chi tiết.", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                showAlert("Lỗi", "Đã xảy ra lỗi khi lưu dịch vụ: " + ex.getMessage(), Alert.AlertType.ERROR);
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
        if (stage != null && stage.isShowing()) {
            alert.initOwner(stage);
        } else if (MainUI.getMainStage() != null) {
            alert.initOwner(MainUI.getMainStage());
        }
        util.AlertHelper.applyTimesNewRomanFont(alert);
        alert.showAndWait();
    }

    private static class ProductWrapper {
        private final Integer id;
        private final String display;
        
        public ProductWrapper(Integer id, String display) {
            this.id = id;
            this.display = display;
        }
        
        public Integer getId() { return id; }
        
        @Override
        public String toString() {
            return display;
        }
    }
}
