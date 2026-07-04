package ui;

import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.InputMethodEvent;

public class UIUtils {

    /**
     * Fixes the macOS Telex/EVKey focus-carryover bug by ignoring all key
     * and input method events that occur immediately (within 150ms) after focus is gained.
     */
    public static void setupIMEFix(TextInputControl textField) {
        if (textField == null) return;

        // Record the timestamp when focus is gained
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Focus gained
                textField.getProperties().put("focusGainedTime", System.currentTimeMillis());
            }
        });

        // Ignore all KeyEvents within 150ms of focus gain (simulated by EVKey/Telex buffer flushes)
        textField.addEventFilter(KeyEvent.ANY, event -> {
            Long focusTime = (Long) textField.getProperties().get("focusGainedTime");
            if (focusTime != null && (System.currentTimeMillis() - focusTime < 150)) {
                event.consume();
            }
        });

        // Ignore all IME text changes within 150ms of focus gain (carried over composition)
        textField.addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, event -> {
            Long focusTime = (Long) textField.getProperties().get("focusGainedTime");
            if (focusTime != null && (System.currentTimeMillis() - focusTime < 150)) {
                event.consume();
            }
        });
    }
}
