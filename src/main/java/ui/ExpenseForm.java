package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.FixedExpense;
import service.FixedExpenseService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class ExpenseForm {
    private Stage stage;
    private boolean isEdit;
    private FixedExpense expense;
    private int defaultYear;
    private int defaultMonth;
    private Runnable onSave;
    
    private TextField txtName;
    private ComboBox<String> cbCategory;
    private TextField txtAmount;
    private DatePicker dpDate;
    private TextField txtNotes;

    public ExpenseForm(Runnable onSave) {
        this(null, LocalDate.now().getYear(), LocalDate.now().getMonthValue(), onSave);
    }

    public ExpenseForm(int defaultYear, int defaultMonth, Runnable onSave) {
        this(null, defaultYear, defaultMonth, onSave);
    }

    public ExpenseForm(FixedExpense exp, Runnable onSave) {
        this(exp, LocalDate.now().getYear(), LocalDate.now().getMonthValue(), onSave);
    }

    public ExpenseForm(FixedExpense exp, int defaultYear, int defaultMonth, Runnable onSave) {
        this.isEdit = exp != null;
        this.expense = exp;
        this.defaultYear = defaultYear;
        this.defaultMonth = defaultMonth;
        this.onSave = onSave;
    }

    private Runnable closeHandler;

    public void close() {
        if (closeHandler != null) {
            closeHandler.run();
        } else if (stage != null) {
            stage.close();
        }
    }

    public BorderPane createFormLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");

        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label(isEdit ? "✏ Sửa Khoản Chi Phí" : "💰 Thêm Chi Phí Mới");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #212121;");

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
        return root;
    }

    public void showInOverlay(StackPane container) {
        BorderPane root = createFormLayout();

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");

        root.setMaxWidth(650);
        root.setMaxHeight(600);
        root.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.35), 20, 0, 0, 0);"
        );

        overlay.getChildren().add(root);
        this.closeHandler = () -> container.getChildren().remove(overlay);
        container.getChildren().add(overlay);
    }

    public void show() {
        stage = new Stage();
        if (MainUI.getMainStage() != null) {
            stage.initOwner(MainUI.getMainStage());
        }
        
        stage.setTitle(isEdit ? "Sửa Khoản Chi Phí" : "Thêm Chi Phí Mới");

        BorderPane root = createFormLayout();

        Scene scene = new Scene(root, 650, 580);
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

        Label sectionTitle = new Label("Thông Tin Khoản Chi Phí");
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

        Label lblName = new Label("Tên khoản mục *");
        lblName.setStyle(labelStyle);
        txtName = new TextField(isEdit ? expense.getExpenseName() : "");
        txtName.setPromptText("Nhập tên khoản mục chi phí (ví dụ: Tiền wifi, Tiền thuê đất...)");
        txtName.setPrefWidth(300);
        txtName.setMaxWidth(Double.MAX_VALUE);
        txtName.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtName);

        Label lblCategory = new Label("Phân loại chi phí *");
        lblCategory.setStyle(labelStyle);
        cbCategory = new ComboBox<>();
        cbCategory.getItems().addAll("cố định", "biến thiên");
        cbCategory.setValue(isEdit && expense.getCategory() != null ? expense.getCategory() : "cố định");
        cbCategory.setEditable(false);
        cbCategory.setStyle(comboStyle);
        cbCategory.setMaxWidth(Double.MAX_VALUE);

        Label lblAmount = new Label("Số tiền (VNĐ) *");
        lblAmount.setStyle(labelStyle);
        txtAmount = new TextField(isEdit ? String.format("%.0f", expense.getAmount()) : "");
        txtAmount.setPromptText("Nhập số tiền...");
        txtAmount.setPrefWidth(300);
        txtAmount.setMaxWidth(Double.MAX_VALUE);
        txtAmount.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtAmount);

        Label lblDate = new Label("Ngày chi *");
        lblDate.setStyle(labelStyle);

        dpDate = new DatePicker();
        dpDate.setPromptText("dd/MM/yyyy");
        dpDate.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-pref-height: 44px; -fx-pref-width: 300px;");
        dpDate.setMaxWidth(Double.MAX_VALUE);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dpDate.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.trim().isEmpty()) {
                    String clean = string.trim();
                    for (String pattern : new String[]{"dd/MM/yyyy", "d/M/yyyy", "dd-MM-yyyy", "d-M-yyyy", "yyyy-MM-dd"}) {
                        try {
                            return LocalDate.parse(clean, DateTimeFormatter.ofPattern(pattern));
                        } catch (Exception ignored) {}
                    }
                }
                return null;
            }
        });

        dpDate.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                try {
                    String text = dpDate.getEditor().getText();
                    if (text != null && !text.trim().isEmpty()) {
                        dpDate.setValue(dpDate.getConverter().fromString(text));
                    }
                } catch (Exception ignored) {}
            }
        });

        LocalDate initialDate = LocalDate.now();
        if (isEdit && expense != null) {
            if (expense.getCreatedAt() != null && expense.getCreatedAt().trim().length() >= 10) {
                try {
                    initialDate = LocalDate.parse(expense.getCreatedAt().trim().substring(0, 10));
                } catch (Exception ignored) {}
            } else if (expense.getExpenseMonth() != null && expense.getExpenseMonth().trim().length() >= 7) {
                try {
                    initialDate = LocalDate.parse(expense.getExpenseMonth().trim() + "-01");
                } catch (Exception ignored) {}
            }
        } else {
            LocalDate now = LocalDate.now();
            if (defaultYear == now.getYear() && defaultMonth == now.getMonthValue()) {
                initialDate = now;
            } else {
                try {
                    int maxDays = YearMonth.of(defaultYear, defaultMonth).lengthOfMonth();
                    initialDate = LocalDate.of(defaultYear, defaultMonth, Math.min(now.getDayOfMonth(), maxDays));
                } catch (Exception e) {
                    initialDate = now;
                }
            }
        }
        dpDate.setValue(initialDate);

        Label lblNotes = new Label("Ghi chú");
        lblNotes.setStyle(labelStyle);
        txtNotes = new TextField(isEdit ? expense.getNotes() : "");
        txtNotes.setPromptText("Nhập ghi chú (nếu có)...");
        txtNotes.setPrefWidth(300);
        txtNotes.setMaxWidth(Double.MAX_VALUE);
        txtNotes.setStyle(fieldStyle);
        UIUtils.setupIMEFix(txtNotes);

        grid.add(lblName, 0, 0); grid.add(txtName, 1, 0);
        grid.add(lblCategory, 0, 1); grid.add(cbCategory, 1, 1);
        grid.add(lblAmount, 0, 2); grid.add(txtAmount, 1, 2);
        grid.add(lblDate, 0, 3); grid.add(dpDate, 1, 3);
        grid.add(lblNotes, 0, 4); grid.add(txtNotes, 1, 4);

        section.getChildren().addAll(sectionTitle, grid);
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
        btnCancel.setOnAction(e -> close());

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
        btnSave.setOnMouseEntered(e -> btnSave.setOpacity(0.9));
        btnSave.setOnMouseExited(e -> btnSave.setOpacity(1.0));
        
        btnSave.setOnAction(e -> {
            String name = txtName.getText().trim();
            String cat = cbCategory.getValue() != null ? cbCategory.getValue().trim() : "cố định";
            double amount = parseDoubleSafe(txtAmount.getText());
            String notes = txtNotes.getText().trim();

            LocalDate date = dpDate.getValue();
            if (date == null) {
                String text = dpDate.getEditor().getText();
                if (text != null && !text.trim().isEmpty()) {
                    date = dpDate.getConverter().fromString(text);
                }
            }

            if (name.isEmpty() || cat.isEmpty() || amount <= 0 || date == null) {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ Tên khoản mục, Phân loại, Số tiền và Ngày chi hợp lệ!");
                alert.show();
                return;
            }

            String monthStr = String.format("%04d-%02d", date.getYear(), date.getMonthValue());
            String timeStr = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            String createdAtStr = date.toString() + " " + timeStr;
            if (isEdit && expense != null && expense.getCreatedAt() != null && expense.getCreatedAt().trim().length() >= 19) {
                createdAtStr = date.toString() + " " + expense.getCreatedAt().trim().substring(11, 19);
            }

            FixedExpenseService service = new FixedExpenseService();
            FixedExpense saveExp = isEdit ? expense : new FixedExpense();
            saveExp.setExpenseName(name);
            saveExp.setCategory(cat);
            saveExp.setAmount(amount);
            saveExp.setExpenseMonth(monthStr);
            saveExp.setCreatedAt(createdAtStr);
            saveExp.setNotes(notes);

            boolean success;
            if (isEdit) {
                success = service.updateExpense(saveExp);
            } else {
                success = service.addExpense(saveExp);
            }

            if (success) {
                close();
                if (onSave != null) javafx.application.Platform.runLater(onSave);
            } else {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi xảy ra khi lưu chi phí!");
                alert.show();
            }
        });

        buttons.getChildren().addAll(btnCancel, btnSave);
        return buttons;
    }

    private double parseDoubleSafe(String str) {
        if (str == null || str.trim().isEmpty()) return 0;
        try {
            return Double.parseDouble(str.replaceAll("[^0-9.-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
