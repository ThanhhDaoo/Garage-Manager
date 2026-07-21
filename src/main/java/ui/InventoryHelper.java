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
        txtSearch.setPromptText("🔍 Tìm theo tên sản phẩm, mã phiếu, người thực hiện...");
        txtSearch.setPrefWidth(380);
        txtSearch.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 13px;"
        );
        UIUtils.setupIMEFix(txtSearch);

        // Month filter
        cbMonth = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cbMonth.getItems().add(String.format("%02d", i));
        }
        cbMonth.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
        cbMonth.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 38px; -fx-pref-width: 90px;");

        // Year filter
        cbYear = new ComboBox<>();
        int curYear = LocalDate.now().getYear();
        for (int i = curYear - 5; i <= curYear + 5; i++) {
            cbYear.getItems().add(String.valueOf(i));
        }
        cbYear.setValue(String.valueOf(curYear));
        cbYear.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-pref-height: 38px; -fx-pref-width: 100px;");

        Button btnNewReceipt = new Button("➕ Tạo Phiếu Nhập Kho");
        btnNewReceipt.setStyle(
            "-fx-background-color: #1976D2;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 18;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnNewReceipt.setOnAction(e -> {
            Window owner = contentArea.getScene().getWindow();
            new InventoryReceiptForm(() -> loadReceiptsData()).show();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnPdf = new Button("📄 Xuất PDF");
        btnPdf.setStyle(
            "-fx-background-color: #FFE0B2;" +
            "-fx-text-fill: #E65100;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
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
            "-fx-padding: 10 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnExcel.setOnAction(e -> {
            String monthStr = cbYear.getValue() + "-" + cbMonth.getValue();
            ReportHelper.exportInventoryToExcel(filteredData, monthStr, contentArea.getScene().getWindow());
        });

        filterBar.getChildren().addAll(
            txtSearch, new Label("Tháng:"), cbMonth, new Label("Năm:"), cbYear, btnNewReceipt, spacer, btnPdf, btnExcel
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

        TableColumn<InventoryReceipt, String> colProvider = new TableColumn<>("Nhà Cung Cấp");
        colProvider.setPrefWidth(130);
        colProvider.setCellValueFactory(new PropertyValueFactory<>("provider"));

        TableColumn<InventoryReceipt, String> colOperator = new TableColumn<>("Người Thực Hiện");
        colOperator.setPrefWidth(120);
        colOperator.setCellValueFactory(new PropertyValueFactory<>("operator"));

        TableColumn<InventoryReceipt, String> colNotes = new TableColumn<>("Ghi Chú");
        colNotes.setPrefWidth(160);
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        tableView.getColumns().addAll(
            colStt, colCode, colDate, colProduct, colQty, colPrice, colTotal, colProvider, colOperator, colNotes
        );
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
                    (r.getProvider() != null && r.getProvider().toLowerCase().contains(q)) ||
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
}
