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
    
    private static ComboBox<String> cbMonth;
    private static ComboBox<String> cbYear;
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
        txtSearch.setPrefWidth(260);
        txtSearch.setMinWidth(200);
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
            Window owner = contentArea.getScene().getWindow();
            new InventoryReceiptForm(() -> loadReceiptsData()).show();
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
            txtSearch, cbMonth, cbYear, btnNewReceipt, spacer, btnPdf, btnExcel
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

        TableColumn<InventoryReceipt, String> colOperator = new TableColumn<>("Người Thực Hiện");
        colOperator.setPrefWidth(120);
        colOperator.setCellValueFactory(new PropertyValueFactory<>("operator"));

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
                    showEditDialog(r);
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
                                    "Không thể xoá phiếu nhập!").showAndWait();
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
            colStt, colCode, colDate, colProduct, colQty, colPrice, colTotal, colOperator, colNotes, colAction
        );
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        root.getChildren().addAll(headerTitle, filterBar, summaryCards, tableView);
        contentArea.getChildren().add(root);

        // Bind events
        cbMonth.setOnAction(e -> loadReceiptsData());
        cbYear.setOnAction(e -> loadReceiptsData());
        
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filterData(newVal);
        });

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
        } else {
            lblReceiptCount = lblValue;
        }

        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }

    private static void loadReceiptsData() {
        String monthStr = cbYear.getValue() + "-" + cbMonth.getValue(); // YYYY-MM
        InventoryReceiptDAO dao = new InventoryReceiptDAO();
        List<InventoryReceipt> receipts = dao.getReceiptsByMonth(monthStr);

        masterData.setAll(receipts);
        filterData(txtSearch.getText());
    }

    private static void filterData(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredData.setAll(masterData);
        } else {
            String q = query.toLowerCase().trim();
            List<InventoryReceipt> list = new ArrayList<>();
            for (InventoryReceipt r : masterData) {
                if (r.getProductName().toLowerCase().contains(q) ||
                    ("nk-" + String.format("%04d", r.getId())).contains(q) ||
                    r.getOperator().toLowerCase().contains(q) ||
                    (r.getNotes() != null && r.getNotes().toLowerCase().contains(q))) {
                    list.add(r);
                }
            }
            filteredData.setAll(list);
        }
        
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
    }

    private static void showEditDialog(InventoryReceipt r) {
        javafx.stage.Stage dialog = new javafx.stage.Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Sửa Phiếu Nhập – NK-" + String.format("%04d", r.getId()));

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

        // Row 4: Ngày nhập
        Label lblDate = new Label("Ngày nhập *");
        lblDate.setStyle(labelStyle);
        DatePicker dpDate = new DatePicker();
        try { dpDate.setValue(java.time.LocalDate.parse(r.getReceiptDate())); } catch (Exception ignored) {}
        dpDate.setMaxWidth(Double.MAX_VALUE);
        dpDate.setStyle("-fx-font-size: 14px; -fx-pref-height: 44px;");

        // Row 5: Người thực hiện
        Label lblOp = new Label("Người thực hiện *");
        lblOp.setStyle(labelStyle);
        TextField txtOp = new TextField(r.getOperator());
        txtOp.setPromptText("Nhập họ tên người thực hiện...");
        txtOp.setStyle(fieldStyle);
        txtOp.setMaxWidth(Double.MAX_VALUE);

        // Row 6: Ghi chú
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
        grid.add(lblDate, 0, 4);  grid.add(dpDate,  1, 4);
        grid.add(lblOp,   0, 5);  grid.add(txtOp,   1, 5);
        grid.add(lblNotes,0, 6);  grid.add(txtNotes,1, 6);

        section.getChildren().addAll(sectionTitle, grid);

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
        btnCancel.setOnAction(e -> dialog.close());

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
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng không hợp lệ!").showAndWait();
                return;
            }
            if (dpDate.getValue() == null) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn ngày nhập!").showAndWait();
                return;
            }
            if (txtOp.getText().trim().isEmpty()) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập người thực hiện!").showAndWait();
                return;
            }

            double oldQty  = r.getQuantity();
            double diffQty = newQty - oldQty;

            r.setQuantity(newQty);
            r.setTotalPrice(r.getCostPrice() * newQty);
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
                AlertHelper.createAlert(Alert.AlertType.INFORMATION, "Thành công",
                    "Đã cập nhật phiếu nhập kho!").showAndWait();
                dialog.close();
                loadReceiptsData();
            } else {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không thể cập nhật phiếu nhập!").showAndWait();
            }
        });

        HBox btnBar = new HBox(15, btnCancel, btnSave);
        btnBar.setAlignment(Pos.CENTER_RIGHT);

        mainContent.getChildren().addAll(title, section, btnBar);
        scrollPane.setContent(mainContent);

        javafx.scene.Scene scene = new javafx.scene.Scene(scrollPane, 700, 700);
        try {
            String css = MainUI.class.getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        dialog.setScene(scene);
        dialog.showAndWait();
    }
}

