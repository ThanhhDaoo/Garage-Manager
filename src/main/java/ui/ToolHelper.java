package ui;

import dao.EmployeeDAO;
import dao.ToolDAO;
import model.Employee;
import model.Tool;
import model.ToolLog;
import util.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ToolHelper {

    private static TableView<Tool> tableView;
    private static ObservableList<Tool> masterData = FXCollections.observableArrayList();
    private static ObservableList<Tool> filteredData = FXCollections.observableArrayList();

    private static Label lblTotalTypes;
    private static Label lblTotalUsable;
    private static Label lblTotalDamaged;
    private static Label lblLowStockWarning;

    private static TextField txtSearch;
    private static ComboBox<String> cbStatusFilter;

    public static void showToolManagement(MainUI mainUI, StackPane contentArea) {
        contentArea.getChildren().clear();

        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header Title & Primary Action Buttons
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label headerTitle = new Label("🧰 Quản Lý Dụng Cụ & Vật Tư Tiêu Hao");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-text-fill: #1a237e;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button btnNewTool = new Button("➕ Thêm Dụng Cụ");
        btnNewTool.setMinWidth(Region.USE_PREF_SIZE);
        btnNewTool.setPrefHeight(38);
        btnNewTool.setStyle(
                "-fx-background-color: #2196F3;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8px 16px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        btnNewTool.setOnAction(e -> showToolForm(null, contentArea));

        Button btnPurchase = new Button("📥 Nhập Mua");
        btnPurchase.setMinWidth(Region.USE_PREF_SIZE);
        btnPurchase.setPrefHeight(38);
        btnPurchase.setStyle(
                "-fx-background-color: #2E7D32;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8px 16px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        btnPurchase.setOnAction(e -> showPurchaseDialog(null, contentArea));

        Button btnDamage = new Button("⚠️ Báo Hỏng");
        btnDamage.setMinWidth(Region.USE_PREF_SIZE);
        btnDamage.setPrefHeight(38);
        btnDamage.setStyle(
                "-fx-background-color: #E65100;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8px 16px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        btnDamage.setOnAction(e -> showDamageDialog(null, contentArea));

        header.getChildren().addAll(headerTitle, headerSpacer, btnNewTool, btnPurchase, btnDamage);

        // Summary Cards
        HBox summaryCards = new HBox(15);
        summaryCards.setAlignment(Pos.CENTER_LEFT);

        VBox card1 = createSummaryCard("TỔNG LOẠI DỤNG CỤ", "0 loại", "#E3F2FD", "#1565C0", 1);
        VBox card2 = createSummaryCard("ĐANG DÙNG TỐT", "0 cái", "#E8F5E9", "#2E7D32", 2);
        VBox card3 = createSummaryCard("ĐÃ HỎNG / HAO HỤT", "0 cái", "#FFF3E0", "#E65100", 3);
        VBox card4 = createSummaryCard("CẦN MUA THÊM", "0 loại", "#FFEBEE", "#C62828", 4);

        summaryCards.getChildren().addAll(card1, card2, card3, card4);

        // Filter Bar & History/Report Buttons
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(12, 16, 12, 16));
        filterBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 10, 0, 0, 4);"
        );

        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm mã, tên dụng cụ...");
        txtSearch.setPrefWidth(220);
        txtSearch.setMinWidth(150);
        HBox.setHgrow(txtSearch, Priority.SOMETIMES);
        txtSearch.setStyle(
                "-fx-background-color: #f5f5f5;" +
                        "-fx-padding: 8px 12px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: transparent;" +
                        "-fx-font-size: 13px;" +
                        "-fx-pref-height: 36px;"
        );
        UIUtils.setupIMEFix(txtSearch);

        cbStatusFilter = new ComboBox<>();
        cbStatusFilter.getItems().addAll("Tất cả trạng thái", "Đủ dùng", "Cần mua thêm");
        cbStatusFilter.setValue("Tất cả trạng thái");
        cbStatusFilter.setMinWidth(Region.USE_PREF_SIZE);
        cbStatusFilter.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 36px; -fx-pref-width: 140px;");
        cbStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterData());

        Region filterSpacer = new Region();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);

        Button btnLogs = new Button("📋 Nhật Ký");
        btnLogs.setMinWidth(Region.USE_PREF_SIZE);
        btnLogs.setPrefHeight(36);
        btnLogs.setStyle(
                "-fx-background-color: #EDE7F6;" +
                        "-fx-text-fill: #512DA8;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8px 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        btnLogs.setOnAction(e -> showLogsDialog(contentArea));

        Button btnMonthlyReport = new Button("📊 Báo Cáo Tháng");
        btnMonthlyReport.setMinWidth(Region.USE_PREF_SIZE);
        btnMonthlyReport.setPrefHeight(36);
        btnMonthlyReport.setStyle(
                "-fx-background-color: #E0F2F1;" +
                        "-fx-text-fill: #00796B;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8px 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        btnMonthlyReport.setOnAction(e -> showMonthlyReportDialog(contentArea));

        filterBar.getChildren().addAll(
                txtSearch, cbStatusFilter, filterSpacer, btnLogs, btnMonthlyReport
        );

        // Tools Table
        tableView = new TableView<>();
        tableView.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;"
        );

        TableColumn<Tool, Integer> colStt = new TableColumn<>("STT");
        colStt.setMinWidth(40);
        colStt.setPrefWidth(45);
        colStt.setMaxWidth(50);
        colStt.setStyle("-fx-alignment: CENTER;");
        colStt.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1));
            }
        });

        TableColumn<Tool, String> colCode = new TableColumn<>("Mã DC");
        colCode.setMinWidth(75);
        colCode.setPrefWidth(85);
        colCode.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        colCode.setCellValueFactory(new PropertyValueFactory<>("toolCode"));

        TableColumn<Tool, String> colName = new TableColumn<>("Tên Dụng Cụ");
        colName.setMinWidth(160);
        colName.setPrefWidth(200);
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #212121;");
                }
            }
        });

        TableColumn<Tool, String> colUnit = new TableColumn<>("ĐVT");
        colUnit.setMinWidth(55);
        colUnit.setPrefWidth(65);
        colUnit.setStyle("-fx-alignment: CENTER;");
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<Tool, Double> colTotal = new TableColumn<>("Tổng Nhập");
        colTotal.setMinWidth(70);
        colTotal.setPrefWidth(80);
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalQty"));
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(new DecimalFormat("#.##").format(item));
            }
        });

        TableColumn<Tool, Double> colDamaged = new TableColumn<>("Đã Hỏng/Mất");
        colDamaged.setMinWidth(80);
        colDamaged.setPrefWidth(95);
        colDamaged.setStyle("-fx-alignment: CENTER-RIGHT;");
        colDamaged.setCellValueFactory(new PropertyValueFactory<>("damagedQty"));
        colDamaged.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(new DecimalFormat("#.##").format(item));
                    if (item > 0) {
                        setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #E65100;");
                    } else {
                        setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #757575;");
                    }
                }
            }
        });

        TableColumn<Tool, Double> colUsable = new TableColumn<>("Còn Dùng Tốt");
        colUsable.setMinWidth(85);
        colUsable.setPrefWidth(100);
        colUsable.setStyle("-fx-alignment: CENTER-RIGHT;");
        colUsable.setCellValueFactory(new PropertyValueFactory<>("usableQty"));
        colUsable.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Tool t = getTableRow() != null ? getTableRow().getItem() : null;
                    setText(new DecimalFormat("#.##").format(item));
                    if (t != null && t.isLowStock()) {
                        setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: 800; -fx-text-fill: #D32F2F;");
                    } else {
                        setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: 800; -fx-text-fill: #2E7D32;");
                    }
                }
            }
        });

        TableColumn<Tool, Double> colMin = new TableColumn<>("Định Mức Tối Thiểu");
        colMin.setMinWidth(80);
        colMin.setPrefWidth(100);
        colMin.setStyle("-fx-alignment: CENTER-RIGHT;");
        colMin.setCellValueFactory(new PropertyValueFactory<>("minQty"));
        colMin.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(new DecimalFormat("#.##").format(item));
            }
        });

        TableColumn<Tool, Double> colCost = new TableColumn<>("Giá Nhập (VNĐ)");
        colCost.setMinWidth(95);
        colCost.setPrefWidth(110);
        colCost.setStyle("-fx-alignment: CENTER-RIGHT;");
        colCost.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        colCost.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(UIUtils.formatCurrency(item));
            }
        });

        TableColumn<Tool, Void> colStatus = new TableColumn<>("Trạng Thái");
        colStatus.setMinWidth(110);
        colStatus.setPrefWidth(120);
        colStatus.setStyle("-fx-alignment: CENTER;");
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Tool t = getTableRow().getItem();
                    Label pill = new Label();
                    pill.setPadding(new Insets(3, 8, 3, 8));
                    pill.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 10;");

                    if (t.isLowStock()) {
                        pill.setText("🔴 Cần mua thêm");
                        pill.setStyle(pill.getStyle() + "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828;");
                    } else {
                        pill.setText("🟢 Đủ dùng");
                        pill.setStyle(pill.getStyle() + "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;");
                    }
                    setGraphic(pill);
                }
            }
        });

        TableColumn<Tool, Void> colAction = new TableColumn<>("Thao Tác");
        colAction.setMinWidth(140);
        colAction.setPrefWidth(160);
        colAction.setStyle("-fx-alignment: CENTER;");
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnDamageRow = new Button("⚠️");
            private final Button btnEditRow = new Button("✏");
            private final Button btnDeleteRow = new Button("🗑");
            private final HBox pane = new HBox(6, btnDamageRow, btnEditRow, btnDeleteRow);

            {
                pane.setAlignment(Pos.CENTER);
                btnDamageRow.setTooltip(new Tooltip("Báo hỏng dụng cụ này"));
                btnDamageRow.setStyle("-fx-background-color: #FFF3E0; -fx-text-fill: #E65100; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 4 8;");
                btnDamageRow.setOnAction(e -> {
                    Tool t = getTableRow().getItem();
                    if (t != null) showDamageDialog(t, contentArea);
                });

                btnEditRow.setTooltip(new Tooltip("Sửa thông tin dụng cụ"));
                btnEditRow.setStyle("-fx-background-color: #E3F2FD; -fx-text-fill: #1976D2; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 4 8;");
                btnEditRow.setOnAction(e -> {
                    Tool t = getTableRow().getItem();
                    if (t != null) showToolForm(t, contentArea);
                });

                btnDeleteRow.setTooltip(new Tooltip("Xóa dụng cụ"));
                btnDeleteRow.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 4 8;");
                btnDeleteRow.setOnAction(e -> {
                    Tool t = getTableRow().getItem();
                    if (t != null) {
                        Alert alert = AlertHelper.createAlert(Alert.AlertType.CONFIRMATION, "Xác nhận xóa",
                                "Bạn có chắc chắn muốn xóa dụng cụ '" + t.getName() + "'?\nToàn bộ nhật ký liên quan cũng sẽ bị xóa.");
                        alert.showAndWait().ifPresent(res -> {
                            if (res == ButtonType.OK) {
                                new ToolDAO().deleteTool(t.getId());
                                loadData();
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        tableView.getColumns().addAll(
                colStt, colCode, colName, colUnit,
                colTotal, colDamaged, colUsable, colMin, colCost, colStatus, colAction
        );
        tableView.setItems(filteredData);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // Search listener
        javafx.animation.PauseTransition debounce = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
        debounce.setOnFinished(e -> filterData());
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> debounce.playFromStart());

        root.getChildren().addAll(header, summaryCards, filterBar, tableView);
        contentArea.getChildren().add(root);

        loadData();
    }

    private static VBox createSummaryCard(String title, String defaultValue, String bgColor, String textColor, int cardIndex) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12, 20, 12, 20));
        card.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-pref-width: 210px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 3);"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #757575; -fx-letter-spacing: 0.5px;");

        Label lblValue = new Label(defaultValue);
        lblValue.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: " + textColor + ";");

        switch (cardIndex) {
            case 1 -> lblTotalTypes = lblValue;
            case 2 -> lblTotalUsable = lblValue;
            case 3 -> lblTotalDamaged = lblValue;
            case 4 -> lblLowStockWarning = lblValue;
        }

        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }

    private static void loadData() {
        ToolDAO dao = new ToolDAO();
        List<Tool> list = dao.getAllTools();
        masterData.setAll(list);
        filterData();
    }

    private static void filterData() {
        String search = txtSearch != null && txtSearch.getText() != null ? txtSearch.getText().trim().toLowerCase() : "";
        String status = cbStatusFilter != null ? cbStatusFilter.getValue() : "Tất cả trạng thái";

        List<Tool> filtered = new ArrayList<>();
        double totalUsable = 0;
        double totalDamaged = 0;
        int lowStockCount = 0;

        for (Tool t : masterData) {
            totalUsable += t.getUsableQty();
            totalDamaged += t.getDamagedQty();
            if (t.isLowStock()) lowStockCount++;

            boolean matchSearch = search.isEmpty() ||
                    (t.getToolCode() != null && t.getToolCode().toLowerCase().contains(search)) ||
                    (t.getName() != null && t.getName().toLowerCase().contains(search));

            boolean matchStatus = true;
            if ("Đủ dùng".equals(status)) {
                matchStatus = !t.isLowStock();
            } else if ("Cần mua thêm".equals(status)) {
                matchStatus = t.isLowStock();
            }

            if (matchSearch && matchStatus) {
                filtered.add(t);
            }
        }

        filteredData.setAll(filtered);

        if (lblTotalTypes != null) lblTotalTypes.setText(masterData.size() + " loại");
        if (lblTotalUsable != null) lblTotalUsable.setText(new DecimalFormat("#.##").format(totalUsable) + " cái");
        if (lblTotalDamaged != null) lblTotalDamaged.setText(new DecimalFormat("#.##").format(totalDamaged) + " cái");
        if (lblLowStockWarning != null) {
            lblLowStockWarning.setText(lowStockCount + " loại");
            lblLowStockWarning.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: " + (lowStockCount > 0 ? "#C62828" : "#2E7D32") + ";");
        }
    }

    private static Label createFieldLabel(String text, String style) {
        Label lbl = new Label(text);
        lbl.setStyle(style);
        lbl.setMinWidth(Region.USE_PREF_SIZE);
        return lbl;
    }

    // ==================== DIALOG 1: FORM THÊM / SỬA DỤNG CỤ ====================
    private static void showToolForm(Tool existing, StackPane container) {
        boolean isEdit = existing != null;
        ToolDAO dao = new ToolDAO();

        BorderPane dialogRoot = new BorderPane();
        dialogRoot.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        dialogRoot.setPrefWidth(580);
        dialogRoot.setMaxWidth(620);
        dialogRoot.setMaxHeight(520);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));

        Label title = new Label(isEdit ? "✏ Sửa Thông Tin Dụng Cụ" : "➕ Thêm Dụng Cụ Mới");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(175);
        col0.setPrefWidth(185);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);
        grid.getColumnConstraints().addAll(col0, col1);

        String labelStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #424242;";
        String fieldStyle = "-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 13px;";

        TextField txtCode = new TextField(isEdit ? existing.getToolCode() : dao.generateToolCode());
        txtCode.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtCode);

        TextField txtName = new TextField(isEdit ? existing.getName() : "");
        txtName.setPromptText("VD: Khăn lau microfiber, Cờ lê 10...");
        txtName.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtName);

        TextField txtUnit = new TextField(isEdit ? existing.getUnit() : "Cái");
        txtUnit.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtUnit);

        TextField txtMinQty = new TextField(isEdit ? new DecimalFormat("#.##").format(existing.getMinQty()) : "5");
        txtMinQty.setPromptText("Cảnh báo khi dưới mức này");
        txtMinQty.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtMinQty);

        TextField txtPrice = new TextField(isEdit ? UIUtils.formatCurrency(existing.getCostPrice()) : "0");
        txtPrice.setStyle(fieldStyle);
        UIUtils.formatCurrencyInput(txtPrice);

        TextArea txtNotes = new TextArea(isEdit && existing.getNotes() != null ? existing.getNotes() : "");
        txtNotes.setPromptText("Ghi chú thêm nếu có...");
        txtNotes.setStyle(fieldStyle);
        txtNotes.setPrefRowCount(2);

        grid.add(createFieldLabel("Mã dụng cụ *", labelStyle), 0, 0); grid.add(txtCode, 1, 0);
        grid.add(createFieldLabel("Tên dụng cụ *", labelStyle), 0, 1); grid.add(txtName, 1, 1);
        grid.add(createFieldLabel("Đơn vị tính", labelStyle), 0, 2); grid.add(txtUnit, 1, 2);
        grid.add(createFieldLabel("Định mức tối thiểu", labelStyle), 0, 3); grid.add(txtMinQty, 1, 3);
        grid.add(createFieldLabel("Đơn giá tham khảo (đ)", labelStyle), 0, 4); grid.add(txtPrice, 1, 4);
        grid.add(createFieldLabel("Ghi chú", labelStyle), 0, 5); grid.add(txtNotes, 1, 5);

        content.getChildren().addAll(title, grid);
        dialogRoot.setCenter(content);

        // Buttons
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");

        HBox btnBar = new HBox(12);
        btnBar.setAlignment(Pos.CENTER_RIGHT);
        btnBar.setPadding(new Insets(15, 25, 15, 25));
        btnBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        Button btnCancel = new Button("Hủy");
        btnCancel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> container.getChildren().remove(overlay));

        Button btnSave = new Button("Lưu");
        btnSave.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        btnSave.setOnAction(e -> {
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            if (code.isEmpty() || name.isEmpty()) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập Mã và Tên dụng cụ!").show();
                return;
            }

            double minQty = 0;
            try { minQty = Double.parseDouble(txtMinQty.getText().trim()); } catch (Exception ignored) {}
            double price = UIUtils.parseCurrency(txtPrice.getText().trim());

            if (isEdit) {
                existing.setToolCode(code);
                existing.setName(name);
                existing.setCategory("");
                existing.setUnit(txtUnit.getText().trim());
                existing.setMinQty(minQty);
                existing.setCostPrice(price);
                existing.setNotes(txtNotes.getText().trim());
                dao.updateTool(existing);
            } else {
                Tool t = new Tool(0, code, name, "", txtUnit.getText().trim(), 0, 0, 0, minQty, price, "Đang sử dụng", txtNotes.getText().trim(), null);
                dao.addTool(t);
            }

            container.getChildren().remove(overlay);
            loadData();
        });

        btnBar.getChildren().addAll(btnCancel, btnSave);
        dialogRoot.setBottom(btnBar);

        overlay.getChildren().add(dialogRoot);
        container.getChildren().add(overlay);
    }

    // ==================== DIALOG 2: NHẬP MUA DỤNG CỤ (TỰ ĐỘNG BẮN CHI PHÍ BIẾN THIÊN) ====================
    private static void showPurchaseDialog(Tool preSelected, StackPane container) {
        ToolDAO dao = new ToolDAO();
        List<Tool> allTools = dao.getAllTools();
        if (allTools.isEmpty()) {
            AlertHelper.createAlert(Alert.AlertType.WARNING, "Thông báo", "Chưa có dụng cụ nào trong danh mục. Vui lòng bấm '➕ Thêm Dụng Cụ' trước!").show();
            return;
        }

        BorderPane dialogRoot = new BorderPane();
        dialogRoot.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        dialogRoot.setPrefWidth(580);
        dialogRoot.setMaxWidth(620);
        dialogRoot.setMaxHeight(500);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));

        Label title = new Label("📥 Nhập Mua Dụng Cụ Mới");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Label subtitle = new Label("💡 Khoản tiền nhập mua này sẽ TỰ ĐỘNG đồng bộ vào mục 'Chi phí biến thiên' của tháng!");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #1565C0; -fx-font-style: italic;");
        subtitle.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(175);
        col0.setPrefWidth(185);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);
        grid.getColumnConstraints().addAll(col0, col1);

        String labelStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #424242;";
        String fieldStyle = "-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 13px;";

        ComboBox<Tool> cbTools = new ComboBox<>();
        cbTools.getItems().addAll(allTools);
        cbTools.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Tool item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getToolCode() + " - " + item.getName() + " (Còn tốt: " + new DecimalFormat("#.##").format(item.getUsableQty()) + " " + item.getUnit() + ")");
            }
        });
        cbTools.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tool item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getToolCode() + " - " + item.getName());
            }
        });
        cbTools.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-pref-height: 36px;");
        cbTools.setMaxWidth(Double.MAX_VALUE);
        if (preSelected != null) {
            for (Tool t : allTools) {
                if (t.getId() == preSelected.getId()) { cbTools.setValue(t); break; }
            }
        } else if (!allTools.isEmpty()) {
            cbTools.setValue(allTools.get(0));
        }

        TextField txtQty = new TextField("1");
        txtQty.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtQty);

        TextField txtUnitPrice = new TextField(cbTools.getValue() != null ? UIUtils.formatCurrency(cbTools.getValue().getCostPrice()) : "0");
        txtUnitPrice.setStyle(fieldStyle);
        UIUtils.formatCurrencyInput(txtUnitPrice);

        TextField txtTotal = new TextField("0");
        txtTotal.setEditable(false);
        txtTotal.setStyle(fieldStyle + "-fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Runnable updateCalc = () -> {
            try {
                double q = Double.parseDouble(txtQty.getText().trim());
                double p = UIUtils.parseCurrency(txtUnitPrice.getText().trim());
                txtTotal.setText(UIUtils.formatCurrency(q * p));
            } catch (Exception e) {
                txtTotal.setText("0");
            }
        };

        txtQty.textProperty().addListener((obs, o, n) -> updateCalc.run());
        txtUnitPrice.textProperty().addListener((obs, o, n) -> updateCalc.run());

        cbTools.valueProperty().addListener((obs, o, n) -> {
            if (n != null) {
                txtUnitPrice.setText(UIUtils.formatCurrency(n.getCostPrice()));
                updateCalc.run();
            }
        });
        updateCalc.run();

        DatePicker dpDate = new DatePicker(LocalDate.now());
        UIUtils.formatDatePicker(dpDate);
        dpDate.setStyle("-fx-font-size: 13px; -fx-pref-height: 36px;");
        dpDate.setMaxWidth(Double.MAX_VALUE);

        TextField txtNotes = new TextField();
        txtNotes.setPromptText("Nhà cung cấp / nơi mua...");
        txtNotes.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtNotes);

        grid.add(createFieldLabel("Chọn dụng cụ *", labelStyle), 0, 0); grid.add(cbTools, 1, 0);
        grid.add(createFieldLabel("Số lượng mua *", labelStyle), 0, 1); grid.add(txtQty, 1, 1);
        grid.add(createFieldLabel("Đơn giá nhập (đ) *", labelStyle), 0, 2); grid.add(txtUnitPrice, 1, 2);
        grid.add(createFieldLabel("Thành tiền (đ)", labelStyle), 0, 3); grid.add(txtTotal, 1, 3);
        grid.add(createFieldLabel("Ngày mua *", labelStyle), 0, 4); grid.add(dpDate, 1, 4);
        grid.add(createFieldLabel("Ghi chú / Nguồn mua", labelStyle), 0, 5); grid.add(txtNotes, 1, 5);

        content.getChildren().addAll(title, subtitle, grid);
        dialogRoot.setCenter(content);

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");

        HBox btnBar = new HBox(12);
        btnBar.setAlignment(Pos.CENTER_RIGHT);
        btnBar.setPadding(new Insets(15, 25, 15, 25));
        btnBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        Button btnCancel = new Button("Hủy");
        btnCancel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> container.getChildren().remove(overlay));

        Button btnSave = new Button("Xác nhận nhập kho & ghi chi phí");
        btnSave.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        btnSave.setOnAction(e -> {
            Tool tool = cbTools.getValue();
            if (tool == null) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn dụng cụ!").show();
                return;
            }

            double qty = 0;
            try {
                qty = Double.parseDouble(txtQty.getText().trim());
                if (qty <= 0) throw new Exception();
            } catch (Exception ex) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng mua phải lớn hơn 0!").show();
                return;
            }

            double unitPrice = UIUtils.parseCurrency(txtUnitPrice.getText().trim());
            LocalDate date = dpDate.getValue() != null ? dpDate.getValue() : LocalDate.now();

            boolean success = dao.recordPurchase(tool.getId(), qty, unitPrice, date.toString(), txtNotes.getText().trim());
            if (success) {
                container.getChildren().remove(overlay);
                loadData();
                AlertHelper.createAlert(Alert.AlertType.INFORMATION, "Thành công",
                        "Đã nhập thêm " + new DecimalFormat("#.##").format(qty) + " " + tool.getUnit() + " '" + tool.getName() + "' thành công!\n" +
                                "Đã tự động tạo 1 khoản chi phí biến thiên: " + UIUtils.formatCurrency(qty * unitPrice) + " đ vào tháng " + date.toString().substring(0, 7)).show();
            } else {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu thông tin nhập mua!").show();
            }
        });

        btnBar.getChildren().addAll(btnCancel, btnSave);
        dialogRoot.setBottom(btnBar);

        overlay.getChildren().add(dialogRoot);
        container.getChildren().add(overlay);
    }

    // ==================== DIALOG 3: BÁO HỎNG / HAO HỤT DỤNG CỤ ====================
    private static void showDamageDialog(Tool preSelected, StackPane container) {
        ToolDAO dao = new ToolDAO();
        List<Tool> allTools = dao.getAllTools();
        if (allTools.isEmpty()) {
            AlertHelper.createAlert(Alert.AlertType.WARNING, "Thông báo", "Chưa có dụng cụ nào trong danh mục!").show();
            return;
        }

        List<Employee> allEmployees = new EmployeeDAO().getAllEmployees();

        BorderPane dialogRoot = new BorderPane();
        dialogRoot.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        dialogRoot.setPrefWidth(580);
        dialogRoot.setMaxWidth(620);
        dialogRoot.setMaxHeight(500);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));

        Label title = new Label("⚠️ Báo Hỏng / Hao Hụt Dụng Cụ");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E65100;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(175);
        col0.setPrefWidth(185);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);
        grid.getColumnConstraints().addAll(col0, col1);

        String labelStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #424242;";
        String fieldStyle = "-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 13px;";

        ComboBox<Tool> cbTools = new ComboBox<>();
        cbTools.getItems().addAll(allTools);
        cbTools.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Tool item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getToolCode() + " - " + item.getName() + " (Hiện còn: " + new DecimalFormat("#.##").format(item.getUsableQty()) + " " + item.getUnit() + ")");
            }
        });
        cbTools.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tool item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getToolCode() + " - " + item.getName());
            }
        });
        cbTools.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-pref-height: 36px;");
        cbTools.setMaxWidth(Double.MAX_VALUE);
        if (preSelected != null) {
            for (Tool t : allTools) {
                if (t.getId() == preSelected.getId()) { cbTools.setValue(t); break; }
            }
        } else if (!allTools.isEmpty()) {
            cbTools.setValue(allTools.get(0));
        }

        Label lblCurrentUsable = new Label();
        lblCurrentUsable.setStyle("-fx-font-size: 12px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
        Runnable updateCurrentStock = () -> {
            Tool t = cbTools.getValue();
            if (t != null) {
                lblCurrentUsable.setText("Kho hiện có: " + new DecimalFormat("#.##").format(t.getUsableQty()) + " " + t.getUnit() + " còn dùng tốt.");
            }
        };
        cbTools.valueProperty().addListener((obs, o, n) -> updateCurrentStock.run());
        updateCurrentStock.run();

        TextField txtQty = new TextField("1");
        txtQty.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtQty);

        ComboBox<String> cbEmployee = new ComboBox<>();
        cbEmployee.setEditable(true);
        for (Employee emp : allEmployees) {
            cbEmployee.getItems().add(emp.getName());
        }
        cbEmployee.setPromptText("Chọn hoặc nhập tên thợ làm hỏng/báo hỏng");
        cbEmployee.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-pref-height: 36px;");
        cbEmployee.setMaxWidth(Double.MAX_VALUE);

        DatePicker dpDate = new DatePicker(LocalDate.now());
        UIUtils.formatDatePicker(dpDate);
        dpDate.setStyle("-fx-font-size: 13px; -fx-pref-height: 36px;");
        dpDate.setMaxWidth(Double.MAX_VALUE);

        TextField txtReason = new TextField();
        txtReason.setPromptText("VD: Bị rách, gãy đầu vặn, dính dầu không tẩy được, rơi mất...");
        txtReason.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtReason);

        TextField txtNotes = new TextField();
        txtNotes.setPromptText("Ghi chú bổ sung nếu có...");
        txtNotes.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtNotes);

        grid.add(createFieldLabel("Dụng cụ bị hỏng *", labelStyle), 0, 0); grid.add(new VBox(4, cbTools, lblCurrentUsable), 1, 0);
        grid.add(createFieldLabel("Số lượng hỏng/mất *", labelStyle), 0, 1); grid.add(txtQty, 1, 1);
        grid.add(createFieldLabel("Thợ làm hỏng / Báo cáo", labelStyle), 0, 2); grid.add(cbEmployee, 1, 2);
        grid.add(createFieldLabel("Ngày ghi nhận *", labelStyle), 0, 3); grid.add(dpDate, 1, 3);
        grid.add(createFieldLabel("Lý do hỏng/rách *", labelStyle), 0, 4); grid.add(txtReason, 1, 4);
        grid.add(createFieldLabel("Ghi chú", labelStyle), 0, 5); grid.add(txtNotes, 1, 5);

        content.getChildren().addAll(title, grid);
        dialogRoot.setCenter(content);

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");

        HBox btnBar = new HBox(12);
        btnBar.setAlignment(Pos.CENTER_RIGHT);
        btnBar.setPadding(new Insets(15, 25, 15, 25));
        btnBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        Button btnCancel = new Button("Hủy");
        btnCancel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> container.getChildren().remove(overlay));

        Button btnSave = new Button("Xác nhận báo hỏng");
        btnSave.setStyle("-fx-background-color: #E65100; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        btnSave.setOnAction(e -> {
            Tool tool = cbTools.getValue();
            if (tool == null) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn dụng cụ!").show();
                return;
            }

            double qty = 0;
            try {
                qty = Double.parseDouble(txtQty.getText().trim());
                if (qty <= 0) throw new Exception();
            } catch (Exception ex) {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng hỏng phải lớn hơn 0!").show();
                return;
            }

            if (qty > tool.getUsableQty()) {
                AlertHelper.createAlert(Alert.AlertType.WARNING, "Cảnh báo",
                        "Số lượng báo hỏng (" + new DecimalFormat("#.##").format(qty) + ") lớn hơn số lượng còn dùng tốt trong kho (" +
                                new DecimalFormat("#.##").format(tool.getUsableQty()) + ")!").show();
            }

            String empName = cbEmployee.getValue() != null ? cbEmployee.getValue().trim() : "";
            LocalDate date = dpDate.getValue() != null ? dpDate.getValue() : LocalDate.now();
            String reason = txtReason.getText().trim();

            boolean success = dao.recordDamage(tool.getId(), qty, empName, date.toString(), reason, txtNotes.getText().trim());
            if (success) {
                container.getChildren().remove(overlay);
                loadData();
                AlertHelper.createAlert(Alert.AlertType.INFORMATION, "Thành công",
                        "Đã ghi nhận báo hỏng " + new DecimalFormat("#.##").format(qty) + " " + tool.getUnit() + " '" + tool.getName() + "'.\n" +
                                "Số lượng còn dùng tốt hiện tại: " + new DecimalFormat("#.##").format(Math.max(0, tool.getUsableQty() - qty)) + " " + tool.getUnit()).show();
            } else {
                AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu báo hỏng!").show();
            }
        });

        btnBar.getChildren().addAll(btnCancel, btnSave);
        dialogRoot.setBottom(btnBar);

        overlay.getChildren().add(dialogRoot);
        container.getChildren().add(overlay);
    }

    // ==================== DIALOG 4: NHẬT KÝ BIẾN ĐỘNG (LỊCH SỬ NHẬP MUA & BÁO HỎNG) ====================
    private static void showLogsDialog(StackPane container) {
        ToolDAO dao = new ToolDAO();

        BorderPane dialogRoot = new BorderPane();
        dialogRoot.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        dialogRoot.setMaxWidth(850);
        dialogRoot.setMaxHeight(600);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("📋 Nhật Ký Biến Động Dụng Cụ (Nhập mua & Báo hỏng)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #512DA8;");

        // Filter Bar (Tháng trước, Năm sau)
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        Label lblMonth = new Label("Tháng:");
        lblMonth.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #424242;");
        ComboBox<String> cbMonth = new ComboBox<>();
        cbMonth.getItems().add("Tất cả");
        for (int m = 1; m <= 12; m++) {
            cbMonth.getItems().add(String.format("%02d", m));
        }
        cbMonth.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
        cbMonth.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 36px; -fx-pref-width: 85px;");
        cbMonth.setMinWidth(85);

        Label lblYear = new Label("Năm:");
        lblYear.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #424242;");
        ComboBox<String> cbYear = new ComboBox<>();
        int curYear = LocalDate.now().getYear();
        for (int y = curYear + 1; y >= curYear - 4; y--) {
            cbYear.getItems().add(String.valueOf(y));
        }
        cbYear.setValue(String.valueOf(curYear));
        cbYear.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 36px; -fx-pref-width: 90px;");
        cbYear.setMinWidth(90);

        filterBar.getChildren().addAll(lblMonth, cbMonth, lblYear, cbYear);

        TableView<ToolLog> logTable = new TableView<>();
        logTable.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");

        TableColumn<ToolLog, String> colDate = new TableColumn<>("Ngày");
        colDate.setMinWidth(85);
        colDate.setPrefWidth(90);
        colDate.setStyle("-fx-alignment: CENTER;");
        colDate.setCellValueFactory(new PropertyValueFactory<>("logDate"));

        TableColumn<ToolLog, String> colType = new TableColumn<>("Hoạt Động");
        colType.setMinWidth(110);
        colType.setPrefWidth(125);
        colType.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getActionTypeDisplay()));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    if (item.contains("Nhập mua")) {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #2E7D32;");
                    } else if (item.contains("Báo hỏng")) {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #E65100;");
                    } else {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #757575;");
                    }
                }
            }
        });

        TableColumn<ToolLog, String> colToolName = new TableColumn<>("Tên Dụng Cụ");
        colToolName.setMinWidth(150);
        colToolName.setPrefWidth(170);
        colToolName.setCellValueFactory(new PropertyValueFactory<>("toolName"));

        TableColumn<ToolLog, Double> colQty = new TableColumn<>("Số Lượng");
        colQty.setMinWidth(65);
        colQty.setPrefWidth(75);
        colQty.setStyle("-fx-alignment: CENTER-RIGHT;");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQty.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(new DecimalFormat("#.##").format(item));
            }
        });

        TableColumn<ToolLog, Double> colTotal = new TableColumn<>("Tổng Tiền (đ)");
        colTotal.setMinWidth(85);
        colTotal.setPrefWidth(100);
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item <= 0) setText("-");
                else setText(UIUtils.formatCurrency(item));
            }
        });

        TableColumn<ToolLog, String> colEmp = new TableColumn<>("Người Thực Hiện");
        colEmp.setMinWidth(100);
        colEmp.setPrefWidth(120);
        colEmp.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<ToolLog, String> colReason = new TableColumn<>("Lý Do / Ghi Chú");
        colReason.setMinWidth(130);
        colReason.setPrefWidth(160);
        colReason.setCellValueFactory(cellData -> {
            ToolLog l = cellData.getValue();
            String res = "";
            if (l.getReason() != null && !l.getReason().isEmpty()) res += l.getReason();
            if (l.getNotes() != null && !l.getNotes().isEmpty()) {
                if (!res.isEmpty()) res += " - ";
                res += l.getNotes();
            }
            return new javafx.beans.property.SimpleStringProperty(res);
        });

        TableColumn<ToolLog, Void> colAction = new TableColumn<>("Xóa");
        colAction.setMinWidth(60);
        colAction.setPrefWidth(65);
        colAction.setStyle("-fx-alignment: CENTER;");
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnDel = new Button("🗑");
            {
                btnDel.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 3 6;");
                btnDel.setOnAction(e -> {
                    ToolLog l = getTableRow().getItem();
                    if (l != null) {
                        Alert alert = AlertHelper.createAlert(Alert.AlertType.CONFIRMATION, "Xác nhận xóa nhật ký",
                                "Bạn có chắc chắn muốn xóa dòng nhật ký này?\nSố lượng dụng cụ và chi phí tương ứng sẽ được hoàn tác.");
                        alert.showAndWait().ifPresent(res -> {
                            if (res == ButtonType.OK) {
                                dao.deleteToolLog(l.getId());
                                logTable.getItems().setAll(dao.getLogsByMonth(cbMonth.getValue()));
                                loadData();
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDel);
                }
            }
        });

        logTable.getColumns().addAll(colDate, colType, colToolName, colQty, colTotal, colEmp, colReason, colAction);
        VBox.setVgrow(logTable, Priority.ALWAYS);

        Runnable loadLogs = () -> {
            String m = cbMonth.getValue();
            String y = cbYear.getValue();
            if (m == null || "Tất cả".equals(m)) {
                logTable.getItems().setAll(dao.getLogsByMonth(y));
            } else {
                logTable.getItems().setAll(dao.getLogsByMonth(y + "-" + m));
            }
        };
        cbMonth.valueProperty().addListener((obs, o, n) -> loadLogs.run());
        cbYear.valueProperty().addListener((obs, o, n) -> loadLogs.run());
        loadLogs.run();

        content.getChildren().addAll(title, filterBar, logTable);
        dialogRoot.setCenter(content);

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");

        HBox btnBar = new HBox(12);
        btnBar.setAlignment(Pos.CENTER_RIGHT);
        btnBar.setPadding(new Insets(12, 20, 12, 20));
        btnBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        Button btnClose = new Button("Đóng");
        btnClose.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        btnClose.setOnAction(e -> container.getChildren().remove(overlay));

        btnBar.getChildren().add(btnClose);
        dialogRoot.setBottom(btnBar);

        overlay.getChildren().add(dialogRoot);
        container.getChildren().add(overlay);
    }

    // ==================== DIALOG 5: TỔNG KẾT & BÁO CÁO KIỂM KÊ THÁNG ====================
    private static void showMonthlyReportDialog(StackPane container) {
        ToolDAO dao = new ToolDAO();
        List<Tool> allTools = dao.getAllTools();

        BorderPane dialogRoot = new BorderPane();
        dialogRoot.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        dialogRoot.setMaxWidth(800);
        dialogRoot.setMaxHeight(600);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("📊 Tổng Kết & Báo Cáo Kiểm Kê Dụng Cụ Cuối Tháng");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00796B;");

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblMonth = new Label("Tháng:");
        lblMonth.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #00796B;");
        ComboBox<String> cbMonth = new ComboBox<>();
        for (int m = 1; m <= 12; m++) {
            cbMonth.getItems().add(String.format("%02d", m));
        }
        cbMonth.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
        cbMonth.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 36px; -fx-pref-width: 80px;");
        cbMonth.setMinWidth(80);

        Label lblYear = new Label("Năm:");
        lblYear.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #00796B;");
        ComboBox<String> cbYear = new ComboBox<>();
        int curYear = LocalDate.now().getYear();
        for (int y = curYear + 1; y >= curYear - 4; y--) {
            cbYear.getItems().add(String.valueOf(y));
        }
        cbYear.setValue(String.valueOf(curYear));
        cbYear.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 36px; -fx-pref-width: 90px;");
        cbYear.setMinWidth(90);

        topBar.getChildren().addAll(lblMonth, cbMonth, lblYear, cbYear);

        // Summary text area / table
        TableView<MonthlyReportRow> reportTable = new TableView<>();
        reportTable.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");

        TableColumn<MonthlyReportRow, String> colCode = new TableColumn<>("Mã");
        colCode.setMinWidth(70);
        colCode.setPrefWidth(80);
        colCode.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        colCode.setCellValueFactory(new PropertyValueFactory<>("toolCode"));

        TableColumn<MonthlyReportRow, String> colName = new TableColumn<>("Tên Dụng Cụ");
        colName.setMinWidth(150);
        colName.setPrefWidth(180);
        colName.setCellValueFactory(new PropertyValueFactory<>("toolName"));

        TableColumn<MonthlyReportRow, String> colPurchased = new TableColumn<>("Nhập Trong Tháng");
        colPurchased.setMinWidth(100);
        colPurchased.setPrefWidth(110);
        colPurchased.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        colPurchased.setCellValueFactory(new PropertyValueFactory<>("purchasedInMonth"));

        TableColumn<MonthlyReportRow, String> colDamaged = new TableColumn<>("Hỏng Trong Tháng");
        colDamaged.setMinWidth(100);
        colDamaged.setPrefWidth(110);
        colDamaged.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #E65100;");
        colDamaged.setCellValueFactory(new PropertyValueFactory<>("damagedInMonth"));

        TableColumn<MonthlyReportRow, String> colRemaining = new TableColumn<>("Hiện Còn Tốt");
        colRemaining.setMinWidth(90);
        colRemaining.setPrefWidth(100);
        colRemaining.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: 800; -fx-text-fill: #1976D2;");
        colRemaining.setCellValueFactory(new PropertyValueFactory<>("currentUsable"));

        TableColumn<MonthlyReportRow, String> colSuggest = new TableColumn<>("Đề Xuất Mua Thêm");
        colSuggest.setMinWidth(110);
        colSuggest.setPrefWidth(130);
        colSuggest.setStyle("-fx-alignment: CENTER;");
        colSuggest.setCellValueFactory(new PropertyValueFactory<>("suggestion"));

        reportTable.getColumns().addAll(colCode, colName, colPurchased, colDamaged, colRemaining, colSuggest);
        VBox.setVgrow(reportTable, Priority.ALWAYS);

        Runnable refreshReport = () -> {
            String month = cbYear.getValue() + "-" + cbMonth.getValue();
            List<ToolLog> logs = dao.getLogsByMonth(month);

            java.util.Map<Integer, Double> purchasedMap = new java.util.HashMap<>();
            java.util.Map<Integer, Double> damagedMap = new java.util.HashMap<>();

            for (ToolLog log : logs) {
                if ("NHAP_MUA".equalsIgnoreCase(log.getActionType())) {
                    purchasedMap.put(log.getToolId(), purchasedMap.getOrDefault(log.getToolId(), 0.0) + log.getQuantity());
                } else if ("BAO_HONG".equalsIgnoreCase(log.getActionType())) {
                    damagedMap.put(log.getToolId(), damagedMap.getOrDefault(log.getToolId(), 0.0) + log.getQuantity());
                }
            }

            List<MonthlyReportRow> rows = new ArrayList<>();
            for (Tool t : allTools) {
                double pQty = purchasedMap.getOrDefault(t.getId(), 0.0);
                double dQty = damagedMap.getOrDefault(t.getId(), 0.0);

                String sug = "-";
                if (t.isLowStock()) {
                    double need = Math.max(0, t.getMinQty() * 2 - t.getUsableQty());
                    sug = "🔴 Mua thêm ~" + new DecimalFormat("#.##").format(need) + " " + t.getUnit();
                } else {
                    sug = "🟢 Đủ dùng";
                }

                rows.add(new MonthlyReportRow(
                        t.getToolCode(),
                        t.getName(),
                        new DecimalFormat("#.##").format(pQty) + " " + t.getUnit(),
                        new DecimalFormat("#.##").format(dQty) + " " + t.getUnit(),
                        new DecimalFormat("#.##").format(t.getUsableQty()) + " " + t.getUnit(),
                        sug
                ));
            }
            reportTable.getItems().setAll(rows);
        };

        cbMonth.valueProperty().addListener((obs, o, n) -> refreshReport.run());
        cbYear.valueProperty().addListener((obs, o, n) -> refreshReport.run());
        refreshReport.run();

        content.getChildren().addAll(title, topBar, reportTable);
        dialogRoot.setCenter(content);

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");

        HBox btnBar = new HBox(12);
        btnBar.setAlignment(Pos.CENTER_RIGHT);
        btnBar.setPadding(new Insets(12, 20, 12, 20));
        btnBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        Button btnClose = new Button("Đóng");
        btnClose.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        btnClose.setOnAction(e -> container.getChildren().remove(overlay));

        btnBar.getChildren().add(btnClose);
        dialogRoot.setBottom(btnBar);

        overlay.getChildren().add(dialogRoot);
        container.getChildren().add(overlay);
    }

    public static class MonthlyReportRow {
        private final String toolCode;
        private final String toolName;
        private final String purchasedInMonth;
        private final String damagedInMonth;
        private final String currentUsable;
        private final String suggestion;

        public MonthlyReportRow(String toolCode, String toolName, String purchasedInMonth, String damagedInMonth, String currentUsable, String suggestion) {
            this.toolCode = toolCode;
            this.toolName = toolName;
            this.purchasedInMonth = purchasedInMonth;
            this.damagedInMonth = damagedInMonth;
            this.currentUsable = currentUsable;
            this.suggestion = suggestion;
        }

        public String getToolCode() { return toolCode; }
        public String getToolName() { return toolName; }
        public String getPurchasedInMonth() { return purchasedInMonth; }
        public String getDamagedInMonth() { return damagedInMonth; }
        public String getCurrentUsable() { return currentUsable; }
        public String getSuggestion() { return suggestion; }
    }
}

