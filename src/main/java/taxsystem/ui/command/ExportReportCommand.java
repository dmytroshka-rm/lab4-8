package taxsystem.ui.command;

import taxsystem.service.TaxReportGenerator;
import taxsystem.service.TaxCalculatorService;
import taxsystem.repository.DataRepository;

import java.util.List;

public class ExportReportCommand implements Command {
    private final TaxReportGenerator reportGenerator;
    private TaxCalculatorService taxService;
    private final DataRepository repository;

    public ExportReportCommand(TaxReportGenerator reportGenerator, DataRepository repository) {
        this.reportGenerator = reportGenerator;
        this.repository = repository;
    }

    public void setTaxService(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.isEmpty()) {
            System.out.println("Використання: export <filename>");
            return;
        }

        if (taxService == null || taxService.getCurrentPerson() == null) {
            System.out.println("Спочатку створіть особу перед експортом звіту.");
            return;
        }

        taxService.recalcTaxes();
        String report = reportGenerator.generateReport(taxService.getCurrentPerson());
        String filename = parameters.get(0);
        repository.exportTaxReport(report, filename);
    }

    @Override
    public String getDescription() {
        return "Експортувати податковий звіт у файл";
    }
}
