package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Employee;
import service.EmployeeService;

import java.time.LocalDate;

public class EmployeeForm {
    private Stage stage;
    private boolean isEdit;
    private Employee employee;
    private Runnable onSave;

    private TextField txtCode;
    private TextField txtName;
    private TextField txtPhone;
    private TextField txtAddress;
    private DatePicker dpDob;
    private ComboBox<String> cbGender;
    private DatePicker dpStartDate;
    private ComboBox<String> cbPosition;
    private TextField txtSalary;

    public EmployeeForm(Runnable onSave) {
        this.isEdit = false;
        this.onSave = onSave;
    }

    public EmployeeForm(Employee emp, Runnable onSave) {
        this.isEdit = emp != null;
        this.employee = emp;
        this.onSave = onSave;
    }

    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(isEdit ? "Sửa Nhân Viên" : "Thêm Nhân Viên Mới");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");

        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label(isEdit ? "✏ Sửa Nhân Viên" : "👥 Thêm Nhân Viên Mới");
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

        Label sectionTitle = new Label("Thông Tin Nhân Viên");
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

        Label lblCode = new Label("Mã nhân viên (Tự động)");
        lblCode.setStyle(labelStyle);
        txtCode = new TextField(isEdit ? employee.getEmployeeCode() : generateNextEmployeeCode());
        txtCode.setEditable(false);
        txtCode.setPrefWidth(300);
        txtCode.setMaxWidth(Double.MAX_VALUE);
        txtCode.setStyle(fieldStyle);

        Label lblName = new Label("Họ tên *");
        lblName.setStyle(labelStyle);
        txtName = new TextField(isEdit ? employee.getName() : "");
        txtName.setPromptText("Nhập họ tên...");
        txtName.setPrefWidth(300);
        txtName.setMaxWidth(Double.MAX_VALUE);
        txtName.setStyle(fieldStyle);

        Label lblPhone = new Label("Số điện thoại");
        lblPhone.setStyle(labelStyle);
        txtPhone = new TextField(isEdit ? employee.getPhone() : "");
        txtPhone.setPromptText("Nhập số điện thoại...");
        txtPhone.setPrefWidth(300);
        txtPhone.setMaxWidth(Double.MAX_VALUE);
        txtPhone.setStyle(fieldStyle);

        Label lblAddress = new Label("Địa chỉ");
        lblAddress.setStyle(labelStyle);
        txtAddress = new TextField(isEdit ? employee.getAddress() : "");
        txtAddress.setPromptText("Nhập địa chỉ...");
        txtAddress.setPrefWidth(300);
        txtAddress.setMaxWidth(Double.MAX_VALUE);
        txtAddress.setStyle(fieldStyle);

        Label lblDob = new Label("Ngày sinh");
        lblDob.setStyle(labelStyle);
        dpDob = new DatePicker();
        dpDob.setPrefWidth(300);
        dpDob.setMaxWidth(Double.MAX_VALUE);
        dpDob.setStyle("-fx-font-size: 14px; -fx-pref-height: 44px;");
        if (isEdit && employee.getDob() != null && !employee.getDob().isEmpty()) {
            try { dpDob.setValue(LocalDate.parse(employee.getDob())); } catch (Exception ex) {}
        }

        Label lblGender = new Label("Giới tính");
        lblGender.setStyle(labelStyle);
        cbGender = new ComboBox<>();
        cbGender.getItems().addAll("Nam", "Nữ", "Khác");
        cbGender.setValue(isEdit && employee.getGender() != null ? employee.getGender() : "Nam");
        cbGender.setStyle(comboStyle);
        cbGender.setMaxWidth(Double.MAX_VALUE);

        Label lblStartDate = new Label("Ngày vào làm");
        lblStartDate.setStyle(labelStyle);
        dpStartDate = new DatePicker();
        dpStartDate.setPrefWidth(300);
        dpStartDate.setMaxWidth(Double.MAX_VALUE);
        dpStartDate.setStyle("-fx-font-size: 14px; -fx-pref-height: 44px;");
        if (isEdit && employee.getStartDate() != null && !employee.getStartDate().isEmpty()) {
            try { dpStartDate.setValue(LocalDate.parse(employee.getStartDate())); } catch (Exception ex) {}
        } else {
            dpStartDate.setValue(LocalDate.now());
        }

