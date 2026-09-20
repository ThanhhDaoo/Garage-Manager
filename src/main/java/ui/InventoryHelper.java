package ui;

import model.InventoryReceipt;
import dao.InventoryReceiptDAO;
import util.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Window;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryHelper {

    private static TableView<InventoryReceipt> tableView;
    private static ObservableList<InventoryReceipt> masterData = FXCollections.observableArrayList();
    private static ObservableList<InventoryReceipt> filteredData = FXCollections.observableArrayList();

    private static Label lblTotalValue;
    private static Label lblReceiptCount;
    private static Label lblTotalValueTitle;
    private static Label lblReceiptCountTitle;
    
    private static ComboBox<String> cbMonth;
    private static ComboBox<String> cbYear;
    private static ComboBox<String> cbPaymentFilter;
    private static TextField txtSearch;

    public static void showInventoryManagement(MainUI mainUI, StackPane contentArea) {
        contentArea.getChildren().clear();

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header Title
        Label headerTitle = new Label("📥 Quản Lý Nhập Kho");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-text-fill: #1a237e;");

        // Control & Filter Panel
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(15));
        filterBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 10, 0, 0, 4);"
        );

        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm phiếu nhập...");
        txtSearch.setPrefWidth(240);
        txtSearch.setMinWidth(180);
        txtSearch.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 13px;" +
            "-fx-pref-height: 38px;"
        );
        UIUtils.setupIMEFix(txtSearch);

        // Month filter
        cbMonth = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cbMonth.getItems().add(String.format("%02d", i));
        }
        cbMonth.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
        cbMonth.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 38px; -fx-pref-width: 90px;");
        cbMonth.setMinWidth(90);

        // Year filter
        cbYear = new ComboBox<>();
        int curYear = LocalDate.now().getYear();
        for (int i = curYear - 5; i <= curYear + 5; i++) {
            cbYear.getItems().add(String.valueOf(i));
        }
        cbYear.setValue(String.valueOf(curYear));
        cbYear.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 38px; -fx-pref-width: 100px;");
        cbYear.setMinWidth(100);

        // Payment status filter
        cbPaymentFilter = new ComboBox<>();
        cbPaymentFilter.getItems().addAll("Tất cả trạng thái", "Đã thanh toán", "Chưa thanh toán");
        cbPaymentFilter.setValue("Tất cả trạng thái");
        cbPaymentFilter.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 38px; -fx-pref-width: 155px;");
        cbPaymentFilter.setMinWidth(155);

        Button btnNewReceipt = new Button("➕ Tạo Phiếu Nhập");
        btnNewReceipt.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnNewReceipt.setMinWidth(165);
        btnNewReceipt.setPrefHeight(38);
        btnNewReceipt.setOnMouseEntered(e -> btnNewReceipt.setOpacity(0.9));
        btnNewReceipt.setOnMouseExited(e -> btnNewReceipt.setOpacity(1.0));
        btnNewReceipt.setOnAction(e -> {
            new InventoryReceiptForm(() -> loadReceiptsData()).showInOverlay(contentArea);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnPdf = new Button("📄 Xuất PDF");
        btnPdf.setStyle(
            "-fx-background-color: #E8F5E9;" +
            "-fx-text-fill: #2E7D32;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10px 18px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnPdf.setMinWidth(115);
        btnPdf.setPrefHeight(38);
        btnPdf.setOnMouseEntered(e -> btnPdf.setOpacity(0.9));
        btnPdf.setOnMouseExited(e -> btnPdf.setOpacity(1.0));
        btnPdf.setOnAction(e -> {
            String monthStr = cbYear.getValue() + "-" + cbMonth.getValue();
            ReportHelper.exportInventoryToPDF(filteredData, monthStr, contentArea.getScene().getWindow());
        });

        Button btnExcel = new Button("📊 Xuất Excel");
        btnExcel.setStyle(
            "-fx-background-color: #E8F5E9;" +
            "-fx-text-fill: #2E7D32;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10px 18px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnExcel.setMinWidth(125);
        btnExcel.setPrefHeight(38);
        btnExcel.setOnMouseEntered(e -> btnExcel.setOpacity(0.9));
        btnExcel.setOnMouseExited(e -> btnExcel.setOpacity(1.0));
        btnExcel.setOnAction(e -> {
            String monthStr = cbYear.getValue() + "-" + cbMonth.getValue();
            ReportHelper.exportInventoryToExcel(filteredData, monthStr, contentArea.getScene().getWindow());
        });

        filterBar.getChildren().addAll(
            txtSearch, cbMonth, cbYear, cbPaymentFilter, btnNewReceipt, spacer, btnPdf, btnExcel
        );

        // Summary Cards
        HBox summaryCards = new HBox(20);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        
        VBox cardTotalVal = createSummaryCard("TỔNG GIÁ TRỊ NHẬP KHO", "0 đ", "#E3F2FD", "#1565C0");
        VBox cardCount = createSummaryCard("SỐ PHIẾU NHẬP KHO", "0 phiếu", "#E8F5E9", "#2E7D32");
        summaryCards.getChildren().addAll(cardTotalVal, cardCount);

        // Receipts Table
        tableView = new TableView<>();
        tableView.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        
        TableColumn<InventoryReceipt, Integer> colStt = new TableColumn<>("STT");
        colStt.setPrefWidth(50);
        colStt.setStyle("-fx-alignment: CENTER;");
        colStt.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        TableColumn<InventoryReceipt, String> colCode = new TableColumn<>("Mã Phiếu");
        colCode.setPrefWidth(90);
        colCode.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        colCode.setCellValueFactory(cellData -> {
            int id = cellData.getValue().getId();
            return new javafx.beans.property.SimpleStringProperty("NK-" + String.format("%04d", id));
        });

        TableColumn<InventoryReceipt, String> colDate = new TableColumn<>("Ngày Nhập");
        colDate.setPrefWidth(110);
        colDate.setStyle("-fx-alignment: CENTER;");
        colDate.setCellValueFactory(new PropertyValueFactory<>("receiptDate"));

        TableColumn<InventoryReceipt, String> colProduct = new TableColumn<>("Sản Phẩm");
        colProduct.setPrefWidth(170);
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<InventoryReceipt, Double> colQty = new TableColumn<>("Số Lượng");
        colQty.setPrefWidth(80);
        colQty.setStyle("-fx-alignment: CENTER-RIGHT;");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQty.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double qty, boolean empty) {
                super.updateItem(qty, empty);
                if (empty || qty == null) {
                    setText(null);
                } else {
                    setText(new java.text.DecimalFormat("#.##").format(qty));
                }
            }
        });

        TableColumn<InventoryReceipt, Double> colPrice = new TableColumn<>("Đơn Giá");
        colPrice.setPrefWidth(110);
        colPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        colPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f đ", price));
                }
            }
        });

        TableColumn<InventoryReceipt, Double> colTotal = new TableColumn<>("Thành Tiền");
        colTotal.setPrefWidth(130);
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f đ", price));
                }
            }
        });

        TableColumn<InventoryReceipt, String> colPaymentStatus = new TableColumn<>("Trạng Thái TT");
        colPaymentStatus.setPrefWidth(140);
        colPaymentStatus.setStyle("-fx-alignment: CENTER;");
        colPaymentStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        colPaymentStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label();
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setStyle("-fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;");
                    if ("Đã thanh toán".equalsIgnoreCase(status) || "paid".equalsIgnoreCase(status)) {
                        badge.setText("✓ Đã thanh toán");
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;");
                    } else {
                        badge.setText("⏳ Chưa thanh toán");
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828;");
                    }
                    badge.setCursor(javafx.scene.Cursor.HAND);
                    badge.setTooltip(new Tooltip("Nhấn để chuyển đổi trạng thái thanh toán"));
                    badge.setOnMouseClicked(e -> {
                        InventoryReceipt r = getTableView().getItems().get(getIndex());
                        String newStatus = "Đã thanh toán".equalsIgnoreCase(r.getPaymentStatus()) ? "Chưa thanh toán" : "Đã thanh toán";
                        r.setPaymentStatus(newStatus);
                        new InventoryReceiptDAO().updateReceipt(r);
                        loadReceiptsData();
                    });
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<InventoryReceipt, String> colOperator = new TableColumn<>("Nhà Cung Cấp");
        colOperator.setPrefWidth(120);
        colOperator.setCellValueFactory(new PropertyValueFactory<>("operator"));
        colOperator.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String op, boolean empty) {
                super.updateItem(op, empty);
                if (empty) {
                    setText(null);
                } else if (op == null || op.trim().isEmpty()) {
                    setText("-");
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: #9e9e9e;");
                } else {
                    setText(op);
                    setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #212121;");
                }
            }
        });

        TableColumn<InventoryReceipt, String> colNotes = new TableColumn<>("Ghi Chú");
        colNotes.setPrefWidth(150);
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // ---- ACTION COLUMN ----
        TableColumn<InventoryReceipt, Void> colAction = new TableColumn<>("Thao Tác");
        colAction.setPrefWidth(95);
        colAction.setStyle("-fx-alignment: CENTER;");
        colAction.setSortable(false);
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("✏");
            private final Button btnDelete = new Button("🗑");
            private final HBox   box       = new HBox(6, btnEdit, btnDelete);
            {
                box.setAlignment(javafx.geometry.Pos.CENTER);
                btnEdit.setStyle(
                    "-fx-background-color: #E3F2FD; -fx-text-fill: #1976D2;" +
                    "-fx-font-size: 11px; -fx-padding: 4 8; -fx-background-radius: 6; -fx-cursor: hand;"
                );
                btnDelete.setStyle(
                    "-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F;" +
                    "-fx-font-size: 11px; -fx-padding: 4 8; -fx-background-radius: 6; -fx-cursor: hand;"
                );

                // --- EDIT ---
                btnEdit.setOnAction(e -> {
                    InventoryReceipt r = getTableView().getItems().get(getIndex());
                    showEditOverlay(contentArea, r);
                });

                // --- DELETE ---
                btnDelete.setOnAction(e -> {
                    InventoryReceipt r = getTableView().getItems().get(getIndex());
                    Alert confirm = AlertHelper.createAlert(
                        Alert.AlertType.CONFIRMATION,
                        "Xác Nhận Xoá",
                        "Bạn có chắc muốn xoá phiếu " +
                        "NK-" + String.format("%04d", r.getId()) + " không?\n" +
                        "Tồn kho sản phẩm sẽ được hoàn lại."
                    );
                    confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(resp -> {
                        if (resp == ButtonType.YES) {
                            InventoryReceiptDAO dao = new InventoryReceiptDAO();
                            if (dao.deleteReceipt(r.getId())) {
                                // Revert stock
                                dao.revertStock(r.getProductId(), r.getQuantity());
                                // Xóa chi phí biến thiên tương ứng
                                String receiptCode = "NK-" + String.format("%04d", r.getId());
                                new dao.FixedExpenseDAO().deleteExpenseByReceiptCode(receiptCode);
                                loadReceiptsData();
                            } else {
                                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi",
                                    "Không thể xoá phiếu nhập!").show();
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tableView.getColumns().addAll(
            colStt, colCode, colDate, colProduct, colQty, colPrice, colTotal, colPaymentStatus, colOperator, colNotes, colAction
        );
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        root.getChildren().addAll(headerTitle, filterBar, summaryCards, tableView);
        contentArea.getChildren().add(root);

        // Bind events with debounce for search
        cbMonth.setOnAction(e -> loadReceiptsData());
        cbYear.setOnAction(e -> loadReceiptsData());
        cbPaymentFilter.setOnAction(e -> filterData());
        
        javafx.animation.PauseTransition debounce = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
        debounce.setOnFinished(e -> filterData());
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> debounce.playFromStart());

        // Initialize Data
        loadReceiptsData();
    }

    private static VBox createSummaryCard(String title, String defaultValue, String bgColor, String textColor) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15, 25, 15, 25));
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 12;" +
            "-fx-pref-width: 250px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 3);"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #757575; -fx-letter-spacing: 0.5px;");
        
        Label lblValue = new Label(defaultValue);
        lblValue.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: " + textColor + ";");

        if (title.contains("GIÁ TRỊ")) {
            lblTotalValue = lblValue;
            lblTotalValueTitle = lblTitle;
        } else {
            lblReceiptCount = lblValue;
            lblReceiptCountTitle = lblTitle;
        }

        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }

    private static void loadReceiptsData() {
        String monthStr = cbYear.getValue() + "-" + cbMonth.getValue(); // YYYY-MM
        InventoryReceiptDAO dao = new InventoryReceiptDAO();
        List<InventoryReceipt> receipts = dao.getReceiptsByMonth(monthStr);

        masterData.setAll(receipts);
        filterData();
    }

    private static void filterData() {
        String query = txtSearch != null && txtSearch.getText() != null ? txtSearch.getText().toLowerCase().trim() : "";
        String statusFilter = cbPaymentFilter != null && cbPaymentFilter.getValue() != null 
            ? cbPaymentFilter.getValue().trim() 
            : "Tất cả trạng thái";

        List<InventoryReceipt> list = new ArrayList<>();
        for (InventoryReceipt r : masterData) {
            // Lọc theo trạng thái thanh toán
            boolean matchStatus = true;
            if ("Đã thanh toán".equalsIgnoreCase(statusFilter)) {
                matchStatus = "Đã thanh toán".equalsIgnoreCase(r.getPaymentStatus()) || "paid".equalsIgnoreCase(r.getProvider());
            } else if ("Chưa thanh toán".equalsIgnoreCase(statusFilter)) {
                matchStatus = "Chưa thanh toán".equalsIgnoreCase(r.getPaymentStatus()) || "unpaid".equalsIgnoreCase(r.getProvider()) || r.getProvider() == null || r.getProvider().trim().isEmpty();
            }

            if (!matchStatus) continue;

            // Lọc theo từ khóa tìm kiếm
            boolean matchQuery = query.isEmpty() ||
                (r.getProductName() != null && r.getProductName().toLowerCase().contains(query)) ||
                ("nk-" + String.format("%04d", r.getId())).contains(query) ||
                (r.getOperator() != null && r.getOperator().toLowerCase().contains(query)) ||
                (r.getReceiptDate() != null && r.getReceiptDate().contains(query)) ||
                (r.getPaymentStatus() != null && r.getPaymentStatus().toLowerCase().contains(query)) ||
                (r.getNotes() != null && r.getNotes().toLowerCase().contains(query));

            if (matchQuery) {
                list.add(r);
            }
        }
        
        filteredData.setAll(list);
        tableView.setItems(filteredData);
        updateSummary();
    }

    private static void updateSummary() {
        double totalVal = 0;
        int count = filteredData.size();
        for (InventoryReceipt r : filteredData) {
            totalVal += r.getTotalPrice();
        }
        if (lblTotalValue != null) {
            lblTotalValue.setText(String.format("%,.0f đ", totalVal));
        }
        if (lblReceiptCount != null) {
            lblReceiptCount.setText(count + " phiếu");
        }

        String filter = cbPaymentFilter != null && cbPaymentFilter.getValue() != null ? cbPaymentFilter.getValue().trim() : "Tất cả trạng thái";
        if (lblTotalValueTitle != null && lblReceiptCountTitle != null) {
            if ("Đã thanh toán".equalsIgnoreCase(filter)) {
                lblTotalValueTitle.setText("TỔNG GIÁ TRỊ (ĐÃ THANH TOÁN)");
                lblReceiptCountTitle.setText("SỐ PHIẾU (ĐÃ THANH TOÁN)");
            } else if ("Chưa thanh toán".equalsIgnoreCase(filter)) {
                lblTotalValueTitle.setText("TỔNG GIÁ TRỊ (CHƯA THANH TOÁN)");
                lblReceiptCountTitle.setText("SỐ PHIẾU (CHƯA THANH TOÁN)");
            } else {
                lblTotalValueTitle.setText("TỔNG GIÁ TRỊ NHẬP KHO");
                lblReceiptCountTitle.setText("SỐ PHIẾU NHẬP KHO");
            }
        }
    }

    private static void showEditOverlay(StackPane container, InventoryReceipt r) {
        BorderPane root = new BorderPane();

        // ===== Outer scroll =====
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");

        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("✏ Chỉnh Sửa Phiếu Nhập Kho");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #212121;");

        // ===== Card section =====
        VBox section = new VBox(20);
        section.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 30;"
        );

        Label sectionTitle = new Label("Thông Tin Phiếu Nhập");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976D2; -fx-font-weight: 700; -fx-padding: 0 0 10 0;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(190);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);
        grid.getColumnConstraints().addAll(col0, col1);

        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;";
        String fieldStyle = "-fx-background-color: #f5f5f5; -fx-padding: 12px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px;";
        String fieldDisabledStyle = fieldStyle + "-fx-text-fill: #757575;";

        // Row 0: Sản phẩm (read-only)
        Label lblProd = new Label("Sản phẩm");
        lblProd.setStyle(labelStyle);
        TextField txtProd = new TextField(r.getProductName());
        txtProd.setEditable(false);
        txtProd.setStyle(fieldDisabledStyle);
        txtProd.setMaxWidth(Double.MAX_VALUE);

        // Row 1: Đơn giá (read-only)
        Label lblCost = new Label("Đơn giá nhập (VND)");
        lblCost.setStyle(labelStyle);
        TextField txtCost = new TextField(String.format("%,.0f", r.getCostPrice()));
        txtCost.setEditable(false);
        txtCost.setStyle(fieldDisabledStyle);
        txtCost.setMaxWidth(Double.MAX_VALUE);

        // Row 2: Số lượng
        Label lblQty = new Label("Số lượng nhập *");
        lblQty.setStyle(labelStyle);
        TextField txtQty = new TextField(new java.text.DecimalFormat("#.##").format(r.getQuantity()));
        txtQty.setStyle(fieldStyle);
        txtQty.setMaxWidth(Double.MAX_VALUE);

        // Row 3: Thành tiền (auto, read-only)
        Label lblTotal = new Label("Thành tiền (VND)");
        lblTotal.setStyle(labelStyle);
        TextField txtTotal = new TextField(String.format("%,.0f", r.getTotalPrice()));
        txtTotal.setEditable(false);
        txtTotal.setStyle(fieldStyle + "-fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        txtTotal.setMaxWidth(Double.MAX_VALUE);

        // Auto-calc total on qty change
        txtQty.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double qty = Double.parseDouble(newVal.trim());
                txtTotal.setText(String.format("%,.0f", r.getCostPrice() * qty));
            } catch (NumberFormatException ex) {
                txtTotal.setText("0");
            }
        });

        // Row 4: Trạng thái thanh toán
        Label lblPaymentStatus = new Label("Trạng thái thanh toán *");
        lblPaymentStatus.setStyle(labelStyle);
        ComboBox<String> cbPaymentStatus = new ComboBox<>();
        cbPaymentStatus.getItems().addAll("Đã thanh toán", "Chưa thanh toán");
        cbPaymentStatus.setValue(r.getPaymentStatus());
        cbPaymentStatus.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-pref-height: 44px;");
        cbPaymentStatus.setMaxWidth(Double.MAX_VALUE);

        // Row 5: Ngày nhập
        Label lblDate = new Label("Ngày nhập *");
        lblDate.setStyle(labelStyle);
        DatePicker dpDate = new DatePicker();
        try { dpDate.setValue(java.time.LocalDate.parse(r.getReceiptDate())); } catch (Exception ignored) {}
        dpDate.setMaxWidth(Double.MAX_VALUE);
        dpDate.setStyle("-fx-font-size: 14px; -fx-pref-height: 44px;");

        // Row 6: Nhà cung cấp
        Label lblOp = new Label("Nhà cung cấp");
        lblOp.setStyle(labelStyle);
        TextField txtOp = new TextField(r.getOperator() != null ? r.getOperator() : "");
        txtOp.setPromptText("Nhập tên nhà cung cấp (nếu có)...");
        txtOp.setStyle(fieldStyle);
        txtOp.setMaxWidth(Double.MAX_VALUE);

        // Row 7: Ghi chú
        Label lblNotes = new Label("Ghi chú");
        lblNotes.setStyle(labelStyle);
        TextArea txtNotes = new TextArea(r.getNotes() != null ? r.getNotes() : "");
        txtNotes.setPromptText("Nhập ghi chú nếu có...");
        txtNotes.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px;");
        txtNotes.setPrefRowCount(3);
        txtNotes.setMaxWidth(Double.MAX_VALUE);

        grid.add(lblProd, 0, 0);  grid.add(txtProd, 1, 0);
        grid.add(lblCost, 0, 1);  grid.add(txtCost, 1, 1);
        grid.add(lblQty,  0, 2);  grid.add(txtQty,  1, 2);
        grid.add(lblTotal,0, 3);  grid.add(txtTotal,1, 3);
        grid.add(lblPaymentStatus, 0, 4); grid.add(cbPaymentStatus, 1, 4);
        grid.add(lblDate, 0, 5);  grid.add(dpDate,  1, 5);
        grid.add(lblOp,   0, 6);  grid.add(txtOp,   1, 6);
        grid.add(lblNotes,0, 7);  grid.add(txtNotes,1, 7);

        section.getChildren().addAll(sectionTitle, grid);

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        Runnable closeEdit = () -> container.getChildren().remove(overlay);

        // ===== Action buttons =====
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
        btnCancel.setOnAction(e -> closeEdit.run());

        Button btnSave = new Button("Lưu Thay Đổi");
        btnSave.setStyle(
            "-fx-background-color: #1976D2;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 30px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        btnSave.setOnAction(e -> {
            double newQty;
            try {
                newQty = Double.parseDouble(txtQty.getText().trim().replace(",", ""));
                if (newQty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng không hợp lệ!").show();
                return;
            }
            if (dpDate.getValue() == null) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn ngày nhập!").show();
                return;
            }
            double oldQty  = r.getQuantity();
            double diffQty = newQty - oldQty;

            r.setQuantity(newQty);
            r.setTotalPrice(r.getCostPrice() * newQty);
            r.setPaymentStatus(cbPaymentStatus.getValue());
            r.setReceiptDate(dpDate.getValue().toString());
            r.setOperator(txtOp.getText().trim());
            r.setNotes(txtNotes.getText().trim());

            InventoryReceiptDAO dao = new InventoryReceiptDAO();
            if (dao.updateReceipt(r)) {
                if (diffQty != 0) {
                    String stockSql = diffQty > 0
                        ? "UPDATE products SET stock = stock + ? WHERE id = ?"
                        : "UPDATE products SET stock = MAX(0, stock + ?) WHERE id = ?";
                    try (java.sql.Connection conn = util.DatabaseManager.getConnection();
                         java.sql.PreparedStatement ps = conn.prepareStatement(stockSql)) {
                        ps.setDouble(1, diffQty);
                        ps.setInt(2, r.getProductId());
                        ps.executeUpdate();
                    } catch (java.sql.SQLException ex) { ex.printStackTrace(); }
                }
                
                // Đồng bộ cập nhật chi phí biến thiên tương ứng
                String receiptCode = "NK-" + String.format("%04d", r.getId());
                String newExpenseName = "Nhập kho: " + r.getProductName() + " (SL: " + new java.text.DecimalFormat("#.##").format(r.getQuantity()) + ")";
                String newMonth = r.getReceiptDate().substring(0, 7); // format: YYYY-MM
                String opText = (r.getOperator() != null && !r.getOperator().trim().isEmpty()) ? ". Nhà cung cấp: " + r.getOperator().trim() : "";
                String newNotes = "Mã phiếu nhập: NK-" + String.format("%04d", r.getId()) + opText;
                String newCreatedAt = (r.getReceiptDate() != null && r.getReceiptDate().length() >= 10)
                    ? r.getReceiptDate().substring(0, 10) + " 12:00:00"
                    : r.getReceiptDate();
                new dao.FixedExpenseDAO().updateExpenseByReceiptCode(receiptCode, newExpenseName, r.getTotalPrice(), newMonth, newNotes, newCreatedAt);

                closeEdit.run();
                loadReceiptsData();
            } else {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không thể cập nhật phiếu nhập!").show();
            }
        });

        // Action Buttons (Fixed at bottom)
        HBox btnBar = new HBox(15, btnCancel, btnSave);
        btnBar.setAlignment(Pos.CENTER_RIGHT);
        btnBar.setPadding(new Insets(15, 30, 15, 30));
        btnBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1 0 0 0;"
        );

        mainContent.getChildren().addAll(title, section);
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);
        root.setBottom(btnBar);

        root.setMaxWidth(700);
        root.setMaxHeight(700);
        root.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.35), 20, 0, 0, 0);"
        );

        overlay.getChildren().add(root);
        container.getChildren().add(overlay);
    }
}

