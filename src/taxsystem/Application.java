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
        mainMenu = new Menu();

        mainMenu.addCommand("add_income", new AddIncomeCommand(taxService));
        mainMenu.addCommand("add_benefit", new AddBenefitCommand(taxService));
        mainMenu.addCommand("calculate", new CalculateTaxesCommand(taxService));
        mainMenu.addCommand("sort", new SortTaxesCommand(taxService));
        mainMenu.addCommand("find", new FindTaxesCommand(taxService));
        mainMenu.addCommand("export", new ExportReportCommand(reportGenerator));
        mainMenu.addCommand("load", new LoadDataCommand(repository));

        mainMenu.addCommand("hello", new Command() {
            @Override
            public void execute(java.util.List<String> parameters) {
                System.out.println(" Вітаємо у системі розрахунку податків!");
            }

            @Override
            public String getDescription() {
                return "Виводить привітальне повідомлення";
            }
        });
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