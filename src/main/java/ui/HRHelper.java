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
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #212121;");

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
            "-fx-border-color: transparent;"
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
                "-fx-border-color: transparent;"
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
        if (style != null) {
            lbl.setStyle(style);
        }
        return lbl;
    }

    private static VBox createEmployeeTab(MainUI mainUI) {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: transparent;");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 20, 0, 0));

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
            "-fx-prompt-text-fill: #757575;" +
            "-fx-text-fill: #212121;"
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
            "-fx-cursor: hand;"
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
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0;");

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
        Label colAction = createLabel("Thao Tác", 100, Pos.CENTER, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");

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
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(0, 5, 0, 5));

        Button btnEdit = new Button("✏");
        btnEdit.setStyle("-fx-background-color: #E3F2FD; -fx-text-fill: #1976D2; -fx-font-size: 13px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> {
            EmployeeForm form = new EmployeeForm(emp, onRefresh);
            form.show();
        });

        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; -fx-font-size: 13px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand;");
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
        topBar.setPadding(new Insets(0, 20, 0, 0));

        ComboBox<String> cbMonth = new ComboBox<>();
        for (int m = 1; m <= 12; m++) {
            cbMonth.getItems().add("Tháng " + m);
        }
        cbMonth.setValue("Tháng " + LocalDate.now().getMonthValue());
        cbMonth.setPrefWidth(150); cbMonth.setMinWidth(150); cbMonth.setMaxWidth(150);
        cbMonth.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px;");

        ComboBox<String> cbYear = new ComboBox<>();
        cbYear.getItems().addAll(mainUI.getAvailableYears());
        cbYear.setValue(String.valueOf(LocalDate.now().getYear()));
        cbYear.setPrefWidth(140); cbYear.setMinWidth(140); cbYear.setMaxWidth(140);
        cbYear.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px;");

        Button btnSave = new Button("💾 Lưu Bảng Công");
        btnSave.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
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
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0;");

        ScrollPane gridScroll = new ScrollPane();
        gridScroll.setFitToHeight(true);
        gridScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        gridScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
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
            lblEmpHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-pref-width: 150px; -fx-min-width: 150px;");
            grid.add(lblEmpHeader, 0, 0);

            for (int d = 1; d <= totalDays; d++) {
                Label lblDay = new Label(String.valueOf(d));
                lblDay.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-pref-width: 32px; -fx-alignment: center;");
                grid.add(lblDay, d, 0);
            }

            Label lblTotalHeader = new Label("Tổng Công");
            lblTotalHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-pref-width: 80px; -fx-alignment: center;");
            grid.add(lblTotalHeader, totalDays + 1, 0);

            EmployeeService empService = new EmployeeService();
            AttendanceService attService = new AttendanceService();
            List<Employee> employees = empService.getAllEmployees();

            int rowIdx = 1;
            for (Employee emp : employees) {
                Label lblEmp = new Label(emp.getName() + " (" + emp.getEmployeeCode() + ")");
                lblEmp.setStyle("-fx-font-weight: 500; -fx-text-fill: #212121; -fx-pref-width: 150px; -fx-min-width: 150px;");
                grid.add(lblEmp, 0, rowIdx);

                java.util.Map<Integer, Button> empButtons = new java.util.HashMap<>();
                attendanceInputs.put(emp.getId(), empButtons);

                Attendance currentAtt = attService.getAttendanceByMonth(emp.getId(), monthStr);
                java.util.Map<Integer, String> dayToVal = new java.util.HashMap<>();
                if (currentAtt != null && currentAtt.getAttendanceData() != null) {
                    String[] days = currentAtt.getAttendanceData().split(",");
                    for (int i = 0; i < days.length; i++) {
                        dayToVal.put(i + 1, days[i]);
                    }
                }

                Label lblTotal = new Label("0.0");
                lblTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32; -fx-pref-width: 80px; -fx-alignment: center; -fx-font-size: 14px;");
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
                    String dbVal = dayToVal.getOrDefault(day, "1");
                    String val = "1";
                    if ("0.5".equals(dbVal) || "1/2".equals(dbVal)) val = "0.5";
                    else if ("N".equals(dbVal)) val = "N";

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
            EmployeeService empService = new EmployeeService();
            java.util.List<Employee> employees = empService.getAllEmployees();
            java.util.Map<Integer, String> empNames = new java.util.HashMap<>();
            for (Employee emp : employees) {
                empNames.put(emp.getId(), emp.getName());
            }

            boolean ok = true;
            for (var entry : attendanceInputs.entrySet()) {
                int empId = entry.getKey();
                var empButtons = entry.getValue();
                String empName = empNames.getOrDefault(empId, "Nhân viên " + empId);
                
                java.util.List<String> vals = new java.util.ArrayList<>();
                int numDays = empButtons.size();
                for (int d = 1; d <= numDays; d++) {
                    vals.add(empButtons.get(d).getText());
                }
                String csvData = String.join(",", vals);
                
                Attendance att = new Attendance(0, empId, empName, monthStr, csvData);
                ok = ok && attService.saveAttendance(att);
            }

            if (ok) {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu bảng chấm công thành công!");
                alert.show();
            } else {
                Alert alert = util.AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi lưu bảng chấm công!");
                alert.show();
            }
        });

        loadGrid.run();
        root.getChildren().addAll(topBar, tableContainer);
        return root;
    }

    private static String getAttendanceButtonStyle(String val) {
        String base = " -fx-font-weight: bold; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 10px; -fx-padding: 0;";
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
        topBar.setPadding(new Insets(0, 20, 0, 0));

        ComboBox<String> cbMonth = new ComboBox<>();
        for (int m = 1; m <= 12; m++) {
            cbMonth.getItems().add("Tháng " + m);
        }
        cbMonth.setValue("Tháng " + LocalDate.now().getMonthValue());
        cbMonth.setPrefWidth(150); cbMonth.setMinWidth(150); cbMonth.setMaxWidth(150);
        cbMonth.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px;");

        ComboBox<String> cbYear = new ComboBox<>();
        cbYear.getItems().addAll(mainUI.getAvailableYears());
        cbYear.setValue(String.valueOf(LocalDate.now().getYear()));
        cbYear.setPrefWidth(140); cbYear.setMinWidth(140); cbYear.setMaxWidth(140);
        cbYear.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-font-size: 13px;");

        Button btnExportAll = new Button("📊 Xuất Bảng Lương");
        btnExportAll.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnExportAll.setOnMouseEntered(e -> btnExportAll.setOpacity(0.9));
        btnExportAll.setOnMouseExited(e -> btnExportAll.setOpacity(1.0));

        btnExportAll.setOnAction(e -> {
            int month = parseIntSafe(cbMonth.getValue());
            int year = Integer.parseInt(cbYear.getValue());
            String monthStr = String.format("%04d-%02d", year, month);
            exportAllPayrollToPDF(btnExportAll.getScene().getWindow(), monthStr);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(cbMonth, cbYear, spacer, btnExportAll);

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
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #212121; -fx-padding: 0 0 15 0;");

        HBox tableHeader = new HBox(0);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(12, 0, 12, 0));
        tableHeader.setStyle(
            "-fx-background-color: #F9FAFB;" +
            "-fx-border-color: #f3f4f6;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label colCode = createLabel("Mã NV", 70, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colName = createLabel("Họ Tên", 140, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colPos = createLabel("Chức Vụ", 100, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colBasic = createLabel("Lương Cơ Bản", 110, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colDays = createLabel("Số Ngày", 60, Pos.CENTER, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colWork = createLabel("Số Công", 60, Pos.CENTER, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colTemp = createLabel("Lương Ngày", 110, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colNet = createLabel("Thực Nhận", 110, Pos.CENTER_LEFT, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label colAct = createLabel("Thao Tác", 220, Pos.CENTER, "-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #374151;");

        tableHeader.getChildren().addAll(colCode, colName, colPos, colBasic, colDays, colWork, colTemp, colNet, colAct);

        VBox tableRows = new VBox(0);
        ScrollPane scrollPane = new ScrollPane(tableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        tableContainer.getChildren().addAll(tableTitle, tableHeader, scrollPane);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        final Runnable[] refreshListWrapper = new Runnable[1];
        refreshListWrapper[0] = () -> {
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
                
                double basicSalary = emp.getBasicSalary();
                double tempWage = (basicSalary / totalDays) * workDays;
                
                Payroll displayPr;
                if (savedPr != null) {
                    displayPr = savedPr;
                } else {
                    double resp = emp.getAllowanceResponsibility();
                    double oth = emp.getAllowanceOther();
                    double ins = emp.getSocialInsurance();
                    double netSalary = tempWage + resp + oth - ins;
                    
                    displayPr = new Payroll(0, emp.getId(), emp.getName(), monthStr, totalDays, workDays, basicSalary,
                        resp, oth, 0, 0, 0, ins, 0, netSalary, "");
                }

                HBox row = new HBox(0);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(12, 0, 12, 0));
                row.setStyle("-fx-background-color: white; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

                Label lblCode = createLabel(emp.getEmployeeCode(), 70, Pos.CENTER_LEFT, "-fx-font-weight: bold; -fx-text-fill: #1976D2; -fx-font-size: 13px;");
                Label lblName = createLabel(emp.getName(), 140, Pos.CENTER_LEFT, "-fx-font-weight: 500; -fx-text-fill: #212121; -fx-font-size: 13px;");
                Label lblPos = createLabel(emp.getPosition(), 100, Pos.CENTER_LEFT, "-fx-text-fill: #4b5563; -fx-font-size: 13px;");
                Label lblBasic = createLabel(String.format("%,.0f đ", emp.getBasicSalary()), 110, Pos.CENTER_LEFT, "-fx-text-fill: #212121; -fx-font-size: 13px;");
                Label lblDays = createLabel(String.valueOf(totalDays), 60, Pos.CENTER, "-fx-text-fill: #6b7280; -fx-font-size: 13px;");
                Label lblWork = createLabel(String.format("%.1f", workDays), 60, Pos.CENTER, "-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 13px;");

                Label lblTemp = createLabel(String.format("%,.0f đ", tempWage), 110, Pos.CENTER_LEFT, "-fx-text-fill: #4b5563; -fx-font-size: 13px;");
                Label lblNet = createLabel(savedPr != null ? String.format("%,.0f đ", savedPr.getNetSalary()) : "-", 110, Pos.CENTER_LEFT, savedPr != null ? "-fx-text-fill: #1976D2; -fx-font-weight: bold; -fx-font-size: 13px;" : "-fx-text-fill: #9e9e9e; -fx-font-size: 13px;");

                HBox actions = new HBox(8);
                actions.setPrefWidth(220);
                actions.setMinWidth(220);
                actions.setMaxWidth(220);
                actions.setAlignment(Pos.CENTER);
                actions.setPadding(new Insets(0, 5, 0, 5));

                Button btnCalc = new Button(savedPr != null ? "Sửa Lương" : "Tính Lương");
                btnCalc.setStyle(savedPr != null ? 
                    "-fx-background-color: #FFF3E0; -fx-text-fill: #E65100; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand;" :
                    "-fx-background-color: #E3F2FD; -fx-text-fill: #1976D2; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand;");
                
                btnCalc.setOnAction(e -> {
                    PayrollForm form = new PayrollForm(mainUI, emp, monthStr, refreshListWrapper[0]);
                    form.show();
                });

                actions.getChildren().add(btnCalc);

                if (savedPr != null) {
                    Button btnPdf = new Button("📄 Phiếu Lương");
                    btnPdf.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand;");
                    btnPdf.setOnAction(e -> exportPaySlipToPDF(btnPdf.getScene().getWindow(), savedPr, emp));
                    actions.getChildren().add(btnPdf);
                }

                row.getChildren().addAll(lblCode, lblName, lblPos, lblBasic, lblDays, lblWork, lblTemp, lblNet, actions);
                
                row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;"));
                row.setOnMouseExited(e -> row.setStyle("-fx-background-color: white; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;"));

                tableRows.getChildren().add(row);
            }
        };

        cbMonth.valueProperty().addListener((obs, old, val) -> refreshListWrapper[0].run());
        cbYear.valueProperty().addListener((obs, old, val) -> refreshListWrapper[0].run());

        refreshListWrapper[0].run();
        root.getChildren().addAll(topBar, tableContainer);
        return root;
    }

    public static void exportPaySlipToPDF(javafx.stage.Window window, Payroll pr, Employee emp) {
        try {
            String payMonth = pr.getPayMonth();
            int year = Integer.parseInt(payMonth.substring(0, 4));
            int month = Integer.parseInt(payMonth.substring(5, 7));
            String displayMonth = "Tháng " + month + " Năm " + year;

            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Lưu Phiếu Lương PDF");
            fileChooser.setInitialFileName("PhieuLuong_" + emp.getEmployeeCode() + "_" + payMonth + ".pdf");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            java.io.File file = fileChooser.showSaveDialog(window);
            if (file == null) return;
            
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            // Landscape A4 Page
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf, com.itextpdf.kernel.geom.PageSize.A4.rotate());
            document.setMargins(20, 20, 20, 20);
            
            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);
            
            // Header: Company Info
            com.itextpdf.layout.element.Paragraph compName = new com.itextpdf.layout.element.Paragraph("CÔNG TY TNHH TM DV PHỤ TÙNG Ô TÔ MINH TÂM")
                .setFont(boldFont).setFontSize(9).setMargin(0);
            com.itextpdf.layout.element.Paragraph compAddr = new com.itextpdf.layout.element.Paragraph("Ngã tư An Dương Vương và Trương Định, P. Trần Phú, TP. Quảng Ngãi, T. Quảng Ngãi.")
                .setFont(font).setFontSize(8).setMargin(0);
            com.itextpdf.layout.element.Paragraph compMst = new com.itextpdf.layout.element.Paragraph("MST: 4300899201")
                .setFont(font).setFontSize(8).setMargin(0);
            
            document.add(compName);
            document.add(compAddr);
            document.add(compMst);
            
            // Title
            com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph("PHIẾU THANH TOÁN TIỀN LƯƠNG (Lương cơ bản cố định)")
                .setFont(boldFont).setFontSize(14).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginTop(10).setMarginBottom(2);
            
            com.itextpdf.layout.element.Paragraph subtitle = new com.itextpdf.layout.element.Paragraph(displayMonth)
                .setFont(font).setFontSize(11).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(15);
            
            document.add(title);
            document.add(subtitle);
            
            // Create Table
            float[] colWidths = {25f, 110f, 80f, 40f, 70f, 60f, 60f, 65f, 65f, 55f, 75f, 55f, 55f, 75f, 65f};
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(colWidths);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            table.setFontSize(8);
            
            com.itextpdf.kernel.colors.Color headerBg = new com.itextpdf.kernel.colors.DeviceRgb(178, 235, 242);
            
            // Helper to build header cells
            java.util.function.BiFunction<String, Integer, com.itextpdf.layout.element.Cell> makeHeaderCell = (text, rowspan) -> {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell(rowspan, 1)
                    .add(new com.itextpdf.layout.element.Paragraph(text).setFont(boldFont))
                    .setBackgroundColor(headerBg)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                return cell;
            };

            // Row 1 Headers
            table.addHeaderCell(makeHeaderCell.apply("STT", 2));
            table.addHeaderCell(makeHeaderCell.apply("Họ và tên", 2));
            table.addHeaderCell(makeHeaderCell.apply("Chức vụ", 2));
            table.addHeaderCell(makeHeaderCell.apply("Ngày công", 2));
            table.addHeaderCell(makeHeaderCell.apply("Lương chính", 2));
            table.addHeaderCell(makeHeaderCell.apply("Trách Nhiệm", 2));
            table.addHeaderCell(makeHeaderCell.apply("Phụ cấp", 2));
            table.addHeaderCell(makeHeaderCell.apply("Hoa hồng tư vấn", 2));
            table.addHeaderCell(makeHeaderCell.apply("Hoa hồng Dịch vụ", 2));
            table.addHeaderCell(makeHeaderCell.apply("Tăng ca (h)", 2));
            
            com.itextpdf.layout.element.Cell cellKyI = new com.itextpdf.layout.element.Cell(1, 3)
                .add(new com.itextpdf.layout.element.Paragraph("Kỳ I").setFont(boldFont))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            table.addHeaderCell(cellKyI);
            
            com.itextpdf.layout.element.Cell cellKyII = new com.itextpdf.layout.element.Cell(1, 2)
                .add(new com.itextpdf.layout.element.Paragraph("Kỳ II được lĩnh").setFont(boldFont))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            table.addHeaderCell(cellKyII);
            
            // Row 2 Headers
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Tổng số").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("BHXH").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Tạm ứng").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Số tiền").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Ký nhận").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            
            // Row 3 Numbering (1 to 15)
            for (int i = 1; i <= 15; i++) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(i)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            }
            
            double workDays = pr.getActualWorkDays();
            double basicSalary = pr.getBasicSalary();
            double baseWage = (basicSalary / pr.getTotalDays()) * workDays;
            
            double resp = pr.getAllowanceResponsibility();
            double oth = pr.getAllowanceOther();
            double cons = pr.getCommissionConsulting();
            double serv = pr.getCommissionService();
            double ot = pr.getOvertimePay();
            
            double totalEarn = baseWage + resp + oth + cons + serv + ot;
            
            double ins = pr.getSocialInsurance();
            double adv = pr.getAdvancePayment();
            double net = pr.getNetSalary();
            
            // Add employee row
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("1").setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(emp.getName()).setFont(font)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(emp.getPosition() != null ? emp.getPosition() : "").setFont(font)));
            
            String dayStr = String.format(workDays % 1 == 0 ? "%.0f" : "%.1f", workDays);
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(dayStr).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", baseWage)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", resp)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", oth)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", cons)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", serv)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", ot)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", totalEarn)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", ins)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", adv)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", net)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("").setFont(font)));
            
            // Total Row (same values)
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("").setFont(boldFont)));
            table.addCell(new com.itextpdf.layout.element.Cell(1, 2).add(new com.itextpdf.layout.element.Paragraph("Tổng cộng").setFont(boldFont)));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(dayStr).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", baseWage)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", resp)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", oth)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", pr.getCommissionConsulting())).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", pr.getCommissionService())).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", pr.getOvertimePay())).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", totalEarn)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", ins)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", adv)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", net)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("").setFont(boldFont)));
            
            document.add(table);
            
            // Total words
            String amountInWords = convertNumberToWords(net);
            com.itextpdf.layout.element.Paragraph wordsPara = new com.itextpdf.layout.element.Paragraph("Số tiền bằng chữ: " + amountInWords)
                .setFont(font).setFontSize(9).setItalic().setMarginTop(10);
            document.add(wordsPara);
            
            // Signatures block
            float[] sigWidths = {260f, 260f, 260f};
            com.itextpdf.layout.element.Table sigTable = new com.itextpdf.layout.element.Table(sigWidths);
            sigTable.setMarginTop(20);
            
            sigTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Người Nhận Lương\n(Ký và ghi rõ họ tên)").setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)).setBorder(null));
            sigTable.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Người Lập Phiếu\n(Ký, họ tên)").setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)).setBorder(null));
            
            com.itextpdf.layout.element.Cell bossCell = new com.itextpdf.layout.element.Cell();
            bossCell.add(new com.itextpdf.layout.element.Paragraph("Giám Đốc\n(Ký, đóng dấu)").setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            bossCell.add(new com.itextpdf.layout.element.Paragraph("\n\n\n\nTÂM").setFont(boldFont).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            bossCell.add(new com.itextpdf.layout.element.Paragraph("PHẠM MINH TÂM").setFont(boldFont).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            bossCell.setBorder(null);
            sigTable.addCell(bossCell);
            
            document.add(sigTable);
            document.close();
            
            Alert alert = util.AlertHelper.createAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xuất phiếu lương thành công!");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = util.AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất PDF: " + e.getMessage());
            alert.show();
        }
    }

    public static void exportAllPayrollToPDF(javafx.stage.Window window, String payMonth) {
        try {
            int year = Integer.parseInt(payMonth.substring(0, 4));
            int month = Integer.parseInt(payMonth.substring(5, 7));
            String displayMonth = "Tháng " + month + " Năm " + year;

            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Lưu Bảng Thanh Toán Tiền Lương PDF");
            fileChooser.setInitialFileName("BangLuong_" + payMonth + ".pdf");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            java.io.File file = fileChooser.showSaveDialog(window);
            if (file == null) return;
            
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            // Landscape A4 Page
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf, com.itextpdf.kernel.geom.PageSize.A4.rotate());
            document.setMargins(20, 20, 20, 20);
            
            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);
            
            // Header: Company Info
            com.itextpdf.layout.element.Paragraph compName = new com.itextpdf.layout.element.Paragraph("CÔNG TY TNHH TM DV PHỤ TÙNG Ô TÔ MINH TÂM")
                .setFont(boldFont).setFontSize(9).setMargin(0);
            com.itextpdf.layout.element.Paragraph compAddr = new com.itextpdf.layout.element.Paragraph("Ngã tư An Dương Vương và Trương Định, P. Trần Phú, TP. Quảng Ngãi, T. Quảng Ngãi.")
                .setFont(font).setFontSize(8).setMargin(0);
            com.itextpdf.layout.element.Paragraph compMst = new com.itextpdf.layout.element.Paragraph("MST: 4300899201")
                .setFont(font).setFontSize(8).setMargin(0);
            
            document.add(compName);
            document.add(compAddr);
            document.add(compMst);
            
            // Title
            com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph("BẢNG THANH TOÁN TIỀN LƯƠNG (Lương cơ bản cố định)")
                .setFont(boldFont).setFontSize(14).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginTop(10).setMarginBottom(2);
            
            com.itextpdf.layout.element.Paragraph subtitle = new com.itextpdf.layout.element.Paragraph(displayMonth)
                .setFont(font).setFontSize(11).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(15);
            
            document.add(title);
            document.add(subtitle);
            
            // Create Table
            float[] colWidths = {25f, 110f, 80f, 40f, 70f, 60f, 60f, 65f, 65f, 55f, 75f, 55f, 55f, 75f, 65f};
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(colWidths);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            table.setFontSize(8);
            
            com.itextpdf.kernel.colors.Color headerBg = new com.itextpdf.kernel.colors.DeviceRgb(178, 235, 242);
            
            // Helper to build header cells
            java.util.function.BiFunction<String, Integer, com.itextpdf.layout.element.Cell> makeHeaderCell = (text, rowspan) -> {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell(rowspan, 1)
                    .add(new com.itextpdf.layout.element.Paragraph(text).setFont(boldFont))
                    .setBackgroundColor(headerBg)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                return cell;
            };

            // Row 1 Headers
            table.addHeaderCell(makeHeaderCell.apply("STT", 2));
            table.addHeaderCell(makeHeaderCell.apply("Họ và tên", 2));
            table.addHeaderCell(makeHeaderCell.apply("Chức vụ", 2));
            table.addHeaderCell(makeHeaderCell.apply("Ngày công", 2));
            table.addHeaderCell(makeHeaderCell.apply("Lương chính", 2));
            table.addHeaderCell(makeHeaderCell.apply("Trách Nhiệm", 2));
            table.addHeaderCell(makeHeaderCell.apply("Phụ cấp", 2));
            table.addHeaderCell(makeHeaderCell.apply("Hoa hồng tư vấn", 2));
            table.addHeaderCell(makeHeaderCell.apply("Hoa hồng Dịch vụ", 2));
            table.addHeaderCell(makeHeaderCell.apply("Tăng ca (h)", 2));
            
            com.itextpdf.layout.element.Cell cellKyI = new com.itextpdf.layout.element.Cell(1, 3)
                .add(new com.itextpdf.layout.element.Paragraph("Kỳ I").setFont(boldFont))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            table.addHeaderCell(cellKyI);
            
            com.itextpdf.layout.element.Cell cellKyII = new com.itextpdf.layout.element.Cell(1, 2)
                .add(new com.itextpdf.layout.element.Paragraph("Kỳ II được lĩnh").setFont(boldFont))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            table.addHeaderCell(cellKyII);
            
            // Row 2 Headers
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Tổng số").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("BHXH").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Tạm ứng").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Số tiền").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Ký nhận").setFont(boldFont)).setBackgroundColor(headerBg).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            
            // Row 3 Numbering (1 to 15)
            for (int i = 1; i <= 15; i++) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(i)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            }
            
            // Retrieve employees
            EmployeeService empService = new EmployeeService();
            AttendanceService attService = new AttendanceService();
            PayrollService prService = new PayrollService();
            List<Employee> employees = empService.getAllEmployees();
            
            YearMonth ym = YearMonth.of(year, month);
            int totalDaysInMonth = ym.lengthOfMonth();
            
            int sttVal = 1;
            
            double sumWorkDays = 0;
            double sumBaseWage = 0;
            double sumResp = 0;
            double sumOth = 0;
            double sumCons = 0;
            double sumServ = 0;
            double sumOt = 0;
            double sumTotal = 0;
            double sumInsurance = 0;
            double sumAdvance = 0;
            double sumNet = 0;
            
            for (Employee emp : employees) {
                double workDays = attService.getActualWorkDays(emp.getId(), payMonth);
                Payroll pr = prService.getPayroll(emp.getId(), payMonth);
                
                double basicSalary = emp.getBasicSalary();
                double baseWage = (basicSalary / totalDaysInMonth) * workDays;
                
                double resp, oth, cons, serv, ot, ins, adv;
                if (pr != null) {
                    resp = pr.getAllowanceResponsibility();
                    oth = pr.getAllowanceOther();
                    cons = pr.getCommissionConsulting();
                    serv = pr.getCommissionService();
                    ot = pr.getOvertimePay();
                    ins = pr.getSocialInsurance();
                    adv = pr.getAdvancePayment();
                } else {
                    resp = emp.getAllowanceResponsibility();
                    oth = emp.getAllowanceOther();
                    cons = 0;
                    serv = 0;
                    ot = 0;
                    ins = emp.getSocialInsurance();
                    adv = 0;
                }
                
                double totalEarn = baseWage + resp + oth + cons + serv + ot;
                double net = totalEarn - ins - adv;
                
                // Add to sums
                sumWorkDays += workDays;
                sumBaseWage += baseWage;
                sumResp += resp;
                sumOth += oth;
                sumCons += cons;
                sumServ += serv;
                sumOt += ot;
                sumTotal += totalEarn;
                sumInsurance += ins;
                sumAdvance += adv;
                sumNet += net;
                
                // Row cells
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(sttVal++)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(emp.getName()).setFont(font)));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(emp.getPosition() != null ? emp.getPosition() : "").setFont(font)));
                
                String dayStr = String.format(workDays % 1 == 0 ? "%.0f" : "%.1f", workDays);
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(dayStr).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
                
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", baseWage)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", resp)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", oth)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", cons)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", serv)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", ot)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", totalEarn)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", ins)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", adv)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", net)).setFont(font)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("").setFont(font)));
            }
            
            // Total Row
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("").setFont(boldFont)));
            table.addCell(new com.itextpdf.layout.element.Cell(1, 2).add(new com.itextpdf.layout.element.Paragraph("Tổng cộng").setFont(boldFont)));
            
            String totalDaysStr = String.format(sumWorkDays % 1 == 0 ? "%.0f" : "%.1f", sumWorkDays);
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(totalDaysStr).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumBaseWage)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumResp)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumOth)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumCons)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumServ)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumOt)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumTotal)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumInsurance)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumAdvance)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", sumNet)).setFont(boldFont)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("").setFont(boldFont)));
            
            document.add(table);
            
            // Total words
            String amountInWords = convertNumberToWords(sumNet);
            com.itextpdf.layout.element.Paragraph wordsPara = new com.itextpdf.layout.element.Paragraph("Số tiền bằng chữ: " + amountInWords)
                .setFont(font).setFontSize(9).setItalic().setMarginTop(10);
            document.add(wordsPara);
            
            // Signatures block
            float[] sigWidths = {260f, 260f, 260f};
            com.itextpdf.layout.element.Table sigTable = new com.itextpdf.layout.element.Table(sigWidths);
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
            
            Alert alert = util.AlertHelper.createAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xuất bảng thanh toán tiền lương thành công!");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = util.AlertHelper.createAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất bảng lương PDF: " + e.getMessage());
            alert.show();
        }
    }

    public static String convertNumberToWords(double number) {
        long amount = Math.round(number);
        if (amount == 0) return "Không đồng";
        
        String[] units = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"};
        String result = "";
        int unitIndex = 0;
        
        while (amount > 0) {
            long block = amount % 1000;
            if (block > 0) {
                String blockText = readBlock((int) block, amount >= 1000);
                result = blockText + " " + units[unitIndex] + " " + result;
            }
            amount /= 1000;
            unitIndex++;
        }
        
        result = result.trim().replaceAll("\\s+", " ");
        if (result.endsWith("mười")) {
            result = result + " ";
        }
        
        // Capitalize first letter
        result = Character.toUpperCase(result.charAt(0)) + result.substring(1) + " đồng";
        return result;
    }

    private static String readBlock(int number, boolean showZero) {
        String[] digits = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        int hundreds = number / 100;
        int tens = (number % 100) / 10;
        int ones = number % 10;
        
        StringBuilder sb = new StringBuilder();
        
        if (hundreds > 0 || showZero) {
            sb.append(digits[hundreds]).append(" trăm ");
        }
        
        if (tens > 0) {
            if (tens == 1) {
                sb.append("mười ");
            } else {
                sb.append(digits[tens]).append(" mươi ");
            }
        } else if (hundreds > 0 && ones > 0) {
            sb.append("lẻ ");
        }
        
        if (ones > 0) {
            if (ones == 1 && tens > 1) {
                sb.append("mốt");
            } else if (ones == 5 && tens >= 1) {
                sb.append("lăm");
            } else {
                sb.append(digits[ones]);
            }
        }
        
        return sb.toString().trim();
    }

    private static List<String> getAvailableYears() {
        java.util.Set<String> years = new java.util.TreeSet<>(java.util.Comparator.reverseOrder());
        int curYear = LocalDate.now().getYear();
        for (int y = curYear; y >= 2020; y--) {
            years.add(String.valueOf(y));
        }
        try {
            List<Invoice> invoices = new InvoiceService().getAllInvoices();
            for (Invoice inv : invoices) {
                if (inv.getCreatedAt() != null && inv.getCreatedAt().length() >= 4) {
                    years.add(inv.getCreatedAt().substring(0, 4));
                }
            }
        } catch (Exception e) {}
        try {
            List<Appointment> appts = new AppointmentService().getAllAppointments();
            for (Appointment appt : appts) {
                if (appt.getAppointmentDate() != null && appt.getAppointmentDate().length() >= 4) {
                    years.add(appt.getAppointmentDate().substring(0, 4));
                }
            }
        } catch (Exception e) {}
        return List.copyOf(years);
    }

    private static int parseIntSafe(String str) {
        if (str == null) return 0;
        try {
            return Integer.parseInt(str.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
