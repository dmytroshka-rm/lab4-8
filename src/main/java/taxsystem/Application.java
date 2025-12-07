package taxsystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import taxsystem.ui.Menu;
import taxsystem.ui.command.*;
import taxsystem.service.TaxCalculatorService;
import taxsystem.service.TaxReportGenerator;
import taxsystem.repository.FileDataRepository;
import taxsystem.repository.DataRepository;

public class Application {

    private static final Logger log = LogManager.getLogger(Application.class);

    private Menu mainMenu;
    private TaxCalculatorService taxService;
    private TaxReportGenerator reportGenerator;
    private DataRepository repository;

    public Application() {
        log.info("=== Запуск застосунку Tax System ===");

        try {
            initializeServices();
            initializeMenu();
            log.info("Ініціалізація застосунку завершена успішно.");
        } catch (Exception e) {
            log.error("Критична помилка під час ініціалізації застосунку!", e);
            throw e; // буде відправлено на email завдяки логеру
        }
    }

    private void initializeServices() {
        log.info("Ініціалізація сервісів...");

        this.repository = new FileDataRepository();
        this.taxService = new TaxCalculatorService();
        this.reportGenerator = new TaxReportGenerator();

        log.debug("Сервіси створено: TaxCalculatorService, TaxReportGenerator, FileDataRepository");
    }

    private void initializeMenu() {
        log.info("Ініціалізація команд меню...");

        mainMenu = new Menu();

        mainMenu.addCommand("add_income", new AddIncomeCommand(taxService));
        mainMenu.addCommand("add_benefit", new AddBenefitCommand(taxService));
        mainMenu.addCommand("calculate", new CalculateTaxesCommand(taxService));
        mainMenu.addCommand("sort", new SortTaxesCommand(taxService));
        mainMenu.addCommand("find", new FindTaxesCommand(taxService));
        mainMenu.addCommand("create_person", new CreatePersonCommand(taxService));

        ExportReportCommand export = new ExportReportCommand(reportGenerator, repository);
        export.setTaxService(taxService);
        mainMenu.addCommand("export", export);

        mainMenu.addCommand("report_mode", new SetReportModeCommand(reportGenerator));

        log.info("Команди меню успішно зареєстровано. Усього команд: {}", 7);
    }

    public void start() {
        log.info("Запуск головного меню...");
        /*
        // === ТЕСТ КРИТИЧНОЇ ПОМИЛКИ ===
        try {
            throw new RuntimeException("ТЕСТОВА КРИТИЧНА ПОМИЛКА! Перевірка email логування.");
        } catch (Exception ex) {
            log.fatal("Виникла критична помилка у програмі!", ex);
        }
        */

        try {
            mainMenu.run();
        } catch (Exception e) {
            log.error("Несподівана помилка під час роботи меню!", e);
        }
        log.info("Головне меню завершило роботу.");
    }

    public static void main(String[] arguments) {
        log.info("=== Старт програми Tax System (main) ===");

        try {
            Application application = new Application();
            application.start();
            log.info("=== Програма завершилася коректно ===");
        } catch (Exception e) {
            log.error("=== Критична помилка у main() ===", e);
        }
    }
}
