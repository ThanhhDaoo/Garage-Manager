package ui;

import javafx.scene.control.TextInputControl;

public class UIUtils {

    /**
     * Safely configures text input controls without interfering with JavaFX's
     * internal Cocoa InputMethodContext event lifecycle.
     */
    public static void setupIMEFix(TextInputControl textField) {
        if (textField == null) return;

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                try {
                    textField.deselect();
                } catch (Exception ignored) {}
            }
        });
    }
}
