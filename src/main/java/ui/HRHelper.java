package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import service.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class HRHelper {

    public static void showHRManagement(MainUI mainUI, StackPane contentArea) {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));

        Label title = new Label("👥 Quản Lý Nhân Sự & Tính Lương");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-font-family: 'Times New Roman';");

        // Custom Tab Bar using Filter Buttons
        HBox navBar = new HBox(12);
        navBar.setAlignment(Pos.CENTER_LEFT);

        Button btnTabEmployee = new Button("Hồ Sơ Nhân Viên");
        Button btnTabAttendance = new Button("Chấm Công Tháng");
        Button btnTabPayroll = new Button("Tính Lương & Phiếu Lương");

        navBar.getChildren().addAll(btnTabEmployee, btnTabAttendance, btnTabPayroll);

        StackPane tabContentArea = new StackPane();
        VBox.setVgrow(tabContentArea, Priority.ALWAYS);

        // Tab Switching logic
        btnTabEmployee.setOnAction(e -> {
            setTabActive(btnTabEmployee, btnTabAttendance, btnTabPayroll);
            tabContentArea.getChildren().setAll(createEmployeeTab(mainUI));
        });
        btnTabAttendance.setOnAction(e -> {
            setTabActive(btnTabAttendance, btnTabEmployee, btnTabPayroll);
            tabContentArea.getChildren().setAll(createAttendanceTab(mainUI));
        });
        btnTabPayroll.setOnAction(e -> {
            setTabActive(btnTabPayroll, btnTabEmployee, btnTabAttendance);
            tabContentArea.getChildren().setAll(createPayrollTab(mainUI));
        });

        // Initialize with Employee Tab
        setTabActive(btnTabEmployee, btnTabAttendance, btnTabPayroll);
        tabContentArea.getChildren().setAll(createEmployeeTab(mainUI));

        view.getChildren().addAll(title, navBar, tabContentArea);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    private static void setTabActive(Button activeBtn, Button... inactiveBtns) {
        activeBtn.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 8px 16px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;" +
            "-fx-font-family: 'Times New Roman';"
        );
        for (Button btn : inactiveBtns) {
            btn.setStyle(
                "-fx-background-color: #f5f5f5;" +
                "-fx-text-fill: #616161;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 8px 16px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;" +
                "-fx-font-family: 'Times New Roman';"
            );
        }
    }

    private static Label createLabel(String text, double width, Pos align, String style) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        lbl.setMinWidth(width);
        lbl.setMaxWidth(width);
        lbl.setPadding(new Insets(0, 5, 0, 5));
        lbl.setAlignment(align);
        
        String fontStyle = "-fx-font-family: 'Times New Roman';";
        if (style != null) {
            if (!style.contains("-fx-font-family")) {
                style = style + (style.endsWith(";") ? "" : ";") + fontStyle;
            }
            lbl.setStyle(style);
        } else {
            lbl.setStyle(fontStyle);
        }
        return lbl;
    }

    private static VBox createEmployeeTab(MainUI mainUI) {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: transparent;");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Tìm kiếm nhân viên...");
        searchField.setPrefWidth(200);
        searchField.setMinWidth(200);
        searchField.setMaxWidth(200);
        searchField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-padding: 10px 15px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Times New Roman';"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAdd = new Button("+ Thêm Nhân Viên Mới");
        btnAdd.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Times New Roman';"
        );
        btnAdd.setOnMouseEntered(e -> btnAdd.setOpacity(0.9));
        btnAdd.setOnMouseExited(e -> btnAdd.setOpacity(1.0));

        topBar.getChildren().addAll(searchField, spacer, btnAdd);

        VBox tableContainer = new VBox(0);
        tableContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 20;"
        );

        Label tableTitle = new Label("Danh Sách Nhân Viên");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0; -fx-font-family: 'Times New Roman';");

        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 0, 12, 0));
        tableHeader.setStyle(
            "-fx-background-color: #F9FAFB;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label colCode = createLabel("Mã NV", 80, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colName = createLabel("Họ Tên", 180, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colPhone = createLabel("Số Điện Thoại", 120, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colPosition = createLabel("Chức Vụ", 140, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colSalary = createLabel("Lương Cơ Bản", 130, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colStartDate = createLabel("Ngày Vào Làm", 120, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colAction = createLabel("Thao Tác", 100, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");

        tableHeader.getChildren().addAll(colCode, colName, colPhone, colPosition, colSalary, colStartDate, colAction);

        VBox tableRows = new VBox(0);
        ScrollPane scrollPane = new ScrollPane(tableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        tableContainer.getChildren().addAll(tableTitle, tableHeader, scrollPane);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        root.getChildren().addAll(topBar, tableContainer);

        Runnable refreshList = () -> {
            tableRows.getChildren().clear();
            EmployeeService empService = new EmployeeService();
            List<Employee> list = empService.getAllEmployees();
            String query = searchField.getText().toLowerCase().trim();
            for (Employee emp : list) {
                if (query.isEmpty() || emp.getName().toLowerCase().contains(query) || emp.getEmployeeCode().toLowerCase().contains(query)) {
                    tableRows.getChildren().add(createEmployeeRow(mainUI, emp, () -> {
                        searchField.setText(searchField.getText());
                    }));
                }
            }
        };

        searchField.textProperty().addListener((obs, old, val) -> refreshList.run());
        btnAdd.setOnAction(e -> {
            EmployeeForm form = new EmployeeForm(refreshList);
            form.show();
        });

        refreshList.run();
        return root;
    }

    private static HBox createEmployeeRow(MainUI mainUI, Employee emp, Runnable onRefresh) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 0, 12, 0));
        row.setStyle("-fx-background-color: white; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

        Label colCode = createLabel(emp.getEmployeeCode(), 80, Pos.CENTER_LEFT, "-fx-font-weight: bold; -fx-text-fill: #1976D2; -fx-font-size: 13px;");
        Label colName = createLabel(emp.getName(), 180, Pos.CENTER_LEFT, "-fx-font-weight: 500; -fx-text-fill: #212121; -fx-font-size: 13px;");
        Label colPhone = createLabel(emp.getPhone() != null ? emp.getPhone() : "-", 120, Pos.CENTER_LEFT, "-fx-text-fill: #4b5563; -fx-font-size: 13px;");
        Label colPosition = createLabel(emp.getPosition() != null ? emp.getPosition() : "-", 140, Pos.CENTER_LEFT, "-fx-text-fill: #4b5563; -fx-font-size: 13px;");
        Label colSalary = createLabel(String.format("%,.0f đ", emp.getBasicSalary()), 130, Pos.CENTER_LEFT, "-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label colStartDate = createLabel(emp.getStartDate() != null ? emp.getStartDate() : "-", 120, Pos.CENTER_LEFT, "-fx-text-fill: #6b7280; -fx-font-size: 13px;");

        HBox actions = new HBox(8);
        actions.setPrefWidth(100);
        actions.setMinWidth(100);
        actions.setMaxWidth(100);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(0, 5, 0, 5));

        Button btnEdit = new Button("✏");
        btnEdit.setStyle("-fx-background-color: #E3F2FD; -fx-text-fill: #1976D2; -fx-font-size: 13px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-family: 'Times New Roman';");
        btnEdit.setOnAction(e -> {
            EmployeeForm form = new EmployeeForm(emp, onRefresh);
            form.show();
        });

        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; -fx-font-size: 13px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-family: 'Times New Roman';");
        btnDelete.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa nhân viên " + emp.getName() + "?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    EmployeeService empService = new EmployeeService();
                    empService.deleteEmployee(emp.getId());
                    onRefresh.run();
                }
            });
        });

        actions.getChildren().addAll(btnEdit, btnDelete);

        row.getChildren().addAll(colCode, colName, colPhone, colPosition, colSalary, colStartDate, actions);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: white; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;"));

        return row;
    }

    private static VBox createAttendanceTab(MainUI mainUI) {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: transparent;");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> cbMonth = new ComboBox<>();
        for (int m = 1; m <= 12; m++) {
            cbMonth.getItems().add("Tháng " + m);
        }
        cbMonth.setValue("Tháng " + LocalDate.now().getMonthValue());
        cbMonth.setPrefWidth(150); cbMonth.setMinWidth(150); cbMonth.setMaxWidth(150);
        cbMonth.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");

        ComboBox<String> cbYear = new ComboBox<>();
        cbYear.getItems().addAll(mainUI.getAvailableYears());
        cbYear.setValue(String.valueOf(LocalDate.now().getYear()));
        cbYear.setPrefWidth(140); cbYear.setMinWidth(140); cbYear.setMaxWidth(140);
        cbYear.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");

        Button btnSave = new Button("💾 Lưu Bảng Công");
        btnSave.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Times New Roman';"
        );
        btnSave.setOnMouseEntered(e -> btnSave.setOpacity(0.9));
        btnSave.setOnMouseExited(e -> btnSave.setOpacity(1.0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(cbMonth, cbYear, spacer, btnSave);

        VBox tableContainer = new VBox(0);
        tableContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 20;"
        );

        Label tableTitle = new Label("Bảng Chấm Công Chi Tiết");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0; -fx-font-family: 'Times New Roman';");

        ScrollPane gridScroll = new ScrollPane();
        gridScroll.setFitToHeight(true);
        gridScroll.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");
        VBox.setVgrow(gridScroll, Priority.ALWAYS);

        tableContainer.getChildren().addAll(tableTitle, gridScroll);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        java.util.Map<Integer, java.util.Map<Integer, Button>> attendanceInputs = new java.util.HashMap<>();
        java.util.Map<Integer, Label> totalLabels = new java.util.HashMap<>();

        Runnable loadGrid = () -> {
            attendanceInputs.clear();
            totalLabels.clear();

            GridPane grid = new GridPane();
            grid.setStyle("-fx-background-color: white;");
            grid.setHgap(8);
            grid.setVgap(10);

            int month = parseIntSafe(cbMonth.getValue());
            int year = Integer.parseInt(cbYear.getValue());
            YearMonth ym = YearMonth.of(year, month);
            int totalDays = ym.lengthOfMonth();
            String monthStr = String.format("%04d-%02d", year, month);

            Label lblEmpHeader = new Label("Nhân Viên");
            lblEmpHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-pref-width: 150px; -fx-min-width: 150px; -fx-font-family: 'Times New Roman';");
            grid.add(lblEmpHeader, 0, 0);

            for (int d = 1; d <= totalDays; d++) {
                Label lblDay = new Label(String.valueOf(d));
                lblDay.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-pref-width: 32px; -fx-alignment: center; -fx-font-family: 'Times New Roman';");
                grid.add(lblDay, d, 0);
            }

            Label lblTotalHeader = new Label("Tổng Công");
            lblTotalHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-pref-width: 80px; -fx-alignment: center; -fx-font-family: 'Times New Roman';");
            grid.add(lblTotalHeader, totalDays + 1, 0);

            EmployeeService empService = new EmployeeService();
            AttendanceService attService = new AttendanceService();
            List<Employee> employees = empService.getAllEmployees();

            int rowIdx = 1;
            for (Employee emp : employees) {
                Label lblEmp = new Label(emp.getName() + " (" + emp.getEmployeeCode() + ")");
                lblEmp.setStyle("-fx-font-weight: 500; -fx-text-fill: #212121; -fx-pref-width: 150px; -fx-min-width: 150px; -fx-font-family: 'Times New Roman';");
                grid.add(lblEmp, 0, rowIdx);

                java.util.Map<Integer, Button> empButtons = new java.util.HashMap<>();
                attendanceInputs.put(emp.getId(), empButtons);

                List<Attendance> currentAtts = attService.getAttendanceByMonth(emp.getId(), monthStr);
                java.util.Map<Integer, String> dayToVal = new java.util.HashMap<>();
                for (Attendance a : currentAtts) {
                    try {
                        int day = Integer.parseInt(a.getWorkDate().substring(8, 10));
                        dayToVal.put(day, a.getAttendanceVal());
                    } catch (Exception ex) {}
                }

                Label lblTotal = new Label("0.0");
                lblTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32; -fx-pref-width: 80px; -fx-alignment: center; -fx-font-size: 14px; -fx-font-family: 'Times New Roman';");
                totalLabels.put(emp.getId(), lblTotal);

                Runnable updateTotal = () -> {
                    double tot = 0;
                    for (Button b : empButtons.values()) {
                        String txt = b.getText();
                        if ("1".equals(txt)) tot += 1.0;
                        else if ("0.5".equals(txt)) tot += 0.5;
                    }
                    lblTotal.setText(String.format("%.1f", tot));
                };

                for (int d = 1; d <= totalDays; d++) {
                    final int day = d;
                    String val = dayToVal.getOrDefault(day, "1");
                    Button btnDay = new Button(val);
                    btnDay.setStyle(getAttendanceButtonStyle(val));
                    btnDay.setPrefSize(32, 32);
                    btnDay.setCursor(javafx.scene.Cursor.HAND);

                    btnDay.setOnAction(e -> {
                        String cur = btnDay.getText();
                        String next = "1";
                        if ("1".equals(cur)) next = "0.5";
                        else if ("0.5".equals(cur)) next = "N";
                        
                        btnDay.setText(next);
                        btnDay.setStyle(getAttendanceButtonStyle(next));
                        updateTotal.run();
                    });

                    empButtons.put(day, btnDay);
                    grid.add(btnDay, d, rowIdx);
                }

                grid.add(lblTotal, totalDays + 1, rowIdx);
                updateTotal.run();
                rowIdx++;
            }

            gridScroll.setContent(grid);
        };

        cbMonth.valueProperty().addListener((obs, old, val) -> loadGrid.run());
        cbYear.valueProperty().addListener((obs, old, val) -> loadGrid.run());

        btnSave.setOnAction(e -> {
            int month = parseIntSafe(cbMonth.getValue());
            int year = Integer.parseInt(cbYear.getValue());
            String monthStr = String.format("%04d-%02d", year, month);
            AttendanceService attService = new AttendanceService();

            boolean ok = true;
            for (var entry : attendanceInputs.entrySet()) {
                int empId = entry.getKey();
                var empButtons = entry.getValue();
                for (var btnEntry : empButtons.entrySet()) {
                    int day = btnEntry.getKey();
                    Button btn = btnEntry.getValue();
                    String val = btn.getText();
                    String workDate = String.format("%s-%02d", monthStr, day);
                    
                    Attendance att = new Attendance(0, empId, monthStr, workDate, val);
                    ok = ok && attService.saveAttendance(att);
                }
            }

            if (ok) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã lưu bảng chấm công thành công!");
                alert.setHeaderText(null);
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu bảng chấm công!");
                alert.setHeaderText(null);
                alert.show();
            }
        });

        loadGrid.run();
        root.getChildren().addAll(topBar, tableContainer);
        return root;
    }

    private static String getAttendanceButtonStyle(String val) {
        String base = " -fx-font-weight: bold; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-family: 'Times New Roman';";
        if ("1".equals(val)) {
            return "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32; -fx-border-color: #A5D6A7;" + base;
        } else if ("0.5".equals(val)) {
            return "-fx-background-color: #FFFDE7; -fx-text-fill: #F57F17; -fx-border-color: #FFF59D;" + base;
        } else {
            return "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828; -fx-border-color: #EF9A9A;" + base;
        }
    }

    private static VBox createPayrollTab(MainUI mainUI) {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: transparent;");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> cbMonth = new ComboBox<>();
        for (int m = 1; m <= 12; m++) {
            cbMonth.getItems().add("Tháng " + m);
        }
        cbMonth.setValue("Tháng " + LocalDate.now().getMonthValue());
        cbMonth.setPrefWidth(150); cbMonth.setMinWidth(150); cbMonth.setMaxWidth(150);
        cbMonth.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");

        ComboBox<String> cbYear = new ComboBox<>();
        cbYear.getItems().addAll(mainUI.getAvailableYears());
        cbYear.setValue(String.valueOf(LocalDate.now().getYear()));
        cbYear.setPrefWidth(140); cbYear.setMinWidth(140); cbYear.setMaxWidth(140);
        cbYear.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-family: 'Times New Roman';");

        topBar.getChildren().addAll(cbMonth, cbYear);

        VBox tableContainer = new VBox(0);
        tableContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 20;"
        );

        Label tableTitle = new Label("Danh Sách Tính Lương");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0; -fx-font-family: 'Times New Roman';");

        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 0, 12, 0));
        tableHeader.setStyle(
            "-fx-background-color: #F9FAFB;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label colCode = createLabel("Mã NV", 80, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colName = createLabel("Họ Tên", 160, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colPos = createLabel("Chức Vụ", 120, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colBasic = createLabel("Lương Cơ Bản", 120, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colDays = createLabel("Số Ngày", 80, Pos.CENTER, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colWork = createLabel("Số Công", 80, Pos.CENTER, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colTemp = createLabel("Lương Ngày", 120, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colNet = createLabel("Thực Nhận", 120, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colAct = createLabel("Thao Tác", 260, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");

        tableHeader.getChildren().addAll(colCode, colName, colPos, colBasic, colDays, colWork, colTemp, colNet, colAct);

        VBox tableRows = new VBox(0);
        ScrollPane scrollPane = new ScrollPane(tableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        tableContainer.getChildren().addAll(tableTitle, tableHeader, scrollPane);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        Runnable refreshList = () -> {
            tableRows.getChildren().clear();
            EmployeeService empService = new EmployeeService();
            AttendanceService attService = new AttendanceService();
            PayrollService prService = new PayrollService();

            List<Employee> employees = empService.getAllEmployees();
            int month = parseIntSafe(cbMonth.getValue());
            int year = Integer.parseInt(cbYear.getValue());
            YearMonth ym = YearMonth.of(year, month);
            int totalDays = ym.lengthOfMonth();
            String monthStr = String.format("%04d-%02d", year, month);

            for (Employee emp : employees) {
                double workDays = attService.getActualWorkDays(emp.getId(), monthStr);
                Payroll savedPr = prService.getPayroll(emp.getId(), monthStr);

                HBox row = new HBox(0);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(12, 0, 12, 0));
                row.setStyle("-fx-background-color: white; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

                Label lblCode = createLabel(emp.getEmployeeCode(), 80, Pos.CENTER_LEFT, "-fx-font-weight: bold; -fx-text-fill: #1976D2; -fx-font-size: 13px;");
                Label lblName = createLabel(emp.getName(), 160, Pos.CENTER_LEFT, "-fx-font-weight: 500; -fx-text-fill: #212121; -fx-font-size: 13px;");
                Label lblPos = createLabel(emp.getPosition(), 120, Pos.CENTER_LEFT, "-fx-text-fill: #4b5563; -fx-font-size: 13px;");
                Label lblBasic = createLabel(String.format("%,.0f đ", emp.getBasicSalary()), 120, Pos.CENTER_LEFT, "-fx-text-fill: #212121; -fx-font-size: 13px;");
                Label lblDays = createLabel(String.valueOf(totalDays), 80, Pos.CENTER, "-fx-text-fill: #6b7280; -fx-font-size: 13px;");
                Label lblWork = createLabel(String.format("%.1f", workDays), 80, Pos.CENTER, "-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 13px;");

                double tempWage = (emp.getBasicSalary() / totalDays) * workDays;
                Label lblTemp = createLabel(String.format("%,.0f đ", tempWage), 120, Pos.CENTER_LEFT, "-fx-text-fill: #4b5563; -fx-font-size: 13px;");
                Label lblNet = createLabel(savedPr != null ? String.format("%,.0f đ", savedPr.getNetSalary()) : "-", 120, Pos.CENTER_LEFT, savedPr != null ? "-fx-text-fill: #1976D2; -fx-font-weight: bold; -fx-font-size: 13px;" : "-fx-text-fill: #9e9e9e; -fx-font-size: 13px;");

                HBox actions = new HBox(8);
                actions.setPrefWidth(260);
                actions.setMinWidth(260);
                actions.setMaxWidth(260);
                actions.setAlignment(Pos.CENTER_LEFT);
                actions.setPadding(new Insets(0, 5, 0, 5));

                Button btnCalc = new Button(savedPr != null ? "Sửa Lương" : "Tính Lương");
                btnCalc.setStyle(savedPr != null ? 
                    "-fx-background-color: #FFF3E0; -fx-text-fill: #E65100; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-family: 'Times New Roman';" :
                    "-fx-background-color: #E3F2FD; -fx-text-fill: #1976D2; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-family: 'Times New Roman';");
                
                btnCalc.setOnAction(e -> {
                    PayrollForm form = new PayrollForm(mainUI, emp, monthStr, () -> {
                        cbMonth.setValue(cbMonth.getValue());
                    });
                    form.show();
                });

                actions.getChildren().add(btnCalc);

                if (savedPr != null) {
                    Button btnPdf = new Button("📄 Phiếu Lương");
                    btnPdf.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-family: 'Times New Roman';");
                    btnPdf.setOnAction(e -> exportPaySlipToPDF(btnPdf.getScene().getWindow(), savedPr, emp));
                    actions.getChildren().add(btnPdf);
                }

                row.getChildren().addAll(lblCode, lblName, lblPos, lblBasic, lblDays, lblWork, lblTemp, lblNet, actions);
                
                row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;"));
                row.setOnMouseExited(e -> row.setStyle("-fx-background-color: white; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;"));

                tableRows.getChildren().add(row);
            }
        };

        cbMonth.valueProperty().addListener((obs, old, val) -> refreshList.run());
        cbYear.valueProperty().addListener((obs, old, val) -> refreshList.run());

        refreshList.run();
        root.getChildren().addAll(topBar, tableContainer);
        return root;
    }

    public static void exportPaySlipToPDF(javafx.stage.Window window, Payroll pr, Employee emp) {
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Lưu Phiếu Lương PDF");
            fileChooser.setInitialFileName("PhieuLuong_" + emp.getEmployeeCode() + "_" + pr.getPayMonth() + ".pdf");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            java.io.File file = fileChooser.showSaveDialog(window);
            if (file == null) return;
            
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf, com.itextpdf.kernel.geom.PageSize.A4);
            document.setMargins(40, 40, 40, 40);
            
            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);
            
            com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph("PHIẾU THANH TOÁN LƯƠNG")
                .setFont(boldFont).setFontSize(20).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(10);
            
            com.itextpdf.layout.element.Paragraph subtitle = new com.itextpdf.layout.element.Paragraph("Tháng: " + pr.getPayMonth())
                .setFont(font).setFontSize(14).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(30);
            
            document.add(title);
            document.add(subtitle);
            
            float[] infoWidths = {280f, 240f};
            com.itextpdf.layout.element.Table infoTable = new com.itextpdf.layout.element.Table(infoWidths);
            infoTable.setMarginBottom(20);
            
            infoTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Mã nhân viên: " + emp.getEmployeeCode()).setFont(font)).setBorder(null));
            infoTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Ngày lập phiếu: " + LocalDate.now().toString()).setFont(font)).setBorder(null));
            
            infoTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Họ tên: " + emp.getName()).setFont(boldFont)).setBorder(null));
            infoTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Số ngày trong tháng: " + pr.getTotalDays()).setFont(font)).setBorder(null));
            
            infoTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Chức vụ: " + emp.getPosition()).setFont(font)).setBorder(null));
            infoTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Số công làm thực tế: " + pr.getActualWorkDays()).setFont(font)).setBorder(null));
            
            document.add(infoTable);
            
            float[] detailWidths = {30f, 340f, 150f};
            com.itextpdf.layout.element.Table detailTable = new com.itextpdf.layout.element.Table(detailWidths);
            detailTable.setMarginBottom(30);
            
            detailTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("STT").setFont(boldFont)).setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));
            detailTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Khoản mục").setFont(boldFont)).setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));
            detailTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Số tiền (VNĐ)").setFont(boldFont).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)).setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));
            
            int stt = 1;
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Lương cơ bản").setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", pr.getBasicSalary())).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            
            double calculatedWage = (pr.getBasicSalary() / pr.getTotalDays()) * pr.getActualWorkDays();
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Lương theo ngày công thực tế").setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", calculatedWage)).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Phụ cấp trách nhiệm").setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", pr.getAllowanceResponsibility())).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Phụ cấp khác").setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", pr.getAllowanceOther())).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Hoa hồng tư vấn").setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", pr.getCommissionConsulting())).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Hoa hồng dịch vụ").setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", pr.getCommissionService())).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Tiền tăng ca").setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", pr.getOvertimePay())).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Khấu trừ Bảo hiểm xã hội (-)").setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("-%,.0f", pr.getSocialInsurance())).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(stt++)).setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Khấu trừ Tiền tạm ứng (-)").setFont(font)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("-%,.0f", pr.getAdvancePayment())).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            
            detailTable.addCell(new com.itextpdf.layout.element.Cell(1, 2).add(new com.itextpdf.layout.element.Paragraph("TỔNG LƯƠNG THỰC NHẬN").setFont(boldFont)));
            detailTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f đ", pr.getNetSalary())).setFont(boldFont).setFontSize(13).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
            
            document.add(detailTable);
            
            float[] sigWidths = {260f, 260f};
            com.itextpdf.layout.element.Table sigTable = new com.itextpdf.layout.element.Table(sigWidths);
            sigTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Người Nhận Lương\n(Ký và ghi rõ họ tên)").setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)).setBorder(null));
            sigTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Người Lập Phiếu\n(Ký và đóng dấu)").setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)).setBorder(null));
            
            document.add(sigTable);
            document.close();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã xuất phiếu lương thành công!");
            alert.setHeaderText(null);
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi xuất PDF: " + e.getMessage());
            alert.setHeaderText(null);
            alert.show();
        }
    }

    private static double parseDoubleSafe(String str) {
        if (str == null || str.trim().isEmpty()) return 0;
        try {
            return Double.parseDouble(str.replaceAll("[^0-9.-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private static int parseIntSafe(String str) {
        if (str == null || str.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(str.replaceAll("[^0-9-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
