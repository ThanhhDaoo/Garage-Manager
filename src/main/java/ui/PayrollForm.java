package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Employee;
import model.Payroll;
import service.AttendanceService;
import service.PayrollService;

import java.time.YearMonth;

public class PayrollForm {
    private Stage stage;
    private MainUI mainUI;
    private Employee employee;
    private String payMonth;
    private Runnable onSave;

    private TextField txtResponsibility;
    private TextField txtOther;
    private TextField txtConsulting;
    private TextField txtServiceComm;
    private TextField txtOvertime;
    private TextField txtInsurance;
    private TextField txtAdvance;

    public PayrollForm(MainUI mainUI, Employee emp, String payMonth, Runnable onSave) {
        this.mainUI = mainUI;
        this.employee = emp;
        this.payMonth = payMonth;
        this.onSave = onSave;
    }

    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Tính Lương Nhân Viên - " + employee.getName());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-background-color: #f8f9fa;");

        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("📊 Tính Toán Chi Tiết Lương Tháng: " + payMonth);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #212121;");

        VBox infoSection = createInfoSection();
        VBox formSection = createFormSection();
        
        // Net Salary Box
        VBox netSalaryBox = new VBox(5);
        netSalaryBox.setPadding(new Insets(15));
        netSalaryBox.setStyle("-fx-background-color: #E3F2FD; -fx-background-radius: 8; -fx-alignment: center;");
        
        Label lblNetTitle = new Label("LƯƠNG THỰC NHẬN");
        lblNetTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #1976D2; -fx-font-weight: bold;");
        Label lblNetSalaryVal = new Label("0 đ");
        lblNetSalaryVal.setStyle("-fx-font-size: 22px; -fx-text-fill: #1565C0; -fx-font-weight: bold;");
        netSalaryBox.getChildren().addAll(lblNetTitle, lblNetSalaryVal);

        // Recalculation logic
        int year = Integer.parseInt(payMonth.substring(0, 4));
        int month = Integer.parseInt(payMonth.substring(5, 7));
        int totalDays = YearMonth.of(year, month).lengthOfMonth();
        double actualWorkDays = new AttendanceService().getActualWorkDays(employee.getId(), payMonth);

        Runnable recalculate = () -> {
            double resp = parseDoubleSafe(txtResponsibility.getText());
            double oth = parseDoubleSafe(txtOther.getText());
            double cons = parseDoubleSafe(txtConsulting.getText());
            double serv = parseDoubleSafe(txtServiceComm.getText());
            double ot = parseDoubleSafe(txtOvertime.getText());
            double ins = parseDoubleSafe(txtInsurance.getText());
            double adv = parseDoubleSafe(txtAdvance.getText());

            double basePortion = (employee.getBasicSalary() / totalDays) * actualWorkDays;
            double net = basePortion + resp + oth + cons + serv + ot - ins - adv;
            
            lblNetSalaryVal.setText(String.format("%,.0f đ", net));
        };

        txtResponsibility.textProperty().addListener((obs, old, val) -> recalculate.run());
        txtOther.textProperty().addListener((obs, old, val) -> recalculate.run());
        txtConsulting.textProperty().addListener((obs, old, val) -> recalculate.run());
        txtServiceComm.textProperty().addListener((obs, old, val) -> recalculate.run());
        txtOvertime.textProperty().addListener((obs, old, val) -> recalculate.run());
        txtInsurance.textProperty().addListener((obs, old, val) -> recalculate.run());
        txtAdvance.textProperty().addListener((obs, old, val) -> recalculate.run());

        recalculate.run();

        HBox actionButtons = createActionButtons(totalDays, actualWorkDays);

        mainContent.getChildren().addAll(title, infoSection, formSection, netSalaryBox, actionButtons);
        scrollPane.setContent(mainContent);

        Scene scene = new Scene(scrollPane, 700, 800);
        try {
            String css = MainUI.class.getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
        stage.setScene(scene);
        stage.show();
    }

    private VBox createInfoSection() {
        VBox infoSection = new VBox(15);
        infoSection.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 30;"
        );

        Label infoSectionTitle = new Label("Thông Tin Nhân Viên");
        infoSectionTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976D2; -fx-font-weight: 700; -fx-padding: 0 0 10 0;");

        VBox infoCard = new VBox(8);
        infoCard.setPadding(new Insets(15));
        infoCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");
        
        Label lblName = new Label("Họ tên: " + employee.getName() + " (" + employee.getEmployeeCode() + ")");
        lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: #212121; -fx-font-size: 14px;");
        Label lblPos = new Label("Chức vụ: " + employee.getPosition());
        lblPos.setStyle("-fx-text-fill: #616161; -fx-font-size: 14px;");
        Label lblBasicSalaryInfo = new Label("Lương cơ bản: " + String.format("%,.0f đ", employee.getBasicSalary()));
        lblBasicSalaryInfo.setStyle("-fx-text-fill: #616161; -fx-font-size: 14px;");

        infoCard.getChildren().addAll(lblName, lblPos, lblBasicSalaryInfo);
        infoSection.getChildren().addAll(infoSectionTitle, infoCard);
        return infoSection;
    }

    private VBox createFormSection() {
        VBox formSection = new VBox(20);
        formSection.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-padding: 30;"
        );

        Label sectionTitle = new Label("Thông Tin Lương & Phụ Cấp");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976D2; -fx-font-weight: 700; -fx-padding: 0 0 10 0;");

        Payroll existing = new PayrollService().getPayroll(employee.getId(), payMonth);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #424242; -fx-font-weight: 600;";
        String fieldStyle = "-fx-background-color: #f5f5f5; -fx-padding: 12px 15px; -fx-background-radius: 8; -fx-border-color: transparent; -fx-font-size: 14px;";

        Label lblResponsibility = new Label("Phụ cấp trách nhiệm (VNĐ)");
        lblResponsibility.setStyle(labelStyle);
        txtResponsibility = new TextField(existing != null ? String.format("%.0f", existing.getAllowanceResponsibility()) : "0");
        txtResponsibility.setPrefWidth(300);
        txtResponsibility.setStyle(fieldStyle);

        Label lblOther = new Label("Phụ cấp khác (VNĐ)");
        lblOther.setStyle(labelStyle);
        txtOther = new TextField(existing != null ? String.format("%.0f", existing.getAllowanceOther()) : "0");
        txtOther.setPrefWidth(300);
        txtOther.setStyle(fieldStyle);

        Label lblConsulting = new Label("Hoa hồng tư vấn (VNĐ)");
        lblConsulting.setStyle(labelStyle);
        txtConsulting = new TextField(existing != null ? String.format("%.0f", existing.getCommissionConsulting()) : "0");
        txtConsulting.setPrefWidth(300);
        txtConsulting.setStyle(fieldStyle);

        Label lblServiceComm = new Label("Hoa hồng dịch vụ (VNĐ)");
        lblServiceComm.setStyle(labelStyle);
        txtServiceComm = new TextField(existing != null ? String.format("%.0f", existing.getCommissionService()) : "0");
        txtServiceComm.setPrefWidth(300);
        txtServiceComm.setStyle(fieldStyle);

        Label lblOvertime = new Label("Tiền tăng ca (VNĐ)");
        lblOvertime.setStyle(labelStyle);
        txtOvertime = new TextField(existing != null ? String.format("%.0f", existing.getOvertimePay()) : "0");
        txtOvertime.setPrefWidth(300);
        txtOvertime.setStyle(fieldStyle);

        Label lblInsurance = new Label("Bảo hiểm xã hội (-) (VNĐ)");
        lblInsurance.setStyle(labelStyle);
        txtInsurance = new TextField(existing != null ? String.format("%.0f", existing.getSocialInsurance()) : "0");
        txtInsurance.setPrefWidth(300);
        txtInsurance.setStyle(fieldStyle);

        Label lblAdvance = new Label("Tạm ứng (-) (VNĐ)");
        lblAdvance.setStyle(labelStyle);
        txtAdvance = new TextField(existing != null ? String.format("%.0f", existing.getAdvancePayment()) : "0");
        txtAdvance.setPrefWidth(300);
        txtAdvance.setStyle(fieldStyle);

        grid.add(lblResponsibility, 0, 0); grid.add(txtResponsibility, 1, 0);
        grid.add(lblOther, 0, 1); grid.add(txtOther, 1, 1);
        grid.add(lblConsulting, 0, 2); grid.add(txtConsulting, 1, 2);
        grid.add(lblServiceComm, 0, 3); grid.add(txtServiceComm, 1, 3);
        grid.add(lblOvertime, 0, 4); grid.add(txtOvertime, 1, 4);
        grid.add(lblInsurance, 0, 5); grid.add(txtInsurance, 1, 5);
        grid.add(lblAdvance, 0, 6); grid.add(txtAdvance, 1, 6);

        formSection.getChildren().addAll(sectionTitle, grid);
        return formSection;
    }

    private HBox createActionButtons(int totalDays, double actualWorkDays) {
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

        Button btnSaveAndExport = new Button("Lưu & Xuất Phiếu Lương");
        btnSaveAndExport.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12px 30px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnSaveAndExport.setOnMouseEntered(e -> btnSaveAndExport.setOpacity(0.9));
        btnSaveAndExport.setOnMouseExited(e -> btnSaveAndExport.setOpacity(1.0));
        btnSaveAndExport.setOnAction(e -> {
            double resp = parseDoubleSafe(txtResponsibility.getText());
            double oth = parseDoubleSafe(txtOther.getText());
            double cons = parseDoubleSafe(txtConsulting.getText());
            double serv = parseDoubleSafe(txtServiceComm.getText());
            double ot = parseDoubleSafe(txtOvertime.getText());
            double ins = parseDoubleSafe(txtInsurance.getText());
            double adv = parseDoubleSafe(txtAdvance.getText());

            double basePortion = (employee.getBasicSalary() / totalDays) * actualWorkDays;
            double net = basePortion + resp + oth + cons + serv + ot - ins - adv;

            Payroll pr = new Payroll();
            pr.setEmployeeId(employee.getId());
            pr.setPayMonth(payMonth);
            pr.setTotalDays(totalDays);
            pr.setActualWorkDays(actualWorkDays);
            pr.setBasicSalary(employee.getBasicSalary());
            pr.setAllowanceResponsibility(resp);
            pr.setAllowanceOther(oth);
            pr.setCommissionConsulting(cons);
            pr.setCommissionService(serv);
            pr.setOvertimePay(ot);
            pr.setSocialInsurance(ins);
            pr.setAdvancePayment(adv);
            pr.setNetSalary(net);

            boolean success = new PayrollService().savePayroll(pr);
            if (success) {
                Payroll saved = new PayrollService().getPayroll(employee.getId(), payMonth);
                HRHelper.exportPaySlipToPDF(stage, saved, employee);
                if (onSave != null) onSave.run();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu bảng lương!");
                alert.setHeaderText(null);
                alert.show();
            }
        });

        buttons.getChildren().addAll(btnCancel, btnSaveAndExport);
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
