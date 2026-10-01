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

    public static String formatCurrency(double amount) {
        if (amount < 0) return "0";
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", symbols);
        return df.format(amount);
    }

    public static double parseCurrency(String text) {
        if (text == null) return 0.0;
        String clean = text.replaceAll("[^\\d]", "");
        if (clean.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(clean);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static void formatCurrencyInput(TextField textField) {
        if (textField == null) return;

        setupIMEFix(textField);

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.BACK_SPACE) {
                int caret = textField.getCaretPosition();
                String text = textField.getText();
                if (caret > 1 && text != null && caret <= text.length() && text.charAt(caret - 1) == '.') {
                    textField.deleteText(caret - 2, caret);
                    event.consume();
                }
            }
        });

        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) return;

            String clean = newVal.replaceAll("[^\\d]", "");
            if (clean.isEmpty()) {
                javafx.application.Platform.runLater(() -> textField.setText(""));
                return;
            }

            try {
                long number = Long.parseLong(clean);
                java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", symbols);
                String formatted = df.format(number);

                if (!newVal.equals(formatted)) {
                    int caretPos = textField.getCaretPosition();
                    int digitsBeforeCaret = 0;
                    for (int i = 0; i < Math.min(caretPos, newVal.length()); i++) {
                        if (Character.isDigit(newVal.charAt(i))) {
                            digitsBeforeCaret++;
                        }
                    }

                    final int targetDigits = digitsBeforeCaret;
                    javafx.application.Platform.runLater(() -> {
                        textField.setText(formatted);
                        int newCaret = 0;
                        int count = 0;
                        for (int i = 0; i < formatted.length(); i++) {
                            if (Character.isDigit(formatted.charAt(i))) {
                                count++;
                            }
                            if (count == targetDigits) {
                                newCaret = i + 1;
                                break;
                            }
                        }
                        if (targetDigits == 0) newCaret = 0;
                        if (newCaret > formatted.length()) newCaret = formatted.length();
                        textField.positionCaret(newCaret);
                    });
                }
            } catch (NumberFormatException ignored) {}
        });
    }
}