        Label lblPosition = new Label("Chức vụ *");
        lblPosition.setStyle(labelStyle);
        cbPosition = new ComboBox<>();
        cbPosition.getItems().addAll("Kỹ thuật điện", "Kỹ thuật viên", "Học viên", "Quản lý", "Khác");
        cbPosition.setValue(isEdit && employee.getPosition() != null ? employee.getPosition() : "Kỹ thuật viên");
        cbPosition.setEditable(true);
        cbPosition.setStyle(comboStyle);
        cbPosition.setMaxWidth(Double.MAX_VALUE);

        Label lblSalary = new Label("Lương cơ bản *");
        lblSalary.setStyle(labelStyle);
        txtSalary = new TextField(isEdit ? String.format("%.0f", employee.getBasicSalary()) : "0");
        txtSalary.setPromptText("Nhập mức lương cơ bản...");
        txtSalary.setPrefWidth(300);
        txtSalary.setMaxWidth(Double.MAX_VALUE);
        txtSalary.setStyle(fieldStyle);

        grid.add(lblCode, 0, 0); grid.add(txtCode, 1, 0);
        grid.add(lblName, 0, 1); grid.add(txtName, 1, 1);
        grid.add(lblPhone, 0, 2); grid.add(txtPhone, 1, 2);
        grid.add(lblAddress, 0, 3); grid.add(txtAddress, 1, 3);
        grid.add(lblDob, 0, 4); grid.add(dpDob, 1, 4);
        grid.add(lblGender, 0, 5); grid.add(cbGender, 1, 5);
        grid.add(lblStartDate, 0, 6); grid.add(dpStartDate, 1, 6);
        grid.add(lblPosition, 0, 7); grid.add(cbPosition, 1, 7);
        grid.add(lblSalary, 0, 8); grid.add(txtSalary, 1, 8);

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
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            String position = cbPosition.getValue() != null ? cbPosition.getValue().trim() : "";
            double salary = parseDoubleSafe(txtSalary.getText());

            if (code.isEmpty() || name.isEmpty() || position.isEmpty()) {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ Họ Tên và Chức Vụ!");
                alert.show();
                return;
            }

            EmployeeService empService = new EmployeeService();
            Employee saveEmp = isEdit ? employee : new Employee();
            saveEmp.setEmployeeCode(code);
            saveEmp.setName(name);
            saveEmp.setPhone(txtPhone.getText().trim());
            saveEmp.setAddress(txtAddress.getText().trim());
            saveEmp.setDob(getDateStringFromDatePicker(dpDob));
            saveEmp.setGender(cbGender.getValue());
            saveEmp.setStartDate(getDateStringFromDatePicker(dpStartDate));
            saveEmp.setPosition(position);
            saveEmp.setBasicSalary(salary);

            boolean success;
            if (isEdit) {
                success = empService.updateEmployee(saveEmp);
            } else {
                success = empService.addEmployee(saveEmp);
            }

            if (success) {
                if (onSave != null) onSave.run();
                stage.close();
            } else {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Mã nhân viên đã tồn tại hoặc có lỗi xảy ra!");
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

    private String generateNextEmployeeCode() {
        EmployeeService empService = new EmployeeService();
        java.util.List<Employee> list = empService.getAllEmployees();
        int maxNum = 0;
        for (Employee emp : list) {
            String code = emp.getEmployeeCode();
            if (code != null && code.startsWith("NV")) {
                try {
                    int num = Integer.parseInt(code.substring(2));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (Exception e) {
                    // Ignore non-numeric suffix
                }
            }
        }
        return String.format("NV%03d", maxNum + 1);
    }

    private String getDateStringFromDatePicker(DatePicker dp) {
        if (dp.getValue() != null) {
            return dp.getValue().toString();
        }
        String text = dp.getEditor().getText();
        if (text != null && !text.trim().isEmpty()) {
            try {
                LocalDate date = dp.getConverter().fromString(text);
                if (date != null) return date.toString();
            } catch (Exception e) {
                String cleanText = text.trim();
                String[] formats = {"dd/MM/yyyy", "d/M/yyyy", "dd-MM-yyyy", "d-M-yyyy", "yyyy-MM-dd"};
                for (String fmt : formats) {
                    try {
                        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern(fmt);
                        LocalDate date = LocalDate.parse(cleanText, dtf);
                        return date.toString();
                    } catch (Exception ex) {}
                }
            }
        }
        return "";
    }
}
