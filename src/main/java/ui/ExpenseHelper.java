package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.FixedExpense;
import service.FixedExpenseService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpenseHelper {

    public static void showExpenseManagement(MainUI mainUI, StackPane contentArea) {
        contentArea.getChildren().clear();

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Top bar: Search & Filters
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 20, 0, 0)); // Thêm padding phải 20px để tránh khuất nút

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm khoản chi phí...");
        searchField.setPrefWidth(220);
        searchField.setStyle("-fx-background-color: white; -fx-padding: 10 12; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-font-size: 13px;");

        ComboBox<String> cbMonth = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cbMonth.getItems().add(String.format("%02d", i));
        }
        cbMonth.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
        cbMonth.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-font-size: 13px; -fx-pref-height: 38px; -fx-pref-width: 90px;");

        ComboBox<String> cbYear = new ComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            cbYear.getItems().add(String.valueOf(i));
        }
        cbYear.setValue(String.valueOf(currentYear));
        cbYear.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-font-size: 13px; -fx-pref-height: 38px; -fx-pref-width: 100px;");

        Button btnAdd = new Button("➕ Thêm Chi Phí");
        btnAdd.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 18;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        Button btnPdf = new Button("📄 Xuất Báo Cáo");
        btnPdf.setStyle(
            "-fx-background-color: #E8F5E9;" +
            "-fx-text-fill: #2E7D32;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 18;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(searchField, cbMonth, cbYear, btnAdd, spacer, btnPdf);

        // Table container
        VBox tableContainer = new VBox(0);
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 12;");
        
        Label tableTitle = new Label("Danh Sách Chi Phí");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 20 20 15 20;");

        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 20, 12, 20));
        tableHeader.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

        Label colStt = createLabel("STT", 60, Pos.CENTER, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colName = createLabel("Khoản Mục Chi Phí", 250, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colCategory = createLabel("Phân Loại", 140, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colAmount = createLabel("Số Tiền", 140, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colNotes = createLabel("Ghi Chú", 180, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colAction = createLabel("Thao Tác", 100, Pos.CENTER, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");

        tableHeader.getChildren().addAll(colStt, colName, colCategory, colAmount, colNotes, colAction);

        VBox tableRows = new VBox(0);
        ScrollPane scrollPane = new ScrollPane(tableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white; -fx-padding: 0 10 0 10;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Footer Summary
        HBox tableFooter = new HBox(0);
        tableFooter.setAlignment(Pos.CENTER_LEFT);
        tableFooter.setPadding(new Insets(15, 20, 15, 20));
        tableFooter.setStyle("-fx-background-color: #ECEFF1; -fx-background-radius: 0 0 12 12; -fx-border-color: #cfd8dc; -fx-border-width: 1 0 0 0;");

        Label lblSummary = createLabel("TỔNG CHI PHÍ THÁNG:", 450, Pos.CENTER_LEFT, "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        Label lblTotal = createLabel("0 đ", 200, Pos.CENTER_LEFT, "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        tableFooter.getChildren().addAll(lblSummary, lblTotal);

        tableContainer.getChildren().addAll(tableTitle, tableHeader, scrollPane, tableFooter);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        root.getChildren().addAll(topBar, tableContainer);
        contentArea.getChildren().add(root);

        // Refresh action
        Runnable refreshList = () -> {
            tableRows.getChildren().clear();
            FixedExpenseService service = new FixedExpenseService();
            String monthStr = cbYear.getValue() + "-" + cbMonth.getValue();
            List<FixedExpense> list = service.getAllExpensesByMonth(monthStr);
            String query = searchField.getText().toLowerCase().trim();

            double totalAmount = 0;
            int stt = 1;
            for (FixedExpense exp : list) {
                if (query.isEmpty() || exp.getExpenseName().toLowerCase().contains(query)) {
                    totalAmount += exp.getAmount();

                    HBox row = new HBox(0);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(12, 10, 12, 10));
                    row.setStyle("-fx-background-color: white; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

                    Label rStt = createLabel(String.valueOf(stt++), 60, Pos.CENTER, "-fx-text-fill: #6b7280; -fx-font-size: 13px;");
                    Label rName = createLabel(exp.getExpenseName(), 250, Pos.CENTER_LEFT, "-fx-font-weight: 500; -fx-text-fill: #212121; -fx-font-size: 13px;");
                    Label rCategory = createLabel(exp.getCategory() != null ? exp.getCategory() : "-", 140, Pos.CENTER_LEFT, "-fx-text-fill: #4b5563; -fx-font-size: 13px;");
                    Label rAmount = createLabel(String.format("%,.0f đ", exp.getAmount()), 140, Pos.CENTER_LEFT, "-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 13px;");
                    Label rNotes = createLabel(exp.getNotes() != null && !exp.getNotes().isEmpty() ? exp.getNotes() : "-", 180, Pos.CENTER_LEFT, "-fx-text-fill: #6b7280; -fx-font-size: 13px;");

                    HBox actions = new HBox(8);
                    actions.setPrefWidth(100);
                    actions.setMinWidth(100);
                    actions.setMaxWidth(100);
                    actions.setAlignment(Pos.CENTER);

                    Button btnEdit = new Button("✏");
                    btnEdit.setStyle("-fx-background-color: #E3F2FD; -fx-text-fill: #1976D2; -fx-font-size: 13px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand;");
                    btnEdit.setOnAction(e -> {
                        ExpenseForm form = new ExpenseForm(exp, () -> searchField.setText(searchField.getText())); // Triggers refresh
                        form.show();
                    });

                    Button btnDelete = new Button("🗑");
                    btnDelete.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; -fx-font-size: 13px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand;");
                    btnDelete.setOnAction(e -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa khoản chi phí \"" + exp.getExpenseName() + "\"?", ButtonType.YES, ButtonType.NO);
                        confirm.setHeaderText(null);
                        confirm.showAndWait().ifPresent(res -> {
                            if (res == ButtonType.YES) {
                                new FixedExpenseService().deleteExpense(exp.getId());
                                searchField.setText(searchField.getText()); // Triggers refresh
                            }
                        });
                    });

                    actions.getChildren().addAll(btnEdit, btnDelete);
                    row.getChildren().addAll(rStt, rName, rCategory, rAmount, rNotes, actions);
                    tableRows.getChildren().add(row);
                }
            }
            lblTotal.setText(String.format("%,.0f đ", totalAmount));
        };

        // Wire event handlers
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshList.run());
        cbMonth.setOnAction(e -> refreshList.run());
        cbYear.setOnAction(e -> refreshList.run());
        btnAdd.setOnAction(e -> {
            ExpenseForm form = new ExpenseForm(refreshList);
            form.show();
        });
        btnPdf.setOnAction(e -> {
            String monthStr = cbYear.getValue() + "-" + cbMonth.getValue();
            exportExpenseReportToPDF(btnPdf.getScene().getWindow(), monthStr);
        });

        // Initial load
        refreshList.run();
    }

    private static Label createLabel(String text, double width, Pos alignment, String style) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setMinWidth(width);
        label.setMaxWidth(width);
        label.setAlignment(alignment);
        label.setStyle(style);
        return label;
    }

    public static void exportExpenseReportToPDF(javafx.stage.Window window, String expenseMonth) {
        try {
            int year = Integer.parseInt(expenseMonth.substring(0, 4));
            int month = Integer.parseInt(expenseMonth.substring(5, 7));
            String displayMonth = "Tháng " + month + " Năm " + year;

            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Lưu Báo Cáo Chi Phí Cố Định PDF");
            fileChooser.setInitialFileName("BaoCaoChiPhi_" + expenseMonth + ".pdf");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            java.io.File file = fileChooser.showSaveDialog(window);
            if (file == null) return;

            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf, com.itextpdf.kernel.geom.PageSize.A4);
            document.setMargins(40, 40, 40, 40);

            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);

            // Company & Report Title
            com.itextpdf.layout.element.Paragraph compName = new com.itextpdf.layout.element.Paragraph("CÔNG TY TNHH TM DV PHỤ TÙNG Ô TÔ MINH TÂM")
                .setFont(boldFont).setFontSize(9).setMargin(0);
            com.itextpdf.layout.element.Paragraph compAddr = new com.itextpdf.layout.element.Paragraph("Ngã tư An Dương Vương và Trương Định, P. Trần Phú, TP. Quảng Ngãi, T. Quảng Ngãi.")
                .setFont(font).setFontSize(8).setMargin(0);
            com.itextpdf.layout.element.Paragraph compMst = new com.itextpdf.layout.element.Paragraph("MST: 4300899201")
                .setFont(font).setFontSize(8).setMargin(0);
            
            document.add(compName);
            document.add(compAddr);
            document.add(compMst);
            
            com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph("BẢNG PHÂN LOẠI CHI PHÍ CỐ ĐỊNH")
                .setFont(boldFont)
                .setFontSize(14)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginTop(15)
                .setMarginBottom(2);
            document.add(title);

            com.itextpdf.layout.element.Paragraph subtitle = new com.itextpdf.layout.element.Paragraph(displayMonth)
                .setFont(font)
                .setFontSize(11)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(15);
            document.add(subtitle);

            // Load data
            FixedExpenseService service = new FixedExpenseService();
            List<FixedExpense> list = service.getAllExpensesByMonth(expenseMonth);

            // Grid Table
            float[] columnWidths = {25f, 200f, 90f, 95f, 105f};
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(columnWidths);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            table.setFontSize(8);

            com.itextpdf.kernel.colors.Color headerBg = new com.itextpdf.kernel.colors.DeviceRgb(178, 235, 242); // Light Cyan from Payroll PDF

            // Helper to build header cells
            java.util.function.Function<String, com.itextpdf.layout.element.Cell> makeHeaderCell = (text) -> {
                return new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(text).setFont(boldFont))
                    .setBackgroundColor(headerBg)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                    .setPadding(6);
            };

            // Table Headers
            table.addHeaderCell(makeHeaderCell.apply("STT"));
            table.addHeaderCell(makeHeaderCell.apply("Khoản mục chi phí\nCHI PHÍ CỐ ĐỊNH"));
            table.addHeaderCell(makeHeaderCell.apply("Phân loại chi phí"));
            table.addHeaderCell(makeHeaderCell.apply("Số tiền"));
            table.addHeaderCell(makeHeaderCell.apply("Ghi chú"));

            // Row Numbering (1 to 5)
            for (int i = 1; i <= 5; i++) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(String.valueOf(i)).setFont(font))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setPadding(4));
            }

            int stt = 1;
            double totalSum = 0;
            for (FixedExpense exp : list) {
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER).setPadding(5));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(exp.getExpenseName()).setFont(font)).setPadding(5));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(exp.getCategory() != null ? exp.getCategory() : "-").setFont(font)).setPadding(5));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", exp.getAmount())).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT).setPadding(5));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(exp.getNotes() != null && !exp.getNotes().isEmpty() ? exp.getNotes() : "-").setFont(font)).setPadding(5));
                totalSum += exp.getAmount();
            }

            // Summary row in Table
            table.addCell(new com.itextpdf.layout.element.Cell(1, 3)
                .add(new com.itextpdf.layout.element.Paragraph("Tổng cộng").setFont(boldFont))
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                .setPadding(6));
            
            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", totalSum)).setFont(boldFont))
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                .setPadding(6));

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph("").setFont(font))
                .setPadding(6));

            document.add(table);

            // Total words
            String amountInWords = HRHelper.convertNumberToWords(totalSum);
            com.itextpdf.layout.element.Paragraph wordsPara = new com.itextpdf.layout.element.Paragraph("Số tiền bằng chữ: " + amountInWords)
                .setFont(font).setFontSize(9).setItalic().setMarginTop(10);
            document.add(wordsPara);

            // Signatures block
            float[] sigWidths = {170f, 170f, 175f};
            com.itextpdf.layout.element.Table sigTable = new com.itextpdf.layout.element.Table(sigWidths);
            sigTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            sigTable.setMarginTop(20);
            
            sigTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Người Lập Biểu\n(Ký, họ tên)").setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)).setBorder(null));
            sigTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Kế Toán Trưởng\n(Ký, họ tên)").setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)).setBorder(null));
            
            com.itextpdf.layout.element.Cell bossCell = new com.itextpdf.layout.element.Cell();
            bossCell.add(new com.itextpdf.layout.element.Paragraph("Giám Đốc\n(Ký, đóng dấu)").setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            bossCell.add(new com.itextpdf.layout.element.Paragraph("\n\n\n\nTÂM").setFont(boldFont).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            bossCell.add(new com.itextpdf.layout.element.Paragraph("PHẠM MINH TÂM").setFont(boldFont).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            bossCell.setBorder(null);
            sigTable.addCell(bossCell);

            document.add(sigTable);
            document.close();

            Alert alert = util.AlertHelper.createAlert(Alert.AlertType.INFORMATION, "Thành công", "Xuất báo cáo chi phí cố định PDF thành công!");
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = util.AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xuất file PDF: " + e.getMessage());
            alert.show();
        }
    }
}
