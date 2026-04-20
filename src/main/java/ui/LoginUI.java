package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class LoginUI extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");

        VBox loginBox = createLoginBox(stage);
        root.setCenter(loginBox);

        Scene scene = new Scene(root, 1000, 650);
        try {
            String css = getClass().getResource("/global-styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {}
        stage.setScene(scene);
        stage.setTitle("🚗 MTProAuto - Đăng Nhập");
        stage.show();
    }

    private VBox createLoginBox(Stage stage) {
        VBox loginBox = new VBox(25);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(50));
        loginBox.setMaxWidth(450);
        loginBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 10);"
        );

        // Logo
        Circle logoCircle = new Circle(40);
        logoCircle.setFill(Color.web("#2196F3"));
        
        StackPane logoContainer = new StackPane();
        Label logoText = new Label("■");
        logoText.setStyle("-fx-font-size: 50px; -fx-text-fill: white;");
        logoContainer.getChildren().addAll(logoCircle, logoText);

        // Title
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        
        Label title = new Label("MTProAuto");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        Label subtitle = new Label("Hệ Thống Quản Lý Rửa Xe");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        
        titleBox.getChildren().addAll(title, subtitle);

        // Username field
        VBox usernameBox = new VBox(8);
        Label lblUsername = new Label("Tên đăng nhập");
        lblUsername.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Nhập tên đăng nhập");
        txtUsername.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #212121;" +
            "-fx-prompt-text-fill: #9e9e9e;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 14px 18px;" +
            "-fx-font-size: 14px;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 10;"
        );
        
        txtUsername.setOnMouseEntered(e -> txtUsername.setStyle(
            txtUsername.getStyle() + "-fx-background-color: #eeeeee;"
        ));
        txtUsername.setOnMouseExited(e -> txtUsername.setStyle(
            txtUsername.getStyle().replace("-fx-background-color: #eeeeee;", "-fx-background-color: #f5f5f5;")
        ));
        
        usernameBox.getChildren().addAll(lblUsername, txtUsername);

        // Password field
        VBox passwordBox = new VBox(8);
        Label lblPassword = new Label("Mật khẩu");
        lblPassword.setStyle("-fx-font-size: 13px; -fx-text-fill: #424242; -fx-font-weight: 600;");
        
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Nhập mật khẩu");
        txtPassword.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #212121;" +
            "-fx-prompt-text-fill: #9e9e9e;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 14px 18px;" +
            "-fx-font-size: 14px;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 10;"
        );
        
        txtPassword.setOnMouseEntered(e -> txtPassword.setStyle(
            txtPassword.getStyle() + "-fx-background-color: #eeeeee;"
        ));
        txtPassword.setOnMouseExited(e -> txtPassword.setStyle(
            txtPassword.getStyle().replace("-fx-background-color: #eeeeee;", "-fx-background-color: #f5f5f5;")
        ));
        
        passwordBox.getChildren().addAll(lblPassword, txtPassword);

        // Remember me checkbox
        CheckBox chkRemember = new CheckBox("Ghi nhớ đăng nhập");
        chkRemember.setStyle("-fx-font-size: 13px; -fx-text-fill: #616161;");

        // Login button
        Button btnLogin = new Button("Đăng Nhập");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(
            btnLogin.getStyle() + "-fx-background-color: #1976D2;"
        ));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle(
            btnLogin.getStyle().replace("-fx-background-color: #1976D2;", "-fx-background-color: #2196F3;")
        ));
        
        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            
            // Simple validation (you can add database check here)
            if (username.equals("admin") && password.equals("admin")) {
                // Open main UI directly
                MainUI mainUI = new MainUI();
                Stage mainStage = new Stage();
                try {
                    mainUI.start(mainStage);
                    stage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên đăng nhập hoặc mật khẩu không đúng!");
            }
        });

        // Forgot password link
        Hyperlink linkForgot = new Hyperlink("Quên mật khẩu?");
        linkForgot.setStyle("-fx-font-size: 13px; -fx-text-fill: #2196F3;");
        linkForgot.setAlignment(Pos.CENTER);

        loginBox.getChildren().addAll(
            logoContainer, 
            titleBox, 
            usernameBox, 
            passwordBox, 
            chkRemember, 
            btnLogin, 
            linkForgot
        );

        return loginBox;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: white;" +
            "-fx-font-family: 'System';"
        );
        
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
