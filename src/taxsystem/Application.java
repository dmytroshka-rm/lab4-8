package taxsystem;

import taxsystem.ui.Menu;
import taxsystem.ui.command.*;
import taxsystem.service.TaxCalculatorService;
import taxsystem.service.TaxReportGenerator;
import taxsystem.repository.FileDataRepository;
import taxsystem.repository.DataRepository;

public class Application {
    private Menu mainMenu;
    private TaxCalculatorService taxService;
    private TaxReportGenerator reportGenerator;
    private DataRepository repository;

    public Application() {
        initializeServices();
        initializeMenu();
    }

    private void initializeServices() {
        this.repository = new FileDataRepository();
        this.taxService = new TaxCalculatorService();
        this.reportGenerator = new TaxReportGenerator();
    }

    private void initializeMenu() {

    }

    public void start() {
        mainMenu.run();
    }

    public static void main(String[] arguments) {
        System.out.println("Запуск системи розрахунку податків...");
        Application application = new Application();
        application.start();
    }
}