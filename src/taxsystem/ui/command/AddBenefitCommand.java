package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.TaxBenefit;
import taxsystem.domain.ChildBenefit;

import java.util.List;
import java.util.Scanner;

public class AddBenefitCommand implements Command {
    private TaxCalculatorService taxService;

    public AddBenefitCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Додавання податкової пільги");
        System.out.print("Введіть тип пільги (наприклад: child): ");
        String type = scanner.nextLine().trim().toLowerCase();

        if (!type.equals("child")) {
            System.out.println("Невідома пільга: " + type);
            System.out.println("Наразі підтримується тільки: child (пільга на дітей)");
            return;
        }

        System.out.print("Введіть ID пільги: ");
        String benefitId = scanner.nextLine().trim();

        double amount = 0;
        while (true) {
            System.out.print("Введіть суму на одну дитину (грн): ");
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
                if (amount <= 0) {
                    System.out.println("Сума має бути більшою за 0.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Введіть правильне число (наприклад: 1500)");
            }
        }

        int childCount = 0;
        while (true) {
            System.out.print("Введіть кількість дітей: ");
            try {
                childCount = Integer.parseInt(scanner.nextLine().trim());
                if (childCount <= 0) {
                    System.out.println("Кількість дітей має бути більшою за 0.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Введіть ціле число (наприклад: 2)");
            }
        }

        System.out.print("Введіть короткий опис пільги: ");
        String description = scanner.nextLine().trim();

        TaxBenefit benefit = new ChildBenefit(benefitId, amount, description, childCount);
        taxService.addBenefit(benefit);

        System.out.println("\nПільгу успішно додано!");
        System.out.println("Тип: " + type);
        System.out.println("ID: " + benefitId);
        System.out.println("Сума на одну дитину: " + amount + " грн");
        System.out.println("Кількість дітей: " + childCount);
        System.out.println("Опис: " + description);
    }

    @Override
    public String getDescription() {
        return "Додати нову податкову пільгу (через покрокове введення)";
    }
}
