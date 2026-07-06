package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.FixedExpense;
import service.FixedExpenseService;

import java.time.LocalDate;

public class ExpenseForm {
    private Stage stage;
    private boolean isEdit;
    private FixedExpense expense;
    private Runnable onSave;
    
    private TextField txtName;
    private ComboBox<String> cbCategory;
    private TextField txtAmount;
    private ComboBox<String> cbMonth;
    private ComboBox<String> cbYear;
    private TextField txtNotes;

    public ExpenseForm(Runnable onSave) {
        this.isEdit = false;
        this.onSave = onSave;
    }

    public ExpenseForm(FixedExpense exp, Runnable onSave) {
        this.isEdit = exp != null;
        this.expense = exp;
        this.onSave = onSave;
    }

    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(isEdit ? "Sửa Chi Phí Cố Định" : "Thêm Chi Phí Cố Định Mới");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");

        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label(isEdit ? "✏ Sửa Chi Phí Cố Định" : "💰 Thêm Chi Phí Cố Định Mới");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #212121;");

        VBox formSection = createFormSection();
        HBox actionButtons = createActionButtons();

        mainContent.getChildren().addAll(title, formSection, actionButtons);
        scrollPane.setContent(mainContent);

        Scene scene = new Scene(scrollPane, 650, 580);
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

        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;";
        String fieldStyle = "-fx-background-color: #f5f5f5; -fx-padding: 12px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px;";
        String comboStyle = "-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-pref-height: 44px; -fx-pref-width: 300px;";

        Label lblName = new Label("Tên khoản mục *");
        lblName.setStyle(labelStyle);
        txtName = new TextField(isEdit ? expense.getExpenseName() : "");
        txtName.setPromptText("Nhập tên khoản mục chi phí (ví dụ: Tiền wifi, Tiền thuê đất...)");
        txtName.setPrefWidth(300);
        txtName.setStyle(fieldStyle);

        Label lblCategory = new Label("Phân loại chi phí *");
        lblCategory.setStyle(labelStyle);
        cbCategory = new ComboBox<>();
        cbCategory.getItems().addAll("cố định");
        cbCategory.setValue(isEdit && expense.getCategory() != null ? expense.getCategory() : "cố định");
        cbCategory.setEditable(false);
        cbCategory.setStyle(comboStyle);

        Label lblAmount = new Label("Số tiền (VNĐ) *");
        lblAmount.setStyle(labelStyle);
        txtAmount = new TextField(isEdit ? String.format("%.0f", expense.getAmount()) : "");
        txtAmount.setPromptText("Nhập số tiền...");
        txtAmount.setPrefWidth(300);
        txtAmount.setStyle(fieldStyle);

        Label lblPeriod = new Label("Tháng/Năm *");
        lblPeriod.setStyle(labelStyle);

        cbMonth = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cbMonth.getItems().add(String.format("%02d", i));
        }

        cbYear = new ComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            cbYear.getItems().add(String.valueOf(i));
        }

        if (isEdit && expense.getExpenseMonth() != null && expense.getExpenseMonth().length() == 7) {
            cbMonth.setValue(expense.getExpenseMonth().substring(5, 7));
            cbYear.setValue(expense.getExpenseMonth().substring(0, 4));
        } else {
            cbMonth.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
            cbYear.setValue(String.valueOf(currentYear));
        }

        cbMonth.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-pref-height: 44px; -fx-pref-width: 140px;");
        cbYear.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px; -fx-pref-height: 44px; -fx-pref-width: 140px;");

        HBox periodBox = new HBox(20);
        periodBox.getChildren().addAll(cbMonth, cbYear);

        Label lblNotes = new Label("Ghi chú");
        lblNotes.setStyle(labelStyle);
        txtNotes = new TextField(isEdit ? expense.getNotes() : "");
        txtNotes.setPromptText("Nhập ghi chú (nếu có)...");
        txtNotes.setPrefWidth(300);
        txtNotes.setStyle(fieldStyle);

        grid.add(lblName, 0, 0); grid.add(txtName, 1, 0);
        grid.add(lblCategory, 0, 1); grid.add(cbCategory, 1, 1);
        grid.add(lblAmount, 0, 2); grid.add(txtAmount, 1, 2);
        grid.add(lblPeriod, 0, 3); grid.add(periodBox, 1, 3);
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
        btnCancel.setOnAction(e -> stage.close());

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
            String m = cbMonth.getValue();
            String y = cbYear.getValue();
            String notes = txtNotes.getText().trim();

            if (name.isEmpty() || cat.isEmpty() || amount <= 0 || m == null || y == null) {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ Tên khoản mục, Phân loại và Số tiền hợp lệ!");
                alert.show();
                return;
            }

            String monthStr = y + "-" + m;
            FixedExpenseService service = new FixedExpenseService();
            FixedExpense saveExp = isEdit ? expense : new FixedExpense();
            saveExp.setExpenseName(name);
            saveExp.setCategory(cat);
            saveExp.setAmount(amount);
            saveExp.setExpenseMonth(monthStr);
            saveExp.setNotes(notes);

            boolean success;
            if (isEdit) {
                success = service.updateExpense(saveExp);
            } else {
                success = service.addExpense(saveExp);
            }

            if (success) {
                if (onSave != null) onSave.run();
                stage.close();
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
