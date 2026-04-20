package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.InvoiceService;
import service.ServiceService;
import service.PackageService;
import service.ProductService;
import service.InvoiceItemService;
import model.Service;
import model.Package;
import model.Product;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateInvoiceForm {
    
    private Stage stage;
    private VBox selectedServicesBox;
    private VBox selectedPackagesBox;
    private VBox selectedProductsBox;
    private Label totalLabel;
    private double totalAmount = 0;
    private ToggleGroup carTypeGroup;
    private VBox servicesContainer;
    private VBox packagesContainer;
    private VBox currentServiceContent;
    private TextField txtName;
    private TextField txtPhone;
    private TextField txtPlate;
    private Runnable onInvoiceCreated;
    
    // Track selected items for saving to database
    private List<Map<String, Object>> selectedServices = new ArrayList<>();
    private List<Map<String, Object>> selectedPackages = new ArrayList<>();
    private List<Map<String, Object>> selectedProducts = new ArrayList<>();
    
    public CreateInvoiceForm() {}
    
    public CreateInvoiceForm(Runnable onInvoiceCreated) {
        this.onInvoiceCreated = onInvoiceCreated;
    }
    
    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tạo Hóa Đơn Mới");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");
        
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");
        
        // Header
        Label title = new Label("📝 Tạo Hóa Đơn Mới");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        // Customer Info Section
        VBox customerSection = createCustomerInfoSection();
        
        // Services Section (combined with packages)
        VBox servicesSection = createCombinedServicesSection();
        
        // Products Section
        VBox productsSection = createProductsSection();
        
        // Summary Section
        VBox summarySection = createSummarySection();
        
        // Action Buttons
        HBox actionButtons = createActionButtons();
        
        mainContent.getChildren().addAll(
            title,
            customerSection,
            servicesSection,
            productsSection,
            summarySection,
            actionButtons
        );
        
        scrollPane.setContent(mainContent);
        
        Scene scene = new Scene(scrollPane, 1000, 700);
        stage.setScene(scene);
        stage.show();
    }
    
    private VBox createCustomerInfoSection() {
        VBox section = new VBox(15);
        section.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 25;"
        );
        
        Label sectionTitle = new Label("Thông Tin Khách Hàng");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        // Name field - Create completely independent TextField
        Label lblName = new Label("Tên khách hàng *");
        lblName.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 500;");
        txtName = new TextField("");
        txtName.setPromptText("Nhập tên khách hàng");
        txtName.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtName.setPrefWidth(300);
        
        // Phone field - Create completely independent TextField
        Label lblPhone = new Label("Số điện thoại *");
        lblPhone.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 500;");
        txtPhone = new TextField("");
        txtPhone.setPromptText("Nhập số điện thoại");
        txtPhone.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPhone.setPrefWidth(300);
        
        // License plate field - Create completely independent TextField
        Label lblPlate = new Label("Biển số xe *");
        lblPlate.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 500;");
        txtPlate = new TextField("");
        txtPlate.setPromptText("Nhập biển số xe");
        txtPlate.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtPlate.setPrefWidth(300);
        
        // Car type selection
        Label lblCarType = new Label("Loại xe *");
        lblCarType.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 500;");
        
        HBox carTypeBox = new HBox(12);
        carTypeGroup = new ToggleGroup();
        
        RadioButton rbSedan = new RadioButton("Sedan");
        rbSedan.setToggleGroup(carTypeGroup);
        rbSedan.setSelected(true);
        rbSedan.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #424242;"
        );
        
        RadioButton rbSUV = new RadioButton("SUV");
        rbSUV.setToggleGroup(carTypeGroup);
        rbSUV.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #424242;"
        );
        
        // Add listener to update service prices when car type changes
        carTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (servicesContainer != null) {
                updateServicePrices();
            }
        });
        
        carTypeBox.getChildren().addAll(rbSedan, rbSUV);
        
        grid.add(lblName, 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(lblPhone, 0, 1);
        grid.add(txtPhone, 1, 1);
        grid.add(lblPlate, 0, 2);
        grid.add(txtPlate, 1, 2);
        grid.add(lblCarType, 0, 3);
        grid.add(carTypeBox, 1, 3);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createCombinedServicesSection() {
        VBox section = new VBox(15);
        section.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 25;"
        );
        
        Label sectionTitle = new Label("Chọn Dịch Vụ");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        // Tab buttons
        HBox tabButtons = new HBox(8);
        Button btnServices = createTabButton("Dịch Vụ Lẻ", true);
        Button btnPackages = createTabButton("Gói Dịch Vụ", false);
        tabButtons.getChildren().addAll(btnServices, btnPackages);
        
        // Content container
        currentServiceContent = new VBox(15);
        
        // Initialize services content - Load from database
        servicesContainer = new VBox(10);
        loadServicesFromDatabase();
        
        // Initialize packages content - Load from database
        packagesContainer = new VBox(10);
        loadPackagesFromDatabase();
        
        // Set initial content to services
        currentServiceContent.getChildren().add(servicesContainer);
        
        // Tab button actions
        btnServices.setOnAction(e -> {
            setActiveTab(btnServices, btnPackages);
            currentServiceContent.getChildren().clear();
            currentServiceContent.getChildren().add(servicesContainer);
        });
        
        btnPackages.setOnAction(e -> {
            setActiveTab(btnPackages, btnServices);
            currentServiceContent.getChildren().clear();
            currentServiceContent.getChildren().add(packagesContainer);
        });
        
        // Selected items display
        Label selectedTitle = new Label("Đã chọn:");
        selectedTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #757575;");
        
        VBox selectedContainer = new VBox(10);
        
        // Services selected
        Label servicesSelectedTitle = new Label("Dịch vụ:");
        servicesSelectedTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #757575;");
        
        selectedServicesBox = new VBox(8);
        selectedServicesBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 15;"
        );
        selectedServicesBox.setMinHeight(50);
        
        Label emptyServicesLabel = new Label("Chưa chọn dịch vụ nào");
        emptyServicesLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9e9e9e;");
        selectedServicesBox.getChildren().add(emptyServicesLabel);
        
        // Packages selected
        Label packagesSelectedTitle = new Label("Gói dịch vụ:");
        packagesSelectedTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #757575;");
        
        selectedPackagesBox = new VBox(8);
        selectedPackagesBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 15;"
        );
        selectedPackagesBox.setMinHeight(50);
        
        Label emptyPackagesLabel = new Label("Chưa chọn gói nào");
        emptyPackagesLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9e9e9e;");
        selectedPackagesBox.getChildren().add(emptyPackagesLabel);
        
        selectedContainer.getChildren().addAll(
            servicesSelectedTitle, selectedServicesBox,
            packagesSelectedTitle, selectedPackagesBox
        );
        
        section.getChildren().addAll(sectionTitle, tabButtons, currentServiceContent, selectedTitle, selectedContainer);
        return section;
    }
    
    private Button createTabButton(String text, boolean active) {
        Button btn = new Button(text);
        if (active) {
            btn.setStyle(
                "-fx-background-color: #2196F3;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 10px 20px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: #f5f5f5;" +
                "-fx-text-fill: #616161;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 10px 20px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;"
            );
        }
        return btn;
    }
    
    private void setActiveTab(Button activeBtn, Button inactiveBtn) {
        activeBtn.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;"
        );
        
        inactiveBtn.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #616161;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;"
        );
    }
    
    private VBox createPackagesSection() {
        // This method is no longer used as packages are now integrated into the combined section
        return new VBox();
    }
    
    private VBox createProductsSection() {
        VBox section = new VBox(15);
        section.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 25;"
        );
        
        Label sectionTitle = new Label("Chọn Sản Phẩm");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        // Available products - Load from database
        VBox productsBox = new VBox(10);
        loadProductsFromDatabase(productsBox);
        
        // Selected products display
        Label selectedTitle = new Label("Sản phẩm đã chọn:");
        selectedTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #757575;");
        
        selectedProductsBox = new VBox(8);
        selectedProductsBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 15;"
        );
        selectedProductsBox.setMinHeight(60);
        
        Label emptyLabel = new Label("Chưa chọn sản phẩm nào");
        emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9e9e9e;");
        selectedProductsBox.getChildren().add(emptyLabel);
        
        section.getChildren().addAll(sectionTitle, productsBox, selectedTitle, selectedProductsBox);
        return section;
    }
    
    private HBox createServiceItem(String name, String priceSedan, String priceSUV) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;"
        );
        
        // Store prices as user data
        item.setUserData(new String[]{name, priceSedan, priceSUV});
        
        VBox info = new VBox(4);
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Label lblPrice = new Label(priceSedan); // Default to sedan price
        lblPrice.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        
        info.getChildren().addAll(lblName, lblPrice);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnAdd = new Button("+ Thêm");
        btnAdd.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 8px 16px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        
        btnAdd.setOnAction(e -> {
            String currentPrice = getCurrentPrice(priceSedan, priceSUV);
            addSelectedService(name, currentPrice);
        });
        
        item.getChildren().addAll(info, spacer, btnAdd);
        return item;
    }
    
    private void updateServicePrices() {
        for (var node : servicesContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox item = (HBox) node;
                String[] data = (String[]) item.getUserData();
                if (data != null) {
                    String name = data[0];
                    String priceSedan = data[1];
                    String priceSUV = data[2];
                    
                    // Find the price label and update it
                    VBox info = (VBox) item.getChildren().get(0);
                    Label priceLabel = (Label) info.getChildren().get(1);
                    
                    String currentPrice = getCurrentPrice(priceSedan, priceSUV);
                    priceLabel.setText(currentPrice);
                }
            }
        }
    }
    
    private String getCurrentPrice(String priceSedan, String priceSUV) {
        RadioButton selectedRadio = (RadioButton) carTypeGroup.getSelectedToggle();
        if (selectedRadio != null && selectedRadio.getText().equals("SUV")) {
            return priceSUV;
        }
        return priceSedan;
    }
    
    private HBox createPackageItem(String name, String description, String price) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;"
        );
        
        VBox info = new VBox(4);
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Label lblDesc = new Label(description);
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        
        Label lblPrice = new Label(price);
        lblPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        
        info.getChildren().addAll(lblName, lblDesc, lblPrice);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnAdd = new Button("+ Thêm");
        btnAdd.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 8px 16px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        
        btnAdd.setOnAction(e -> {
            addSelectedPackage(name, price);
        });
        
        item.getChildren().addAll(info, spacer, btnAdd);
        return item;
    }
    
    private HBox createProductItem(String name, String price) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;"
        );
        
        VBox info = new VBox(4);
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Label lblPrice = new Label(price);
        lblPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        
        info.getChildren().addAll(lblName, lblPrice);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Spinner<Integer> spinner = new Spinner<>(1, 99, 1);
        spinner.setPrefWidth(80);
        spinner.setStyle("-fx-font-size: 13px;");
        
        Button btnAdd = new Button("+ Thêm");
        btnAdd.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 8px 16px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        
        btnAdd.setOnAction(e -> {
            int quantity = spinner.getValue();
            addSelectedProduct(name, price, quantity);
        });
        
        item.getChildren().addAll(info, spacer, spinner, btnAdd);
        return item;
    }
    
    private void addSelectedService(String name, String price) {
        if (selectedServicesBox.getChildren().get(0) instanceof Label) {
            selectedServicesBox.getChildren().clear();
        }
        
        HBox selectedItem = createSelectedItem(name, price);
        selectedServicesBox.getChildren().add(selectedItem);
        
        double unitPrice = parsePrice(price);
        updateTotal(unitPrice);
        
        // Track for database
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("price", unitPrice);
        item.put("hbox", selectedItem);
        selectedServices.add(item);
    }
    
    private void addSelectedPackage(String name, String price) {
        if (selectedPackagesBox.getChildren().get(0) instanceof Label) {
            selectedPackagesBox.getChildren().clear();
        }
        
        HBox selectedItem = createSelectedItem(name, price);
        selectedPackagesBox.getChildren().add(selectedItem);
        
        double unitPrice = parsePrice(price);
        updateTotal(unitPrice);
        
        // Track for database
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("price", unitPrice);
        item.put("hbox", selectedItem);
        selectedPackages.add(item);
    }
    
    private void addSelectedProduct(String name, String price, int quantity) {
        if (selectedProductsBox.getChildren().get(0) instanceof Label) {
            selectedProductsBox.getChildren().clear();
        }
        
        String displayText = name + " (x" + quantity + ")";
        double unitPrice = parsePrice(price);
        double itemTotal = unitPrice * quantity;
        String displayPrice = formatPrice(itemTotal);
        
        HBox selectedItem = createSelectedItem(displayText, displayPrice);
        selectedProductsBox.getChildren().add(selectedItem);
        
        updateTotal(itemTotal);
        
        // Track for database
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("unitPrice", unitPrice);
        item.put("quantity", quantity);
        item.put("totalPrice", itemTotal);
        item.put("hbox", selectedItem);
        selectedProducts.add(item);
    }
    
    private HBox createSelectedItem(String name, String price) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8));
        item.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;"
        );
        
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 13px; -fx-text-fill: #212121;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label lblPrice = new Label(price);
        lblPrice.setStyle("-fx-font-size: 13px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        
        Button btnRemove = new Button("✕");
        btnRemove.setStyle(
            "-fx-background-color: #ffebee;" +
            "-fx-text-fill: #f44336;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 4px 8px;" +
            "-fx-background-radius: 4;" +
            "-fx-cursor: hand;"
        );
        
        btnRemove.setOnAction(e -> {
            VBox parent = (VBox) item.getParent();
            parent.getChildren().remove(item);
            updateTotal(-parsePrice(price));
            
            // Remove from tracking lists
            if (parent == selectedServicesBox) {
                selectedServices.removeIf(s -> s.get("hbox") == item);
            } else if (parent == selectedPackagesBox) {
                selectedPackages.removeIf(p -> p.get("hbox") == item);
            } else if (parent == selectedProductsBox) {
                selectedProducts.removeIf(p -> p.get("hbox") == item);
            }
            
            if (parent.getChildren().isEmpty()) {
                Label emptyLabel = new Label("Chưa chọn");
                emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9e9e9e;");
                parent.getChildren().add(emptyLabel);
            }
        });
        
        item.getChildren().addAll(lblName, spacer, lblPrice, btnRemove);
        return item;
    }
    
    private VBox createSummarySection() {
        VBox section = new VBox(15);
        section.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #2196F3;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 25;"
        );
        
        Label sectionTitle = new Label("Tổng Cộng");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        totalLabel = new Label("0đ");
        totalLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        section.getChildren().addAll(sectionTitle, totalLabel);
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
        
        Button btnCreate = new Button("Tạo Hóa Đơn");
        btnCreate.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 30px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnCreate.setOnAction(e -> {
            // Validate required fields
            if (txtName.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập tên khách hàng!");
                alert.showAndWait();
                return;
            }
            
            if (txtPhone.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập số điện thoại!");
                alert.showAndWait();
                return;
            }
            
            if (txtPlate.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập biển số xe!");
                alert.showAndWait();
                return;
            }
            
            if (totalAmount <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng chọn ít nhất một dịch vụ hoặc sản phẩm!");
                alert.showAndWait();
                return;
            }
            
            // Get vehicle type
            RadioButton selectedRadio = (RadioButton) carTypeGroup.getSelectedToggle();
            String vehicleType = selectedRadio != null ? selectedRadio.getText() : "sedan";
            
            // Save invoice to database
            InvoiceService invoiceService = new InvoiceService();
            int invoiceId = invoiceService.addInvoice(
                txtName.getText().trim(),
                txtPhone.getText().trim(),
                txtPlate.getText().trim(),
                vehicleType.toLowerCase(),
                totalAmount,
                0,
                totalAmount,
                ""
            );
            
            if (invoiceId > 0) {
                // Save invoice items
                InvoiceItemService itemService = new InvoiceItemService();
                
                // Save services
                for (Map<String, Object> service : selectedServices) {
                    itemService.addInvoiceItem(
                        invoiceId,
                        "service",
                        (String) service.get("name"),
                        1,
                        (Double) service.get("price"),
                        (Double) service.get("price")
                    );
                }
                
                // Save packages
                for (Map<String, Object> pkg : selectedPackages) {
                    itemService.addInvoiceItem(
                        invoiceId,
                        "package",
                        (String) pkg.get("name"),
                        1,
                        (Double) pkg.get("price"),
                        (Double) pkg.get("price")
                    );
                }
                
                // Save products
                for (Map<String, Object> product : selectedProducts) {
                    itemService.addInvoiceItem(
                        invoiceId,
                        "product",
                        (String) product.get("name"),
                        (Integer) product.get("quantity"),
                        (Double) product.get("unitPrice"),
                        (Double) product.get("totalPrice")
                    );
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Tạo hóa đơn thành công!");
                alert.showAndWait();
                
                // Call callback to refresh invoice list
                if (onInvoiceCreated != null) {
                    onInvoiceCreated.run();
                }
                
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Không thể tạo hóa đơn!");
                alert.showAndWait();
            }
        });
        
        buttons.getChildren().addAll(btnCancel, btnCreate);
        return buttons;
    }
    
    private void loadServicesFromDatabase() {
        ServiceService serviceService = new ServiceService();
        List<Service> services = serviceService.getAllServices();
        
        if (services.isEmpty()) {
            Label emptyLabel = new Label("Chưa có dịch vụ nào");
            emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9e9e9e;");
            servicesContainer.getChildren().add(emptyLabel);
        } else {
            // Wrap in ScrollPane for many services
            VBox servicesList = new VBox(10);
            for (Service service : services) {
                String priceSedan = String.format("%,.0fđ", service.getPriceSmall());
                String priceSUV = String.format("%,.0fđ", service.getPriceLarge());
                servicesList.getChildren().add(
                    createServiceItem(service.getName(), priceSedan, priceSUV)
                );
            }
            
            ScrollPane scrollPane = new ScrollPane(servicesList);
            scrollPane.setFitToWidth(true);
            scrollPane.setMaxHeight(300);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            servicesContainer.getChildren().add(scrollPane);
        }
    }
    
    private void loadPackagesFromDatabase() {
        PackageService packageService = new PackageService();
        List<Package> packages = packageService.getAllPackages();
        
        if (packages.isEmpty()) {
            Label emptyLabel = new Label("Chưa có gói dịch vụ nào");
            emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9e9e9e;");
            packagesContainer.getChildren().add(emptyLabel);
        } else {
            // Wrap in ScrollPane for many packages
            VBox packagesList = new VBox(10);
            for (Package pkg : packages) {
                String price = String.format("%,.0fđ", pkg.getPrice());
                packagesList.getChildren().add(
                    createPackageItem(pkg.getName(), pkg.getDescription(), price)
                );
            }
            
            ScrollPane scrollPane = new ScrollPane(packagesList);
            scrollPane.setFitToWidth(true);
            scrollPane.setMaxHeight(300);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            packagesContainer.getChildren().add(scrollPane);
        }
    }
    
    private void loadProductsFromDatabase(VBox productsBox) {
        ProductService productService = new ProductService();
        List<Product> products = productService.getAllProducts();
        
        if (products.isEmpty()) {
            Label emptyLabel = new Label("Chưa có sản phẩm nào");
            emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9e9e9e;");
            productsBox.getChildren().add(emptyLabel);
        } else {
            // Wrap in ScrollPane for many products
            VBox productsList = new VBox(10);
            for (Product product : products) {
                String price = String.format("%,.0fđ", product.getPrice());
                productsList.getChildren().add(
                    createProductItem(product.getName(), price)
                );
            }
            
            ScrollPane scrollPane = new ScrollPane(productsList);
            scrollPane.setFitToWidth(true);
            scrollPane.setMaxHeight(300);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            productsBox.getChildren().add(scrollPane);
        }
    }
    
    private void updateTotal(double amount) {
        totalAmount += amount;
        totalLabel.setText(formatPrice(totalAmount));
    }
    
    private double parsePrice(String price) {
        return Double.parseDouble(price.replace("đ", "").replace(",", "").trim());
    }
    
    private String formatPrice(double price) {
        return String.format("%,.0fđ", price);
    }
}
