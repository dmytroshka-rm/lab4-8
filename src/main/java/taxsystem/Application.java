package taxsystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import taxsystem.ui.Menu;
import taxsystem.ui.command.*;
import taxsystem.service.TaxCalculatorService;
import taxsystem.service.TaxReportGenerator;
import taxsystem.service.PersonService;
import taxsystem.repository.DatabaseManager;
import taxsystem.repository.SqliteDataRepository;
import taxsystem.repository.DataRepository;

public class Application {

    private static final Logger log = LogManager.getLogger(Application.class);

    private Menu mainMenu;
    private TaxCalculatorService taxService;
    private TaxReportGenerator reportGenerator;
    private PersonService personService;
    private DataRepository repository;
    private DatabaseManager dbManager;

    public Application() {
        log.info("=== Запуск застосунку Tax System ===");

        try {
            initializeServices();
            initializeMenu();
            log.info("Ініціалізація застосунку завершена успішно.");
        } catch (Exception e) {
            log.error("Критична помилка під час ініціалізації застосунку!", e);
            throw e;
        }
    }

    private void initializeServices() {
        log.info("Ініціалізація сервісів...");

        this.dbManager = new DatabaseManager("taxsystem.db");
        this.repository = new SqliteDataRepository(dbManager);
        this.taxService = new TaxCalculatorService();
        this.reportGenerator = new TaxReportGenerator();
        this.personService = new PersonService(repository);

        log.debug("Сервіси створено: PersonService, TaxCalculatorService, TaxReportGenerator");
    }

    private void initializeMenu() {
        log.info("Ініціалізація команд меню...");

        mainMenu = new Menu();

        mainMenu.addCommand("create_person", new CreatePersonCommand(personService));
        mainMenu.addCommand("add_income", new AddIncomeCommand(personService, taxService));
        mainMenu.addCommand("add_benefit", new AddBenefitCommand(personService));
        mainMenu.addCommand("calculate", new CalculateTaxesCommand(personService, taxService));
        mainMenu.addCommand("sort", new SortTaxesCommand(personService, taxService));
        mainMenu.addCommand("find", new FindTaxesCommand(personService, taxService));
        mainMenu.addCommand("export", new ExportReportCommand(personService, taxService, reportGenerator, repository));
        mainMenu.addCommand("report_mode", new SetReportModeCommand(reportGenerator));

        log.info("Команди меню успішно зареєстровано. Усього команд: {}", 8);
    }

    public void start() {
        log.info("Запуск головного меню...");

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
