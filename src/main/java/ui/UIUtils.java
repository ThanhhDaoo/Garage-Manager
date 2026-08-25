package ui;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;

public class UIUtils {

    public static void clearFocus(Stage stage) {
        try {
            if (stage != null && stage.getScene() != null) {
                Node root = stage.getScene().getRoot();
                if (root != null) {
                    root.requestFocus();
                }
            }
        } catch (Exception ignored) {}
    }

    public static void makeSafeSearchField(TextField searchField) {
        if (searchField == null) return;

        searchField.setFocusTraversable(false);

        searchField.setOnMouseClicked(e -> {
            searchField.setFocusTraversable(true);
            searchField.requestFocus();
        });

        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                searchField.setFocusTraversable(false);
                try {
                    searchField.deselect();
                } catch (Exception ignored) {}
            }
        });
    }

    public static void setupIMEFix(TextInputControl textField) {
        if (textField instanceof TextField) {
            makeSafeSearchField((TextField) textField);
        } else if (textField != null) {
            textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    try {
                        textField.deselect();
                    } catch (Exception ignored) {}
                }
            });
        }
    }
}
