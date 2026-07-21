package ui;

import model.Product;
import model.InventoryReceipt;
import model.FixedExpense;
import dao.ProductDAO;
import dao.InventoryReceiptDAO;
import dao.FixedExpenseDAO;
import util.AlertHelper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryReceiptForm {
    private Stage stage;
    private ComboBox<Product> cbProducts;
    private TextField txtSearch;
    private TextField txtCostPrice;
    private TextField txtQty;
    private TextField txtTotalPrice;
    private DatePicker dpDate;
    private TextField txtProvider;
    private TextField txtOperator;
    private TextArea txtNotes;
    private Runnable onSave;

    public InventoryReceiptForm(Runnable onSave) {
        this.onSave = onSave;
    }

    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tạo Phiếu Nhập Kho");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");

        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("📥 Tạo Phiếu Nhập Kho Mới");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #212121;");

        VBox formSection = createFormSection();
        HBox actionButtons = createActionButtons();

        mainContent.getChildren().addAll(title, formSection, actionButtons);
        scrollPane.setContent(mainContent);

        Scene scene = new Scene(scrollPane, 700, 750);
        try {
            String css = MainUI.class.getResource("/global-styles.css").toExternalForm();
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

        Label sectionTitle = new Label("Thông Tin Nhập Kho");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976D2; -fx-font-weight: 700; -fx-padding: 0 0 10 0;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(180);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);
        grid.getColumnConstraints().addAll(col0, col1);

        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;";
        String fieldStyle = "-fx-background-color: #f5f5f5; -fx-padding: 12px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px;";
        String comboStyle = "-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-pref-height: 44px; -fx-pref-width: 300px;";

        // Product Selector
        Label lblProduct = new Label("Chọn sản phẩm *");
        lblProduct.setStyle(labelStyle);

        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm sản phẩm...");
        txtSearch.setStyle(fieldStyle);
        txtSearch.setPrefWidth(120);
        UIUtils.setupIMEFix(txtSearch);

        cbProducts = new ComboBox<>();
        cbProducts.setPromptText("--- Chọn sản phẩm trong kho ---");
        cbProducts.setStyle(comboStyle);
        cbProducts.setMaxWidth(Double.MAX_VALUE);

        // Load products
        List<Product> allProducts = new ArrayList<>();
        try {
            allProducts.addAll(new ProductDAO().getAllProducts());
            cbProducts.getItems().addAll(allProducts);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Custom Cell to show product name and unit
        cbProducts.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + (item.getUnit() != null ? item.getUnit() : "đơn vị") + ")");
                }
            }
        });
        cbProducts.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + (item.getUnit() != null ? item.getUnit() : "đơn vị") + ")");
                }
            }
        });

        HBox productBox = new HBox(10);
        productBox.setAlignment(Pos.CENTER_LEFT);
        productBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cbProducts, Priority.ALWAYS);
        productBox.getChildren().addAll(txtSearch, cbProducts);

        // Cost Price (read-only)
        Label lblCostPrice = new Label("Giá nhập (VND)");
        lblCostPrice.setStyle(labelStyle);
        txtCostPrice = new TextField("0");
        txtCostPrice.setEditable(false);
        txtCostPrice.setStyle(fieldStyle + "-fx-text-fill: #757575;");
        txtCostPrice.setMaxWidth(Double.MAX_VALUE);

        // Quantity
        Label lblQty = new Label("Số lượng nhập *");
        lblQty.setStyle(labelStyle);
        txtQty = new TextField("1");
        txtQty.setStyle(fieldStyle);
        txtQty.setMaxWidth(Double.MAX_VALUE);
        UIUtils.setupIMEFix(txtQty);

        // Total Price (read-only)
        Label lblTotalPrice = new Label("Thành tiền (VND)");
        lblTotalPrice.setStyle(labelStyle);
        txtTotalPrice = new TextField("0");
        txtTotalPrice.setEditable(false);
        txtTotalPrice.setStyle(fieldStyle + "-fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        txtTotalPrice.setMaxWidth(Double.MAX_VALUE);

        // Date picker
        Label lblDate = new Label("Ngày nhập *");
        lblDate.setStyle(labelStyle);
        dpDate = new DatePicker(LocalDate.now());
        dpDate.setMaxWidth(Double.MAX_VALUE);
        dpDate.setStyle("-fx-font-size: 14px; -fx-pref-height: 44px;");

        // Provider
        Label lblProvider = new Label("Nhà cung cấp");
        lblProvider.setStyle(labelStyle);
        txtProvider = new TextField();
        txtProvider.setPromptText("Tên nhà cung cấp (nếu có)...");
        txtProvider.setStyle(fieldStyle);
        txtProvider.setMaxWidth(Double.MAX_VALUE);

        // Operator
        Label lblOperator = new Label("Người thực hiện *");
        lblOperator.setStyle(labelStyle);
        txtOperator = new TextField("Admin");
        txtOperator.setPromptText("Nhập họ tên người thực hiện...");
        txtOperator.setStyle(fieldStyle);
        txtOperator.setMaxWidth(Double.MAX_VALUE);

        // Notes
        Label lblNotes = new Label("Ghi chú");
        lblNotes.setStyle(labelStyle);
        txtNotes = new TextArea();
        txtNotes.setPromptText("Nhập ghi chú chi tiết nếu có...");
        txtNotes.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px;");
        txtNotes.setPrefRowCount(3);
        txtNotes.setMaxWidth(Double.MAX_VALUE);

        // Actions & Calculations Bindings
        cbProducts.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtCostPrice.setText(String.format("%.0f", newVal.getCostPrice()));
                calculateTotal();
            } else {
                txtCostPrice.setText("0");
                txtTotalPrice.setText("0");
            }
        });

        txtQty.textProperty().addListener((obs, oldVal, newVal) -> {
            calculateTotal();
        });

        // Search Filter Logic
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            Product currentSelection = cbProducts.getValue();
            if (newVal == null || newVal.trim().isEmpty()) {
                cbProducts.getItems().setAll(allProducts);
            } else {
                String search = newVal.toLowerCase().trim();
                List<Product> filtered = new ArrayList<>();
                for (Product p : allProducts) {
                    if (p.getName().toLowerCase().contains(search)) {
                        filtered.add(p);
                    }
                }
                cbProducts.getItems().setAll(filtered);
                if (!cbProducts.isShowing() && txtSearch.isFocused()) {
                    cbProducts.show();
                }
            }
            if (currentSelection != null && cbProducts.getItems().contains(currentSelection)) {
                cbProducts.setValue(currentSelection);
            }
        });

        txtSearch.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.DOWN) {
                cbProducts.requestFocus();
                cbProducts.show();
            }
        });

        cbProducts.setOnHiding(event -> {
            txtSearch.clear();
        });

        // Add to grid
        grid.add(lblProduct, 0, 0); grid.add(productBox, 1, 0);
        grid.add(lblCostPrice, 0, 1); grid.add(txtCostPrice, 1, 1);
        grid.add(lblQty, 0, 2); grid.add(txtQty, 1, 2);
        grid.add(lblTotalPrice, 0, 3); grid.add(txtTotalPrice, 1, 3);
        grid.add(lblDate, 0, 4); grid.add(dpDate, 1, 4);
        grid.add(lblProvider, 0, 5); grid.add(txtProvider, 1, 5);
        grid.add(lblOperator, 0, 6); grid.add(txtOperator, 1, 6);
        grid.add(lblNotes, 0, 7); grid.add(txtNotes, 1, 7);

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private void calculateTotal() {
        try {
            double cost = Double.parseDouble(txtCostPrice.getText());
            String qtyText = txtQty.getText().trim();
            if (qtyText.isEmpty()) {
                txtTotalPrice.setText("0");
                return;
            }
            double qty = Double.parseDouble(qtyText);
            txtTotalPrice.setText(String.format("%.0f", cost * qty));
        } catch (NumberFormatException e) {
            txtTotalPrice.setText("0");
        }
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

        Button btnSave = new Button("Xác nhận nhập kho");
        btnSave.setStyle(
            "-fx-background-color: #1976D2;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 30px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnSave.setOnAction(e -> saveReceipt());

        buttons.getChildren().addAll(btnCancel, btnSave);
        return buttons;
    }

    private void saveReceipt() {
        Product p = cbProducts.getValue();
        if (p == null) {
            AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn sản phẩm cần nhập!").showAndWait();
            return;
        }

        double qty = 0;
        try {
            qty = Double.parseDouble(txtQty.getText().trim());
            if (qty <= 0) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng nhập phải lớn hơn 0!").showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng nhập không đúng định dạng!").showAndWait();
            return;
        }

        LocalDate date = dpDate.getValue();
        if (date == null) {
            AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn ngày nhập!").showAndWait();
            return;
        }

        String operator = txtOperator.getText().trim();
        if (operator.isEmpty()) {
            AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập tên người thực hiện!").showAndWait();
            return;
        }

        double cost = p.getCostPrice();
        double total = cost * qty;
        String dateStr = date.toString(); // YYYY-MM-DD
        String provider = txtProvider.getText().trim();
        String notes = txtNotes.getText().trim();

        // 1. Save Inventory Receipt
        InventoryReceipt receipt = new InventoryReceipt(0, p.getId(), p.getName(), qty, cost, total, dateStr, provider, notes, operator, null);
        InventoryReceiptDAO receiptDAO = new InventoryReceiptDAO();
        int receiptId = receiptDAO.addReceipt(receipt);

        if (receiptId > 0) {
            // 2. Update stock of product
            double newStock = p.getStock() + qty;
            new ProductDAO().updateProductStock(p.getId(), newStock);

            // 3. Save as Variable Expense (chi phí biến thiên)
            FixedExpense expense = new FixedExpense();
            expense.setExpenseName("Nhập kho: " + p.getName() + " (SL: " + new java.text.DecimalFormat("#.##").format(qty) + ")");
            expense.setCategory("biến thiên");
            expense.setAmount(total);
            expense.setExpenseMonth(dateStr.substring(0, 7)); // format: YYYY-MM
            expense.setNotes("Mã phiếu nhập: NK-" + String.format("%04d", receiptId) + ". Người lập: " + operator + (provider.isEmpty() ? "" : ". NCC: " + provider));
            new FixedExpenseDAO().addExpense(expense);

            AlertHelper.createAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu phiếu nhập kho và tự động cập nhật tồn kho!").showAndWait();
            if (onSave != null) {
                onSave.run();
            }
            stage.close();
        } else {
            AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu phiếu nhập kho vào cơ sở dữ liệu!").showAndWait();
        }
    }
}
