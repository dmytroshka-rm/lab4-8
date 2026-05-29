package taxsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.repository.DatabaseManager;
import taxsystem.ui.LoginController;

public class MainApp extends Application {

    private static final Logger log = LogManager.getLogger(MainApp.class);

    @Override
    public void start(Stage primaryStage) {
        log.info("=== Запуск JavaFX застосунку Tax System ===");

        try {
            DatabaseManager dbManager = new DatabaseManager("taxsystem.db");
            LoginController loginController = new LoginController(primaryStage, dbManager);
            loginController.show();

            log.info("JavaFX застосунок успішно запущено.");
        } catch (Exception e) {
            log.error("Критична помилка запуску JavaFX!", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
