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
    private TextField txtCostMini;
    private TextField txtCostSedan;
    private TextField txtCostCuv;
    private TextField txtCostSuv;
    private TextField txtCostMpv;
    private TextField txtCostPickup;
    private ComboBox<String> cbCategory;
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
        if (MainUI.getMainStage() != null && MainUI.getMainStage().getScene() != null && MainUI.getMainStage().getScene().getRoot() != null) {
            MainUI.getMainStage().getScene().getRoot().requestFocus();
        }
        stage = new Stage();
        if (MainUI.getMainStage() != null) {
            stage.initOwner(MainUI.getMainStage());
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(isEdit ? "Sửa Gói Dịch Vụ" : "Thêm Gói Dịch Vụ Mới");
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

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

        Scene scene = new Scene(root, 800, 800);
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
        txtName.setMaxWidth(Double.MAX_VALUE);
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
        txtDesc.setMaxWidth(Double.MAX_VALUE);
        if (isEdit && existingPackage != null) {
            txtDesc.setText(existingPackage.getDescription());
        }
        UIUtils.setupIMEFix(txtDesc);
        
        // Category Selection
        Label lblCategory = new Label("Phân loại gói dịch vụ *");
        lblCategory.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-font-family: 'Times New Roman';");
        cbCategory = new ComboBox<>();
        cbCategory.getItems().addAll("rửa xe", "chăm sóc", "phụ kiện", "sơn");
        cbCategory.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;" +
            "-fx-pref-height: 44px;" +
            "-fx-pref-width: 500px;" +
            "-fx-font-family: 'Times New Roman';"
        );
        cbCategory.setMaxWidth(Double.MAX_VALUE);
        cbCategory.setValue(isEdit && existingPackage != null && existingPackage.getCategory() != null ? existingPackage.getCategory() : "chăm sóc");
        
        // Price section header
        Label lblPriceHeader = new Label("💰 Bảng Giá Theo Loại Xe (Tích chọn để nhập giá bán & chi phí vật tư)");
        lblPriceHeader.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976D2; -fx-font-weight: 700; -fx-padding: 10 0 5 0; -fx-font-family: 'Times New Roman';");
        
        // Price list container
        VBox priceListContainer = new VBox(15);
        priceListContainer.setStyle("-fx-background-color: #FAFAFA; -fx-padding: 20; -fx-background-radius: 10; -fx-border-color: #E0E0E0; -fx-border-radius: 10;");
        priceListContainer.setMaxWidth(Double.MAX_VALUE);

        // Table Header Row for Vehicle Pricing
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setStyle("-fx-padding: 0 0 10 0; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label colVehicle = new Label("Loại Xe");
        colVehicle.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #424242; -fx-min-width: 90; -fx-font-family: 'Times New Roman';");

        Label colPrice = new Label("Giá Bán Gói (VNĐ)");
        colPrice.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1976D2; -fx-font-family: 'Times New Roman';");
        HBox.setHgrow(colPrice, Priority.ALWAYS);
        colPrice.setMaxWidth(Double.MAX_VALUE);

        Label colCost = new Label("Chi Phí Vật Tư / Giá Vốn (VNĐ)");
        colCost.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #D84315; -fx-font-family: 'Times New Roman';");
        HBox.setHgrow(colCost, Priority.ALWAYS);
        colCost.setMaxWidth(Double.MAX_VALUE);

        headerRow.getChildren().addAll(colVehicle, colPrice, colCost);
        priceListContainer.getChildren().add(headerRow);
        
        // Price & Cost for Mini
        chkMini = new CheckBox("Mini");
        chkMini.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90; -fx-font-family: 'Times New Roman';");
        txtPriceMini = new TextField();
        txtPriceMini.setPromptText("Giá bán (VD: 96000)");
        txtPriceMini.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        txtCostMini.setPromptText("Chi phí vật tư (VD: 20000)");
        txtCostMini.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        if (isEdit && existingPackage != null && existingPackage.getPriceMini() > 0) {
            chkMini.setSelected(true);
            txtPriceMini.setText(String.format("%.0f", existingPackage.getPriceMini()));
            txtCostMini.setText(String.format("%.0f", existingPackage.getCostPriceMini() > 0 ? existingPackage.getCostPriceMini() : existingPackage.getCostPrice()));
        } else {
            chkMini.setSelected(false);
            txtCostMini.setText("0");
        }
        
        // Price & Cost for Sedan
        chkSedan = new CheckBox("Sedan");
        chkSedan.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90; -fx-font-family: 'Times New Roman';");
        txtPriceSedan = new TextField();
        txtPriceSedan.setPromptText("Giá bán (VD: 120000)");
        txtPriceSedan.setStyle("-fx-background-color: #E8F5E9; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        txtCostSedan.setPromptText("Chi phí vật tư (VD: 25000)");
        txtCostSedan.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        if (isEdit && existingPackage != null && existingPackage.getPriceSedan() > 0) {
            chkSedan.setSelected(true);
            txtPriceSedan.setText(String.format("%.0f", existingPackage.getPriceSedan()));
            txtCostSedan.setText(String.format("%.0f", existingPackage.getCostPriceSedan() > 0 ? existingPackage.getCostPriceSedan() : existingPackage.getCostPrice()));
        } else {
            chkSedan.setSelected(false);
            txtCostSedan.setText("0");
        }
        
        // Price & Cost for CUV
        chkCuv = new CheckBox("CUV");
        chkCuv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90; -fx-font-family: 'Times New Roman';");
        txtPriceCuv = new TextField();
        txtPriceCuv.setPromptText("Giá bán (VD: 180000)");
        txtPriceCuv.setStyle("-fx-background-color: #FFF3E0; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        txtCostCuv.setPromptText("Chi phí vật tư (VD: 35000)");
        txtCostCuv.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        if (isEdit && existingPackage != null && existingPackage.getPriceCuv() > 0) {
            chkCuv.setSelected(true);
            txtPriceCuv.setText(String.format("%.0f", existingPackage.getPriceCuv()));
            txtCostCuv.setText(String.format("%.0f", existingPackage.getCostPriceCuv() > 0 ? existingPackage.getCostPriceCuv() : existingPackage.getCostPrice()));
        } else {
            chkCuv.setSelected(false);
            txtCostCuv.setText("0");
        }
        
        // Price & Cost for SUV
        chkSuv = new CheckBox("SUV");
        chkSuv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90; -fx-font-family: 'Times New Roman';");
        txtPriceSuv = new TextField();
        txtPriceSuv.setPromptText("Giá bán (VD: 240000)");
        txtPriceSuv.setStyle("-fx-background-color: #FCE4EC; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        txtCostSuv.setPromptText("Chi phí vật tư (VD: 45000)");
        txtCostSuv.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        if (isEdit && existingPackage != null && existingPackage.getPriceSuv() > 0) {
            chkSuv.setSelected(true);
            txtPriceSuv.setText(String.format("%.0f", existingPackage.getPriceSuv()));
            txtCostSuv.setText(String.format("%.0f", existingPackage.getCostPriceSuv() > 0 ? existingPackage.getCostPriceSuv() : existingPackage.getCostPrice()));
        } else {
            chkSuv.setSelected(false);
            txtCostSuv.setText("0");
        }
        
        // Price & Cost for MPV
        chkMpv = new CheckBox("MPV");
        chkMpv.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90; -fx-font-family: 'Times New Roman';");
        txtPriceMpv = new TextField();
        txtPriceMpv.setPromptText("Giá bán (VD: 252000)");
        txtPriceMpv.setStyle("-fx-background-color: #E0F7FA; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        txtCostMpv.setPromptText("Chi phí vật tư (VD: 50000)");
        txtCostMpv.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        if (isEdit && existingPackage != null && existingPackage.getPriceMpv() > 0) {
            chkMpv.setSelected(true);
            txtPriceMpv.setText(String.format("%.0f", existingPackage.getPriceMpv()));
            txtCostMpv.setText(String.format("%.0f", existingPackage.getCostPriceMpv() > 0 ? existingPackage.getCostPriceMpv() : existingPackage.getCostPrice()));
        } else {
            chkMpv.setSelected(false);
            txtCostMpv.setText("0");
        }
        
        // Price & Cost for Pickup
        chkPickup = new CheckBox("Pickup");
        chkPickup.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600; -fx-min-width: 90; -fx-font-family: 'Times New Roman';");
        txtPricePickup = new TextField();
        txtPricePickup.setPromptText("Giá bán (VD: 264000)");
        txtPricePickup.setStyle("-fx-background-color: #F3E5F5; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        txtCostPickup.setPromptText("Chi phí vật tư (VD: 55000)");
        txtCostPickup.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 10px 12px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");
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
        if (isEdit && existingPackage != null && existingPackage.getPricePickup() > 0) {
            chkPickup.setSelected(true);
            txtPricePickup.setText(String.format("%.0f", existingPackage.getPricePickup()));
            txtCostPickup.setText(String.format("%.0f", existingPackage.getCostPricePickup() > 0 ? existingPackage.getCostPricePickup() : existingPackage.getCostPrice()));
        } else {
            chkPickup.setSelected(false);
            txtCostPickup.setText("0");
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
        grid.add(lblCategory, 0, row++);
        grid.add(cbCategory, 0, row++);
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
            
            double priceMini = 0, costMini = 0;
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
                String cval = txtCostMini.getText().trim();
                if (!cval.isEmpty()) {
                    try {
                        costMini = Double.parseDouble(cval);
                    } catch (NumberFormatException ex) {
                        showAlert("Lỗi", "Chi phí vật tư xe Mini phải là số hợp lệ!", Alert.AlertType.ERROR);
                        return;
                    }
                }
            }
            
            double priceSedan = 0, costSedan = 0;
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
                String cval = txtCostSedan.getText().trim();
                if (!cval.isEmpty()) {
                    try {
                        costSedan = Double.parseDouble(cval);
                    } catch (NumberFormatException ex) {
                        showAlert("Lỗi", "Chi phí vật tư xe Sedan phải là số hợp lệ!", Alert.AlertType.ERROR);
                        return;
                    }
                }
            }
            
            double priceCuv = 0, costCuv = 0;
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
                String cval = txtCostCuv.getText().trim();
                if (!cval.isEmpty()) {
                    try {
                        costCuv = Double.parseDouble(cval);
                    } catch (NumberFormatException ex) {
                        showAlert("Lỗi", "Chi phí vật tư xe CUV phải là số hợp lệ!", Alert.AlertType.ERROR);
                        return;
                    }
                }
            }
            
            double priceSuv = 0, costSuv = 0;
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
                String cval = txtCostSuv.getText().trim();
                if (!cval.isEmpty()) {
                    try {
                        costSuv = Double.parseDouble(cval);
                    } catch (NumberFormatException ex) {
                        showAlert("Lỗi", "Chi phí vật tư xe SUV phải là số hợp lệ!", Alert.AlertType.ERROR);
                        return;
                    }
                }
            }
            
            double priceMpv = 0, costMpv = 0;
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
                String cval = txtCostMpv.getText().trim();
                if (!cval.isEmpty()) {
                    try {
                        costMpv = Double.parseDouble(cval);
                    } catch (NumberFormatException ex) {
                        showAlert("Lỗi", "Chi phí vật tư xe MPV phải là số hợp lệ!", Alert.AlertType.ERROR);
                        return;
                    }
                }
            }
            
            double pricePickup = 0, costPickup = 0;
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
                String cval = txtCostPickup.getText().trim();
                if (!cval.isEmpty()) {
                    try {
                        costPickup = Double.parseDouble(cval);
                    } catch (NumberFormatException ex) {
                        showAlert("Lỗi", "Chi phí vật tư xe Pickup phải là số hợp lệ!", Alert.AlertType.ERROR);
                        return;
                    }
                }
            }
            
            try {
                String name = txtName.getText().trim();
                String description = txtDesc.getText().trim();
                double avgSavings = 0;
                String status = rbActive.isSelected() ? "Đang bán" : "Tạm dừng";
                String category = cbCategory.getValue();
                double avgCost = (costMini + costSedan + costCuv + costSuv + costMpv + costPickup) / 6.0;
                
                PackageService packageService = new PackageService();
                boolean success;
                
                if (isEdit) {
                    success = packageService.updatePackage(packageId, name, description,
                                                           priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup,
                                                           costMini, costSedan, costCuv, costSuv, costMpv, costPickup,
                                                           avgSavings, status, category, avgCost);
                } else {
                    success = packageService.addPackage(name, description,
                                                        priceMini, priceSedan, priceCuv, priceSuv, priceMpv, pricePickup,
                                                        costMini, costSedan, costCuv, costSuv, costMpv, costPickup,
                                                        avgSavings, status, category, avgCost);
                }
                
                if (success) {
                    stage.close();
                    if (MainUI.getMainStage() != null) {
                        MainUI.getMainStage().toFront();
                        MainUI.getMainStage().requestFocus();
                    }
                    showAlert("Thành công", 
                             isEdit ? "Cập nhật gói dịch vụ thành công!" : "Thêm gói dịch vụ mới thành công!", 
                             Alert.AlertType.INFORMATION);
                    if (MainUI.getMainStage() != null) {
                        MainUI.getMainStage().toFront();
                        MainUI.getMainStage().requestFocus();
                    }
                    if (onSave != null) {
                        javafx.application.Platform.runLater(onSave);
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
        if (stage != null && stage.isShowing()) {
            alert.initOwner(stage);
        } else if (MainUI.getMainStage() != null) {
            alert.initOwner(MainUI.getMainStage());
        }
        util.AlertHelper.applyTimesNewRomanFont(alert);
        alert.showAndWait();
    }
}
