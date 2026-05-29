package taxsystem.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.domain.User;
import taxsystem.repository.DatabaseManager;
import taxsystem.repository.DataRepository;
import taxsystem.repository.SqliteDataRepository;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;
import taxsystem.service.TaxReportGenerator;
import taxsystem.service.UserService;

public class LoginController {

    private static final Logger log = LogManager.getLogger(LoginController.class);

    private final Stage stage;
    private final DatabaseManager dbManager;
    private final UserService userService;

    public LoginController(Stage stage, DatabaseManager dbManager) {
        this.stage = stage;
        this.dbManager = dbManager;
        this.userService = new UserService(dbManager);
    }

    public void show() {
        stage.setTitle("Tax System — Вхід");
        stage.setResizable(false);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("login-tabs");

        Tab loginTab    = new Tab("Вхід",        wrapTabContent(buildLoginPane()));
        Tab registerTab = new Tab("Реєстрація",  wrapTabContent(buildRegisterPane()));

        tabPane.getTabs().addAll(loginTab, registerTab);

        VBox root = new VBox(12);
        root.getStyleClass().add("login-root");
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Система обліку податків");
        title.getStyleClass().add("login-title");

        root.getChildren().addAll(title, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 420, 480);
        UiStyles.applyAppTheme(scene);
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(440);
        stage.setResizable(true);
        stage.show();
        log.info("Показано вікно входу/реєстрації.");
    }

    /** Щоб форма не обрізалась у вкладці при малій висоті вікна — прокрутка + прозорий фон. */
    private ScrollPane wrapTabContent(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    // ─────────────────────────────────────────────────────────────
    //  Панель «Вхід»
    // ─────────────────────────────────────────────────────────────

    private VBox buildLoginPane() {
        VBox pane = new VBox(12);
        pane.getStyleClass().add("login-card");
        pane.setPadding(new Insets(16));
        pane.setAlignment(Pos.CENTER_LEFT);

        Label loginError = new Label();
        loginError.setTextFill(Color.web("#dc2626"));
        loginError.setWrapText(true);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Логін");
        usernameField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");
        passwordField.setPrefWidth(300);

        Button loginBtn = new Button("Увійти");
        loginBtn.setPrefWidth(300);
        UiStyles.styleButton(loginBtn, UiStyles.ButtonVariant.PRIMARY);

        loginBtn.setOnAction(e -> {
            loginError.setText("");
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                loginError.setText("Заповніть всі поля.");
                return;
            }

            User user = userService.login(username, password);
            if (user == null) {
                loginError.setText("Невірний логін або пароль.");
                log.warn("Невдала спроба входу: {}", username);
            } else {
                openMainApp(user);
            }
        });

        // Enter у полі пароля — також логін
        passwordField.setOnAction(e -> loginBtn.fire());

        Label uLbl = new Label("Логін:");
        uLbl.getStyleClass().add("form-label");
        Label pLbl = new Label("Пароль:");
        pLbl.getStyleClass().add("form-label");
        pane.getChildren().addAll(
                uLbl, usernameField,
                pLbl, passwordField,
                loginBtn, loginError
        );
        return pane;
    }

    // ─────────────────────────────────────────────────────────────
    //  Панель «Реєстрація»
    // ─────────────────────────────────────────────────────────────

    private VBox buildRegisterPane() {
        VBox pane = new VBox(10);
        pane.getStyleClass().add("login-card");
        pane.setPadding(new Insets(16));
        pane.setAlignment(Pos.CENTER_LEFT);

        Label registerMsg = new Label();
        registerMsg.setWrapText(true);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Логін");
        usernameField.setPrefWidth(300);

        TextField emailField = new TextField();
        emailField.setPromptText("Email (необов'язково)");
        emailField.setPrefWidth(300);

        PasswordField passwordField  = new PasswordField();
        passwordField.setPromptText("Пароль (мін. 4 символи)");
        passwordField.setPrefWidth(300);

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Підтвердіть пароль");
        confirmField.setPrefWidth(300);

        Button registerBtn = new Button("Зареєструватись");
        registerBtn.setPrefWidth(300);
        UiStyles.styleButton(registerBtn, UiStyles.ButtonVariant.SUCCESS);

        registerBtn.setOnAction(e -> {
            registerMsg.setTextFill(Color.RED);
            registerMsg.setText("");

            String username = usernameField.getText().trim();
            String email    = emailField.getText().trim();
            String password = passwordField.getText();
            String confirm  = confirmField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                registerMsg.setText("Логін і пароль обов'язкові.");
                return;
            }
            if (!password.equals(confirm)) {
                registerMsg.setText("Паролі не збігаються.");
                return;
            }

            String error = userService.register(username, password, email);
            if (error != null) {
                registerMsg.setText(error);
            } else {
                // Автоматичний вхід після реєстрації
                registerMsg.setTextFill(Color.web("#27ae60"));
                registerMsg.setText("Реєстрація успішна! Вхід...");
                User user = userService.login(username, password);
                if (user != null) openMainApp(user);
            }
        });

        Label l1 = new Label("Логін:");
        Label l2 = new Label("Email:");
        Label l3 = new Label("Пароль:");
        Label l4 = new Label("Підтвердити пароль:");
        for (Label l : new Label[]{l1, l2, l3, l4}) {
            l.getStyleClass().add("form-label");
        }
        pane.getChildren().addAll(
                l1, usernameField,
                l2, emailField,
                l3, passwordField,
                l4, confirmField,
                registerBtn, registerMsg
        );
        return pane;
    }

    // ─────────────────────────────────────────────────────────────
    //  Перехід до головного вікна
    // ─────────────────────────────────────────────────────────────

    private void openMainApp(User user) {
        log.info("Перехід до головного вікна для користувача: {}", user.getUsername());

        DataRepository repository = new SqliteDataRepository(dbManager);
        TaxCalculatorService taxService = new TaxCalculatorService();
        TaxReportGenerator reportGenerator = new TaxReportGenerator();
        PersonService personService = new PersonService(repository);

        MainController mainController = new MainController(
                stage, personService, taxService, reportGenerator, repository
        );
        mainController.setCurrentUser(user);
        mainController.show();
    }
}
