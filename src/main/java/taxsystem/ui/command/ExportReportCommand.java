package taxsystem.ui.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;
import taxsystem.service.TaxReportGenerator;
import taxsystem.repository.DataRepository;

import java.util.List;

public class ExportReportCommand implements Command {
    private static final Logger log = LogManager.getLogger(ExportReportCommand.class);
    private final PersonService personService;
    private final TaxCalculatorService taxService;
    private final TaxReportGenerator reportGenerator;
    private final DataRepository repository;

    public ExportReportCommand(PersonService personService, TaxCalculatorService taxService,
                               TaxReportGenerator reportGenerator, DataRepository repository) {
        this.personService = personService;
        this.taxService = taxService;
        this.reportGenerator = reportGenerator;
        this.repository = repository;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.isEmpty()) {
            log.warn("Команда ExportReport: не вказано ім'я файлу.");
            System.out.println("Використання: export <filename>");
            return;
        }

        if (personService.getCurrentPerson() == null) {
            log.warn("Команда ExportReport: особу не задано.");
            System.out.println("Спочатку створіть особу перед експортом звіту.");
            return;
        }

        taxService.recalcTaxes(personService.getCurrentPerson());
        String report = reportGenerator.generateReport(personService.getCurrentPerson());
        String filename = parameters.get(0);
        repository.exportTaxReport(report, filename);
        log.info("Команда ExportReport: звіт збережено у файл '{}'", filename);
        System.out.println("Звіт успішно експортовано у файл: " + filename);
    }

    @Override
    public String getDescription() {
        return "Експортувати податковий звіт у файл";
    }
}
