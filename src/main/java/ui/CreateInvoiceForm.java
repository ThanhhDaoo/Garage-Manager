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
    private Label subtotalLabel;
    private Label vatLabel;
    private Label discountTotalLabel;
    private double totalAmount = 0;
    private ToggleGroup carTypeGroup;
    private VBox servicesContainer;
    private VBox packagesContainer;
    private VBox currentServiceContent;
    private TextField txtName;
    private TextField txtPhone;
    private TextField txtPlate;
    private TextField txtAddress;
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
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
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

        // Address field - Create completely independent TextField
        Label lblAddress = new Label("Địa chỉ");
        lblAddress.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 500;");
        txtAddress = new TextField("");
        txtAddress.setPromptText("Nhập địa chỉ khách hàng");
        txtAddress.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 12px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;"
        );
        txtAddress.setPrefWidth(300);
        
        // Car type selection
        Label lblCarType = new Label("Loại xe *");
        lblCarType.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 500;");
        
        HBox carTypeBox = new HBox(12);
        carTypeGroup = new ToggleGroup();
        
        RadioButton rbMini = new RadioButton("Mini");
        rbMini.setToggleGroup(carTypeGroup);
        rbMini.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        RadioButton rbSedan = new RadioButton("Sedan");
        rbSedan.setToggleGroup(carTypeGroup);
        rbSedan.setSelected(true);
        rbSedan.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        RadioButton rbCUV = new RadioButton("CUV");
        rbCUV.setToggleGroup(carTypeGroup);
        rbCUV.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        RadioButton rbSUV = new RadioButton("SUV");
        rbSUV.setToggleGroup(carTypeGroup);
        rbSUV.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        RadioButton rbPickup = new RadioButton("Pickup");
        rbPickup.setToggleGroup(carTypeGroup);
        rbPickup.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        
        // Add listener to update service prices when car type changes
        carTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (servicesContainer != null) {
                updateServicePrices();
            }
        });
        
        carTypeBox.getChildren().addAll(rbMini, rbSedan, rbCUV, rbSUV, rbPickup);
        
        grid.add(lblName, 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(lblPhone, 0, 1);
        grid.add(txtPhone, 1, 1);
        grid.add(lblPlate, 0, 2);
        grid.add(txtPlate, 1, 2);
        grid.add(lblAddress, 0, 3);
        grid.add(txtAddress, 1, 3);
        grid.add(lblCarType, 0, 4);
        grid.add(carTypeBox, 1, 4);
        
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
        
        // Search bar
        TextField txtSearchService = new TextField();
        txtSearchService.setPromptText("🔍 Tìm kiếm dịch vụ lẻ hoặc gói dịch vụ...");
        txtSearchService.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        txtSearchService.textProperty().addListener((obs, oldVal, newVal) -> {
            filterServicesAndPackages(newVal);
        });
        
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
        
        section.getChildren().addAll(sectionTitle, tabButtons, txtSearchService, currentServiceContent, selectedTitle, selectedContainer);
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
        
        // Search bar
        TextField txtSearchProduct = new TextField();
        txtSearchProduct.setPromptText("🔍 Tìm kiếm sản phẩm...");
        txtSearchProduct.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 13px;"
        );
        
        // Available products - Load from database
        VBox productsBox = new VBox(10);
        loadProductsFromDatabase(productsBox);
        
        txtSearchProduct.textProperty().addListener((obs, oldVal, newVal) -> {
            filterProducts(productsBox, newVal);
        });
        
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
        
        section.getChildren().addAll(sectionTitle, txtSearchProduct, productsBox, selectedTitle, selectedProductsBox);
        return section;
    }
    
    private HBox createServiceItem(String name, String priceMini, String priceSedan, String priceCuv, String priceSuv, String pricePickup) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;"
        );
        
        // Store all prices as user data
        item.setUserData(new String[]{name, priceMini, priceSedan, priceCuv, priceSuv, pricePickup});
        
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
            String currentPrice = getCurrentPrice(priceMini, priceSedan, priceCuv, priceSuv, pricePickup);
            addSelectedService(name, currentPrice);
        });
        
        item.getChildren().addAll(info, spacer, btnAdd);
        return item;
    }
    
    private void updateServicePrices() {
        // Update services
        for (var node : servicesContainer.getChildren()) {
            if (node instanceof ScrollPane) {
                ScrollPane sp = (ScrollPane) node;
                VBox servicesList = (VBox) sp.getContent();
                for (var serviceNode : servicesList.getChildren()) {
                    if (serviceNode instanceof HBox) {
                        updateItemPrice((HBox) serviceNode);
                    }
                }
            }
        }
        
        // Update packages
        for (var node : packagesContainer.getChildren()) {
            if (node instanceof ScrollPane) {
                ScrollPane sp = (ScrollPane) node;
                VBox packagesList = (VBox) sp.getContent();
                for (var packageNode : packagesList.getChildren()) {
                    if (packageNode instanceof HBox) {
                        updateItemPrice((HBox) packageNode);
                    }
                }
            }
        }
    }
    
    private void updateItemPrice(HBox item) {
        String[] data = (String[]) item.getUserData();
        if (data != null && data.length >= 6) {
            // For services: data[0]=name, data[1-5]=prices
            // For packages: data[0]=name, data[1]=description, data[2-6]=prices
            String priceMini, priceSedan, priceCuv, priceSuv, pricePickup;
            
            if (data.length == 6) {
                // Service format
                priceMini = data[1];
                priceSedan = data[2];
                priceCuv = data[3];
                priceSuv = data[4];
                pricePickup = data[5];
            } else {
                // Package format (has description)
                priceMini = data[2];
                priceSedan = data[3];
                priceCuv = data[4];
                priceSuv = data[5];
                pricePickup = data[6];
            }
            
            // Find the price label and update it
            VBox info = (VBox) item.getChildren().get(0);
            Label priceLabel = (Label) info.getChildren().get(info.getChildren().size() - 1);
            
            String currentPrice = getCurrentPrice(priceMini, priceSedan, priceCuv, priceSuv, pricePickup);
            priceLabel.setText(currentPrice);
        }
    }
    
    private String getCurrentPrice(String priceMini, String priceSedan, String priceCuv, String priceSuv, String pricePickup) {
        RadioButton selectedRadio = (RadioButton) carTypeGroup.getSelectedToggle();
        if (selectedRadio != null) {
            String vehicleType = selectedRadio.getText();
            switch (vehicleType) {
                case "Mini": return priceMini;
                case "Sedan": return priceSedan;
                case "CUV": return priceCuv;
                case "SUV": return priceSuv;
                case "Pickup": return pricePickup;
            }
        }
        return priceSedan; // Default
    }
    
    private HBox createPackageItem(String name, String description, String priceMini, String priceSedan, String priceCuv, String priceSuv, String pricePickup) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;"
        );
        
        // Store all prices as user data
        item.setUserData(new String[]{name, description, priceMini, priceSedan, priceCuv, priceSuv, pricePickup});
        
        VBox info = new VBox(4);
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Label lblDesc = new Label(description);
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        
        Label lblPrice = new Label(priceSedan); // Default to sedan price
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
            String currentPrice = getCurrentPrice(priceMini, priceSedan, priceCuv, priceSuv, pricePickup);
            addSelectedPackage(name, currentPrice);
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
        
        // Store product name as user data for filtering
        item.setUserData(name);
        
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
        
        double unitPrice = parsePrice(price);
        VBox selectedItem = createSelectedItem(name, price, unitPrice);
        selectedServicesBox.getChildren().add(selectedItem);
        
        // Track for database
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("price", unitPrice);
        item.put("hbox", selectedItem);
        selectedServices.add(item);
        
        recalculateTotal();
    }
    
    private void addSelectedPackage(String name, String price) {
        if (selectedPackagesBox.getChildren().get(0) instanceof Label) {
            selectedPackagesBox.getChildren().clear();
        }
        
        double unitPrice = parsePrice(price);
        VBox selectedItem = createSelectedItem(name, price, unitPrice);
        selectedPackagesBox.getChildren().add(selectedItem);
        
        // Track for database
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("price", unitPrice);
        item.put("hbox", selectedItem);
        selectedPackages.add(item);
        
        recalculateTotal();
    }
    
    private void addSelectedProduct(String name, String price, int quantity) {
        if (selectedProductsBox.getChildren().get(0) instanceof Label) {
            selectedProductsBox.getChildren().clear();
        }
        
        String displayText = name + " (x" + quantity + ")";
        double unitPrice = parsePrice(price);
        double itemTotal = unitPrice * quantity;
        String displayPrice = formatPrice(itemTotal);
        
        VBox selectedItem = createSelectedItem(displayText, displayPrice, itemTotal);
        selectedProductsBox.getChildren().add(selectedItem);
        
        // Track for database
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("unitPrice", unitPrice);
        item.put("quantity", quantity);
        item.put("totalPrice", itemTotal);
        item.put("hbox", selectedItem);
        selectedProducts.add(item);
        
        recalculateTotal();
    }
    
    private VBox createSelectedItem(String name, String price, double basePrice) {
        VBox container = new VBox(4);
        container.setPadding(new Insets(8));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;"
        );
        
        // Store base price for calculations
        container.getProperties().put("basePrice", basePrice);
        
        // === Top row: Name + Discount Combo + Remove ===
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label discLabel = new Label("Giảm:");
        discLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #616161;");
        
        ComboBox<String> itemDiscountCombo = new ComboBox<>();
        itemDiscountCombo.getItems().addAll("0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%");
        itemDiscountCombo.setValue("0%");
        itemDiscountCombo.setPrefWidth(75);
        itemDiscountCombo.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-background-color: #f5f5f5;" +
            "-fx-border-color: #bdbdbd;" +
            "-fx-border-radius: 4;" +
            "-fx-background-radius: 4;"
        );
        itemDiscountCombo.setOnAction(e -> recalculateTotal());
        container.getProperties().put("discountCombo", itemDiscountCombo);
        
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
            VBox parent = (VBox) container.getParent();
            parent.getChildren().remove(container);
            
            // Remove from tracking lists
            if (parent == selectedServicesBox) {
                selectedServices.removeIf(s -> s.get("hbox") == container);
            } else if (parent == selectedPackagesBox) {
                selectedPackages.removeIf(p -> p.get("hbox") == container);
            } else if (parent == selectedProductsBox) {
                selectedProducts.removeIf(p -> p.get("hbox") == container);
            }
            
            if (parent.getChildren().isEmpty()) {
                Label emptyLabel = new Label("Chưa chọn");
                emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9e9e9e;");
                parent.getChildren().add(emptyLabel);
            }
            
            recalculateTotal();
        });
        
        topRow.getChildren().addAll(lblName, spacer, discLabel, itemDiscountCombo, btnRemove);
        
        // === Bottom row: Price breakdown ===
        HBox bottomRow = new HBox(12);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setPadding(new Insets(2, 0, 0, 0));
        
        Label lblPrice = new Label("Giá: " + price);
        lblPrice.setStyle("-fx-font-size: 12px; -fx-text-fill: #616161;");
        
        Label lblDiscAmount = new Label("");
        lblDiscAmount.setStyle("-fx-font-size: 12px; -fx-text-fill: #f44336;");
        container.getProperties().put("discountAmountLabel", lblDiscAmount);
        
        double vatAmount = basePrice * 0.08;
        Label lblVat = new Label("VAT 8%: " + formatPrice(vatAmount));
        lblVat.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");
        container.getProperties().put("vatLabel", lblVat);
        
        Label lblTotal = new Label("= " + formatPrice(basePrice + vatAmount));
        lblTotal.setStyle("-fx-font-size: 12px; -fx-text-fill: #2196F3; -fx-font-weight: 600;");
        container.getProperties().put("totalLabel", lblTotal);
        
        bottomRow.getChildren().addAll(lblPrice, lblDiscAmount, lblVat, lblTotal);
        
        container.getChildren().addAll(topRow, bottomRow);
        return container;
    }
    
    private VBox createSummarySection() {
        VBox section = new VBox(12);
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
        
        // Subtotal row
        HBox subtotalRow = new HBox(10);
        subtotalRow.setAlignment(Pos.CENTER_LEFT);
        Label subtotalText = new Label("Tạm tính:");
        subtotalText.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        subtotalLabel = new Label("0đ");
        subtotalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        subtotalRow.getChildren().addAll(subtotalText, spacer1, subtotalLabel);
        
        // Discount total row
        HBox discountRow = new HBox(10);
        discountRow.setAlignment(Pos.CENTER_LEFT);
        Label discountText = new Label("Tổng giảm giá:");
        discountText.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        discountTotalLabel = new Label("0đ");
        discountTotalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f44336; -fx-font-weight: 600;");
        discountRow.getChildren().addAll(discountText, spacer2, discountTotalLabel);
        
        // VAT total row
        HBox vatRow = new HBox(10);
        vatRow.setAlignment(Pos.CENTER_LEFT);
        Label vatText = new Label("Tổng VAT (8%):");
        vatText.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        vatLabel = new Label("0đ");
        vatLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        vatRow.getChildren().addAll(vatText, spacer3, vatLabel);
        
        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #2196F3;");
        
        // Total row
        HBox totalRow = new HBox(10);
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label totalText = new Label("Thành tiền:");
        totalText.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121;");
        Region spacer4 = new Region();
        HBox.setHgrow(spacer4, Priority.ALWAYS);
        totalLabel = new Label("0đ");
        totalLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        totalRow.getChildren().addAll(totalText, spacer4, totalLabel);
        
        section.getChildren().addAll(sectionTitle, subtotalRow, discountRow, vatRow, separator, totalRow);
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
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập tên khách hàng!");
                alert.showAndWait();
                return;
            }
            
            if (txtPhone.getText().trim().isEmpty()) {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập số điện thoại!");
                alert.showAndWait();
                return;
            }
            
            if (txtPlate.getText().trim().isEmpty()) {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập biển số xe!");
                alert.showAndWait();
                return;
            }
            
            if (totalAmount <= 0) {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn ít nhất một dịch vụ hoặc sản phẩm!");
                alert.showAndWait();
                return;
            }
            
            // Get vehicle type
            RadioButton selectedRadio = (RadioButton) carTypeGroup.getSelectedToggle();
            String vehicleType = selectedRadio != null ? selectedRadio.getText() : "sedan";
            
            // Calculate per-item totals for invoice
            double totalSubtotal = 0;
            double totalDiscountAmount = 0;
            double totalFinalAmount = 0;
            
            for (Map<String, Object> svc : selectedServices) {
                double bp = (Double) svc.get("price");
                int dp = getItemDiscountPercent(svc);
                double da = bp * dp / 100.0;
                double ad = bp - da;
                double vt = ad * 0.08;
                totalSubtotal += bp;
                totalDiscountAmount += da;
                totalFinalAmount += ad + vt;
            }
            for (Map<String, Object> pk : selectedPackages) {
                double bp = (Double) pk.get("price");
                int dp = getItemDiscountPercent(pk);
                double da = bp * dp / 100.0;
                double ad = bp - da;
                double vt = ad * 0.08;
                totalSubtotal += bp;
                totalDiscountAmount += da;
                totalFinalAmount += ad + vt;
            }
            for (Map<String, Object> pr : selectedProducts) {
                double bp = (Double) pr.get("totalPrice");
                int dp = getItemDiscountPercent(pr);
                double da = bp * dp / 100.0;
                double ad = bp - da;
                double vt = ad * 0.08;
                totalSubtotal += bp;
                totalDiscountAmount += da;
                totalFinalAmount += ad + vt;
            }
            
            // Save invoice to database
            InvoiceService invoiceService = new InvoiceService();
            int invoiceId = invoiceService.addInvoice(
                txtName.getText().trim(),
                txtPhone.getText().trim(),
                txtPlate.getText().trim(),
                vehicleType.toLowerCase(),
                txtAddress.getText().trim(),
                totalSubtotal,
                totalDiscountAmount,
                totalFinalAmount,
                ""
            );
            
            if (invoiceId > 0) {
                // Save invoice items
                InvoiceItemService itemService = new InvoiceItemService();
                
                // Save services (totalPrice = price after discount, before VAT)
                for (Map<String, Object> service : selectedServices) {
                    double bp = (Double) service.get("price");
                    int dp = getItemDiscountPercent(service);
                    double afterDisc = bp - (bp * dp / 100.0);
                    itemService.addInvoiceItem(
                        invoiceId,
                        "service",
                        (String) service.get("name"),
                        1,
                        bp,
                        afterDisc
                    );
                }
                
                // Save packages
                for (Map<String, Object> pkg : selectedPackages) {
                    double bp = (Double) pkg.get("price");
                    int dp = getItemDiscountPercent(pkg);
                    double afterDisc = bp - (bp * dp / 100.0);
                    itemService.addInvoiceItem(
                        invoiceId,
                        "package",
                        (String) pkg.get("name"),
                        1,
                        bp,
                        afterDisc
                    );
                }
                
                // Save products
                for (Map<String, Object> product : selectedProducts) {
                    double bp = (Double) product.get("totalPrice");
                    int dp = getItemDiscountPercent(product);
                    double afterDisc = bp - (bp * dp / 100.0);
                    itemService.addInvoiceItem(
                        invoiceId,
                        "product",
                        (String) product.get("name"),
                        (Integer) product.get("quantity"),
                        (Double) product.get("unitPrice"),
                        afterDisc
                    );
                }
                
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.INFORMATION, "Thành công", "Tạo hóa đơn thành công!");
                alert.showAndWait();
                
                // Call callback to refresh invoice list
                if (onInvoiceCreated != null) {
                    onInvoiceCreated.run();
                }
                
                stage.close();
            } else {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo hóa đơn!");
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
                String priceMini = String.format("%,.0fđ", service.getPriceMini());
                String priceSedan = String.format("%,.0fđ", service.getPriceSedan());
                String priceCuv = String.format("%,.0fđ", service.getPriceCuv());
                String priceSuv = String.format("%,.0fđ", service.getPriceSuv());
                String pricePickup = String.format("%,.0fđ", service.getPricePickup());
                servicesList.getChildren().add(
                    createServiceItem(service.getName(), priceMini, priceSedan, priceCuv, priceSuv, pricePickup)
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
                String priceMini = String.format("%,.0fđ", pkg.getPriceMini());
                String priceSedan = String.format("%,.0fđ", pkg.getPriceSedan());
                String priceCuv = String.format("%,.0fđ", pkg.getPriceCuv());
                String priceSuv = String.format("%,.0fđ", pkg.getPriceSuv());
                String pricePickup = String.format("%,.0fđ", pkg.getPricePickup());
                packagesList.getChildren().add(
                    createPackageItem(pkg.getName(), pkg.getDescription(), priceMini, priceSedan, priceCuv, priceSuv, pricePickup)
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
    
    @SuppressWarnings("unchecked")
    private int getItemDiscountPercent(Map<String, Object> item) {
        javafx.scene.Node node = (javafx.scene.Node) item.get("hbox");
        if (node != null) {
            ComboBox<String> combo = (ComboBox<String>) node.getProperties().get("discountCombo");
            if (combo != null && combo.getValue() != null) {
                return Integer.parseInt(combo.getValue().replace("%", ""));
            }
        }
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    private void recalculateTotal() {
        double grandSubtotal = 0;
        double grandDiscount = 0;
        double grandVat = 0;
        double grandTotal = 0;
        
        // Process services
        for (Map<String, Object> service : selectedServices) {
            double basePrice = (Double) service.get("price");
            int discPct = getItemDiscountPercent(service);
            double[] result = calcItemTotals(service, basePrice, discPct);
            grandSubtotal += result[0];
            grandDiscount += result[1];
            grandVat += result[2];
            grandTotal += result[3];
        }
        
        // Process packages
        for (Map<String, Object> pkg : selectedPackages) {
            double basePrice = (Double) pkg.get("price");
            int discPct = getItemDiscountPercent(pkg);
            double[] result = calcItemTotals(pkg, basePrice, discPct);
            grandSubtotal += result[0];
            grandDiscount += result[1];
            grandVat += result[2];
            grandTotal += result[3];
        }
        
        // Process products
        for (Map<String, Object> product : selectedProducts) {
            double basePrice = (Double) product.get("totalPrice");
            int discPct = getItemDiscountPercent(product);
            double[] result = calcItemTotals(product, basePrice, discPct);
            grandSubtotal += result[0];
            grandDiscount += result[1];
            grandVat += result[2];
            grandTotal += result[3];
        }
        
        totalAmount = grandTotal; // for validation
        
        if (subtotalLabel != null) subtotalLabel.setText(formatPrice(grandSubtotal));
        if (discountTotalLabel != null) {
            discountTotalLabel.setText(grandDiscount > 0 ? "-" + formatPrice(grandDiscount) : "0đ");
        }
        if (vatLabel != null) vatLabel.setText(formatPrice(grandVat));
        if (totalLabel != null) totalLabel.setText(formatPrice(grandTotal));
    }
    
    @SuppressWarnings("unchecked")
    private double[] calcItemTotals(Map<String, Object> item, double basePrice, int discPct) {
        double discAmount = basePrice * discPct / 100.0;
        double afterDisc = basePrice - discAmount;
        double vat = afterDisc * 0.08;
        double itemTotal = afterDisc + vat;
        
        // Update per-item labels
        javafx.scene.Node node = (javafx.scene.Node) item.get("hbox");
        if (node != null) {
            Label discLabel = (Label) node.getProperties().get("discountAmountLabel");
            Label vatLbl = (Label) node.getProperties().get("vatLabel");
            Label totalLbl = (Label) node.getProperties().get("totalLabel");
            
            if (discLabel != null) discLabel.setText(discPct > 0 ? "Giảm: -" + formatPrice(discAmount) : "");
            if (vatLbl != null) vatLbl.setText("VAT 8%: " + formatPrice(vat));
            if (totalLbl != null) totalLbl.setText("= " + formatPrice(itemTotal));
        }
        
        return new double[]{basePrice, discAmount, vat, itemTotal};
    }
    
    private void filterServicesAndPackages(String query) {
        String lowerQuery = query.toLowerCase().trim();
        
        // Filter services
        for (var node : servicesContainer.getChildren()) {
            if (node instanceof ScrollPane) {
                ScrollPane sp = (ScrollPane) node;
                VBox servicesList = (VBox) sp.getContent();
                for (var serviceNode : servicesList.getChildren()) {
                    if (serviceNode instanceof HBox) {
                        HBox item = (HBox) serviceNode;
                        String[] data = (String[]) item.getUserData();
                        if (data != null && data.length > 0) {
                            String name = data[0];
                            boolean matches = name.toLowerCase().contains(lowerQuery);
                            item.setVisible(matches);
                            item.setManaged(matches);
                        }
                    }
                }
            }
        }
        
        // Filter packages
        for (var node : packagesContainer.getChildren()) {
            if (node instanceof ScrollPane) {
                ScrollPane sp = (ScrollPane) node;
                VBox packagesList = (VBox) sp.getContent();
                for (var packageNode : packagesList.getChildren()) {
                    if (packageNode instanceof HBox) {
                        HBox item = (HBox) packageNode;
                        String[] data = (String[]) item.getUserData();
                        if (data != null && data.length > 0) {
                            String name = data[0];
                            String desc = data.length > 1 ? data[1] : "";
                            boolean matches = name.toLowerCase().contains(lowerQuery) || desc.toLowerCase().contains(lowerQuery);
                            item.setVisible(matches);
                            item.setManaged(matches);
                        }
                    }
                }
            }
        }
    }
    
    private void filterProducts(VBox productsBox, String query) {
        String lowerQuery = query.toLowerCase().trim();
        for (var node : productsBox.getChildren()) {
            if (node instanceof ScrollPane) {
                ScrollPane sp = (ScrollPane) node;
                VBox productsList = (VBox) sp.getContent();
                for (var productNode : productsList.getChildren()) {
                    if (productNode instanceof HBox) {
                        HBox item = (HBox) productNode;
                        String name = (String) item.getUserData();
                        if (name != null) {
                            boolean matches = name.toLowerCase().contains(lowerQuery);
                            item.setVisible(matches);
                            item.setManaged(matches);
                        }
                    }
                }
            }
        }
    }
    
    private double parsePrice(String price) {
        // Remove currency symbol and all thousand separators (comma, period, space, non-breaking space)
        // This handles different locale formats: "90,000đ", "90.000đ", "90 000đ"
        String cleaned = price.replace("đ", "")
                              .replace(",", "")
                              .replace(".", "")
                              .replace(" ", "")
                              .replace("\u00A0", "") // non-breaking space
                              .trim();
        return Double.parseDouble(cleaned);
    }
    
    private String formatPrice(double price) {
        return String.format("%,.0fđ", price);
    }
}
