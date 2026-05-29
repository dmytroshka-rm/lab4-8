package taxsystem.ui.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.IncomeSource;

import java.util.List;
import java.util.Scanner;

public class FindTaxesCommand implements Command {
    private static final Logger log = LogManager.getLogger(FindTaxesCommand.class);
    private final PersonService personService;
    private final TaxCalculatorService taxService;

    public FindTaxesCommand(PersonService personService, TaxCalculatorService taxService) {
        this.personService = personService;
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (personService.getCurrentPerson() == null) {
            log.warn("Команда FindTaxes: особу не задано.");
            System.out.println("Спочатку створіть особу (команда create_person).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        double minTax;
        double maxTax;

        if (parameters.size() >= 2) {
            try {
                minTax = Double.parseDouble(parameters.get(0));
                maxTax = Double.parseDouble(parameters.get(1));
            } catch (NumberFormatException e) {
                log.warn("Команда FindTaxes: невірний формат параметрів: {}", parameters);
                System.out.println("Невірний формат чисел. Використовуйте, наприклад: find 1000 5000");
                return;
            }
        } else {
            System.out.println("Пошук доходів за розміром податку");
            minTax = askDouble(scanner, "Введіть мінімальну суму податку (грн): ");
            maxTax = askDouble(scanner, "Введіть максимальну суму податку (грн): ");
        }

        taxService.recalcTaxes(personService.getCurrentPerson());
        List<IncomeSource> found = taxService.findByTaxRange(personService.getCurrentPerson(), minTax, maxTax);

        System.out.printf("%nРезультати пошуку податків у діапазоні від %.2f до %.2f грн:%n", minTax, maxTax);

        log.info("Команда FindTaxes: знайдено {} доходів у діапазоні [{} - {}]", found.size(), minTax, maxTax);
        if (found.isEmpty()) {
            System.out.println("Не знайдено жодного доходу в цьому діапазоні.");
        } else {
            for (IncomeSource s : found) {
                System.out.printf("%-20s | %-30s | податок: %10.2f грн%n",
                        s.getClass().getSimpleName(), s.getDescription(), s.getTaxAmount());
            }
            System.out.println("Пошук завершено.");
        }
    }

    private double askDouble(Scanner scanner, String message) {
        double value;
        while (true) {
            System.out.print(message);
            try {
                value = Double.parseDouble(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.println("Сума не може бути від'ємною.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Введіть правильне число (наприклад: 2500)");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Знайти доходи за розміром податку (через покрокове введення)";
    }
}
