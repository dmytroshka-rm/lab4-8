package taxsystem.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.util.Objects;

public final class UiStyles {

    private UiStyles() {}

    public static void applyAppTheme(Scene scene) {
        String url = Objects.requireNonNull(
                UiStyles.class.getResource("/styles/app.css"),
                "classpath:/styles/app.css"
        ).toExternalForm();
        scene.getStylesheets().add(url);
    }

    public static void styleButton(Button button, ButtonVariant variant) {
        button.getStyleClass().add("btn");
        button.getStyleClass().add(variant.className);
    }

    public enum ButtonVariant {
        PRIMARY("btn-primary"),
        SUCCESS("btn-success"),
        DANGER("btn-danger"),
        SECONDARY("btn-secondary"),
        OUTLINE("btn-outline");

        private final String className;

        ButtonVariant(String className) {
            this.className = className;
        }
    }
}
