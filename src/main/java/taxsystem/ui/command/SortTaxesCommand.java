package taxsystem.ui.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.IncomeSource;

import java.util.List;

public class SortTaxesCommand implements Command {
    private static final Logger log = LogManager.getLogger(SortTaxesCommand.class);
    private final PersonService personService;
    private final TaxCalculatorService taxService;

    public SortTaxesCommand(PersonService personService, TaxCalculatorService taxService) {
        this.personService = personService;
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (personService.getCurrentPerson() == null) {
            log.warn("Команда SortTaxes: особу не задано.");
            System.out.println("Спочатку створіть особу (команда create_person).");
            return;
        }

        String dir = parameters.isEmpty() ? "asc" : parameters.get(0).toLowerCase();
        boolean ascending = !"desc".equals(dir);

        taxService.recalcTaxes(personService.getCurrentPerson());
        List<IncomeSource> sorted = taxService.sortByTax(personService.getCurrentPerson(), ascending);

        if (sorted.isEmpty()) {
            log.info("Команда SortTaxes: немає доходів для сортування.");
            System.out.println("Немає джерел доходу для сортування.");
            return;
        }

        log.info("Команда SortTaxes: відсортовано {} доходів ({})", sorted.size(), ascending ? "asc" : "desc");
        System.out.println("Доходи відсортовані за податком (" + (ascending ? "зростання" : "спадання") + "):");
        for (IncomeSource s : sorted) {
            System.out.printf("%-20s | %-30s | сума: %10.2f | податок: %10.2f%n",
                    s.getClass().getSimpleName(), s.getDescription(), s.getAmount(), s.getTaxAmount());
        }
    }

    @Override
    public String getDescription() {
        return "Відсортувати податки за сумою (asc/desc)";
    }
}
