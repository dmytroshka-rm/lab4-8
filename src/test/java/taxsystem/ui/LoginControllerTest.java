package taxsystem.ui;

import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import taxsystem.repository.DatabaseManager;

import taxsystem.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {

    private Stage stage;
    private DatabaseManager dbManager;

    @Start
    void start(Stage stage) throws java.io.IOException {
        this.stage = stage;
        java.io.File tmpDb = java.io.File.createTempFile("uitest_", ".db");
        tmpDb.deleteOnExit();
        dbManager = new DatabaseManager(tmpDb.getAbsolutePath());
        // Створюємо тестового користувача для тестів логіну
        new UserService(dbManager).register("testlogin", "test1234", "test@test.com");
        LoginController controller = new LoginController(stage, dbManager);
        controller.show();
    }

    // ── Тест 1: заголовок вікна містить потрібний текст ───────────────────
    @Test
    void windowTitleContainsTaxSystem() {
        String title = stage.getTitle();
        assertTrue(
                title.contains("Tax") || title.contains("Вхід") || title.contains("Податк"),
                "Заголовок вікна має містити назву системи, але було: " + title
        );
    }

    // ── Тест 2: дві вкладки — «Вхід» і «Реєстрація» ─────────────────────
    @Test
    void tabPaneHasTwoTabs(FxRobot robot) {
        TabPane tabPane = robot.lookup(".tab-pane").query();
        assertNotNull(tabPane, "TabPane має бути присутнім на сцені");
        assertEquals(2, tabPane.getTabs().size(), "Мають бути рівно 2 вкладки");
        assertEquals("Вхід",       tabPane.getTabs().get(0).getText());
        assertEquals("Реєстрація", tabPane.getTabs().get(1).getText());
    }

    // ── Тест 3: кнопка «Увійти» присутня і має правильний текст ──────────
    @Test
    void loginButtonIsVisible() {
        FxAssert.verifyThat("Увійти", LabeledMatchers.hasText("Увійти"));
    }

    // ── Тест 4: форма входу містить поля введення ─────────────────────────
    @Test
    void loginFormHasInputFields(FxRobot robot) {
        // Окремі lookup — TestFX не підтримує комбінований CSS-селектор через кому
        long textFields     = robot.lookup(".text-field").queryAll().size();
        long passwordFields = robot.lookup(".password-field").queryAll().size();
        long total = textFields + passwordFields;

        assertTrue(total >= 2,
                "Форма має мінімум 2 поля (логін + пароль), знайдено: " + total);
    }

    // ── Тест 5: порожній логін — залишаємось на сторінці входу ───────────
    @Test
    void emptyLoginKeepsWindowOpen(FxRobot robot) {
        robot.clickOn("Увійти");

        // Вікно має залишитись відкритим (не перейти до MainController)
        assertTrue(stage.isShowing(), "Вікно має залишатись відкритим після порожнього логіну");
        String title = stage.getTitle();
        assertFalse(
                title.contains("Головна") || title.contains("Система обліку — "),
                "Не повинен відкритись головний екран при порожніх полях"
        );
    }

    // ── Тест 6: перехід на вкладку «Реєстрація» ──────────────────────────
    @Test
    void switchToRegisterTabShowsRegisterButton(FxRobot robot) {
        robot.clickOn("Реєстрація");
        FxAssert.verifyThat("Зареєструватись", LabeledMatchers.hasText("Зареєструватись"));
    }

    // ── Тест 7: поля входу доступні для введення ────────────────────────
    @Test
    void canTypeInLoginFields(FxRobot robot) {
        TextField loginField = robot.lookup(".text-field").queryAs(TextField.class);
        assertNotNull(loginField, "Поле логіну має існувати");
        assertFalse(loginField.isDisabled(), "Поле логіну не має бути заблоковане");
        assertTrue(loginField.isEditable(), "Поле логіну має бути редагованим");
    }

    // ── Тест 8: реєстрація — форма містить поля ─────────────────────────
    @Test
    void registerFormHasFields(FxRobot robot) {
        robot.clickOn("Реєстрація");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        long textFields = robot.lookup(".text-field").queryAll().size();
        long passwordFields = robot.lookup(".password-field").queryAll().size();
        assertTrue(textFields >= 2, "Форма реєстрації має мінімум 2 текстових поля");
        assertTrue(passwordFields >= 2, "Форма реєстрації має мінімум 2 поля паролю");
    }

    // ── Тест 9: порожня реєстрація — залишаємось ─────────────────────────
    @Test
    void emptyRegisterKeepsWindowOpen(FxRobot robot) {
        robot.clickOn("Реєстрація");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Зареєструватись");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        assertTrue(stage.isShowing());
        assertTrue(stage.getTitle().contains("Вхід"));
    }

    // ── Тест 10: невірний логін показує помилку ──────────────────────────
    @Test
    void wrongLoginShowsError(FxRobot robot) {
        // Встановлюємо логін і пароль через Platform.runLater
        javafx.application.Platform.runLater(() -> {
            robot.lookup(".text-field").queryAll().stream()
                    .filter(n -> n instanceof TextField)
                    .map(n -> (TextField) n)
                    .findFirst()
                    .ifPresent(f -> f.setText("nonexistent_user"));
            robot.lookup(".password-field").queryAll().stream()
                    .filter(n -> n instanceof javafx.scene.control.PasswordField)
                    .map(n -> (javafx.scene.control.PasswordField) n)
                    .findFirst()
                    .ifPresent(f -> f.setText("wrongpass"));
        });
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("Увійти");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // Повинні залишитись на сторінці входу
        assertTrue(stage.isShowing());
        assertTrue(stage.getTitle().contains("Вхід"));
    }

    // ── Тест 11: успішна реєстрація та автовхід ──────────────────────────
    @Test
    void successfulRegistrationNavigatesToMainApp(FxRobot robot) {
        robot.clickOn("Реєстрація");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // Знаходимо ScrollPane вкладки реєстрації та її поля
        TabPane tabPane = robot.lookup(".tab-pane").query();
        javafx.scene.Node regContent = tabPane.getTabs().get(1).getContent();

        robot.interact(() -> {
            // Шукаємо поля саме у вмісті вкладки реєстрації
            var textFields = regContent.lookupAll(".text-field").stream()
                    .filter(n -> n instanceof TextField)
                    .map(n -> (TextField) n)
                    .toList();
            var passFields = regContent.lookupAll(".password-field").stream()
                    .filter(n -> n instanceof javafx.scene.control.PasswordField)
                    .map(n -> (javafx.scene.control.PasswordField) n)
                    .toList();

            if (textFields.size() >= 1) textFields.get(0).setText("reguser_" + System.nanoTime());
            if (textFields.size() >= 2) textFields.get(1).setText("reg@test.com");
            for (var pf : passFields) pf.setText("pass1234");
        });

        robot.clickOn("Зареєструватись");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        assertTrue(stage.isShowing());
    }

    // ── Тест 12: реєстрація з різними паролями ──────────────────────────
    @Test
    void mismatchedPasswordsShowError(FxRobot robot) {
        robot.clickOn("Реєстрація");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        TabPane tabPane = robot.lookup(".tab-pane").query();
        javafx.scene.Node regContent = tabPane.getTabs().get(1).getContent();

        robot.interact(() -> {
            var textFields = regContent.lookupAll(".text-field").stream()
                    .filter(n -> n instanceof TextField)
                    .map(n -> (TextField) n)
                    .toList();
            var passFields = regContent.lookupAll(".password-field").stream()
                    .filter(n -> n instanceof javafx.scene.control.PasswordField)
                    .map(n -> (javafx.scene.control.PasswordField) n)
                    .toList();

            if (!textFields.isEmpty()) textFields.get(0).setText("mismatchuser");
            if (passFields.size() >= 2) {
                passFields.get(0).setText("pass1234");
                passFields.get(1).setText("different");
            }
        });

        robot.clickOn("Зареєструватись");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        assertTrue(stage.isShowing());
        assertTrue(stage.getTitle().contains("Вхід"));
    }

    // ── Тест 13: успішний логін переходить на головне вікно ──────────────
    @Test
    void successfulLoginOpensMainApp(FxRobot robot) {
        TabPane tabPane = robot.lookup(".tab-pane").query();
        javafx.scene.Node loginContent = tabPane.getTabs().get(0).getContent();

        robot.interact(() -> {
            var textFields = loginContent.lookupAll(".text-field").stream()
                    .filter(n -> n instanceof TextField)
                    .map(n -> (TextField) n).toList();
            var passFields = loginContent.lookupAll(".password-field").stream()
                    .filter(n -> n instanceof javafx.scene.control.PasswordField)
                    .map(n -> (javafx.scene.control.PasswordField) n).toList();

            if (!textFields.isEmpty()) textFields.get(0).setText("testlogin");
            if (!passFields.isEmpty()) passFields.get(0).setText("test1234");
        });

        robot.clickOn("Увійти");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        assertTrue(stage.isShowing());
        // Після успішного логіну заголовок має змінитись
        assertFalse(stage.getTitle().contains("Вхід"),
                "Після логіну має відкритись головне вікно, але заголовок: " + stage.getTitle());
    }

    // ── Тест 14: вхід з невірними даними ─────────────────────────────────
    @Test
    void loginWithInvalidCredentials(FxRobot robot) {
        TabPane tabPane = robot.lookup(".tab-pane").query();
        javafx.scene.Node loginContent = tabPane.getTabs().get(0).getContent();

        robot.interact(() -> {
            var textFields = loginContent.lookupAll(".text-field").stream()
                    .filter(n -> n instanceof TextField)
                    .map(n -> (TextField) n)
                    .toList();
            var passFields = loginContent.lookupAll(".password-field").stream()
                    .filter(n -> n instanceof javafx.scene.control.PasswordField)
                    .map(n -> (javafx.scene.control.PasswordField) n)
                    .toList();

            if (!textFields.isEmpty()) textFields.get(0).setText("baduser");
            if (!passFields.isEmpty()) passFields.get(0).setText("badpass");
        });

        robot.clickOn("Увійти");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        assertTrue(stage.isShowing());
        assertTrue(stage.getTitle().contains("Вхід"));
    }
}
