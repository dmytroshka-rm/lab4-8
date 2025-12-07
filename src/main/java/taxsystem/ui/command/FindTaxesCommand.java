package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.IncomeSource;

import java.util.List;
import java.util.Scanner;

public class FindTaxesCommand implements Command {
    private final TaxCalculatorService taxService;

    public FindTaxesCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        Scanner scanner = new Scanner(System.in);

        double minTax;
        double maxTax;

        if (parameters.size() >= 2) {
            try {
                minTax = Double.parseDouble(parameters.get(0));
                maxTax = Double.parseDouble(parameters.get(1));
            } catch (NumberFormatException e) {
                System.out.println("Невірний формат чисел. Використовуйте, наприклад: find 1000 5000");
                return;
            }
        } else {
            System.out.println("Пошук доходів за розміром податку");
            minTax = askDouble(scanner, "Введіть мінімальну суму податку (грн): ");
            maxTax = askDouble(scanner, "Введіть максимальну суму податку (грн): ");
        }

        if (taxService.getCurrentPerson() == null) {
            System.out.println("Спочатку створіть особу.");
            return;
        }

        taxService.recalcTaxes();

        System.out.printf("%n Результати пошуку податків у діапазоні від %.2f до %.2f грн:%n", minTax, maxTax);

        List<IncomeSource> sources = taxService.getCurrentPerson().getIncomeSources();

        boolean found = false;
        for (IncomeSource s : sources) {
            double tax = s.getTaxAmount();
            if (tax >= minTax && tax <= maxTax) {
                System.out.printf("%-15s | %-30s | податок: %8.2f грн%n",
                        s.getClass().getSimpleName(),
                        s.getDescription(),
                        tax);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Не знайдено жодного доходу в цьому діапазоні.");
        } else {
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
                    System.out.println("Сума не може бути від’ємною.");
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
