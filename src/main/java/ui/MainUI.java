package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainUI extends Application {

    @Override
    public void start(Stage stage) {

        TableView<String> table = new TableView<>();

        VBox root = new VBox(table);

        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.setTitle("Garage Manager");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}