package util;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class AlertHelper {
    
    public static void applyTimesNewRomanFont(Alert alert) {
        try {
            DialogPane dialogPane = alert.getDialogPane();
            String css = AlertHelper.class.getResource("/global-styles.css").toExternalForm();
            dialogPane.getStylesheets().add(css);
        } catch (Exception e) {
            // If CSS not found, apply inline style
            alert.getDialogPane().setStyle("-fx-font-family: 'Times New Roman';");
        }
    }
    
    public static Alert createAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyTimesNewRomanFont(alert);
        return alert;
    }
}
