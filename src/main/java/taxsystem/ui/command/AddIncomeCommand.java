package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.*;
import java.util.List;
import java.util.Scanner;

public class AddIncomeCommand implements Command {
    private TaxCalculatorService taxService;

    public AddIncomeCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Додавання нового джерела доходу ===");
        System.out.print("Введіть тип доходу (employment | gift | aid): ");
        String type = scanner.nextLine().trim().toLowerCase();
        String id;
        double amount;
        String description;

        try {
            switch (type) {
                case "employment" -> {
                    System.out.print("Введіть ID джерела доходу: ");
                    id = scanner.nextLine().trim();

                    amount = askDouble(scanner, "Введіть суму доходу (грн): ");

                    System.out.print("Введіть назву роботодавця: ");
                    String employer = scanner.nextLine().trim();

                    System.out.print("Це основне місце роботи? (true/false): ");
                    boolean isMainJob = Boolean.parseBoolean(scanner.nextLine().trim());

                    System.out.print("Введіть короткий опис доходу: ");
                    description = scanner.nextLine().trim();

                    taxService.addIncome(new EmploymentIncome(id, amount, description, employer, isMainJob));
                    System.out.println("\nДохід від роботи успішно додано!");
                }

                case "gift" -> {
                    System.out.print("Введіть ID джерела доходу: ");
                    id = scanner.nextLine().trim();

                    amount = askDouble(scanner, "Введіть суму подарунку (грн): ");

                    System.out.print("Введіть ім’я дарувальника: ");
                    String donorName = scanner.nextLine().trim();

                    System.out.print("Введіть ваш родинний зв’язок із дарувальником (наприклад: батько, друг, сестра): ");
                    String relationship = scanner.nextLine().trim();

                    System.out.print("Введіть короткий опис подарунку: ");
                    description = scanner.nextLine().trim();

                    taxService.addIncome(new GiftIncome(id, amount, description, donorName, relationship));
                    System.out.println("\nПодарунок успішно додано!");
                }

                case "aid" -> {
                    System.out.print("Введіть ID джерела доходу: ");
                    id = scanner.nextLine().trim();

                    amount = askDouble(scanner, "Введіть суму допомоги (грн): ");

                    System.out.print("Введіть тип допомоги (наприклад: одноразова, соціальна): ");
                    String aidType = scanner.nextLine().trim();

                    System.out.print("Ця допомога оподатковується? (true/false): ");
                    boolean isTaxable = Boolean.parseBoolean(scanner.nextLine().trim());

                    System.out.print("Введіть короткий опис: ");
                    description = scanner.nextLine().trim();

                    taxService.addIncome(new MaterialAid(id, amount, description, aidType, isTaxable));
                    System.out.println("\nМатеріальну допомогу успішно додано!");
                }

                default -> {
                    System.out.println("Невідомий тип доходу: " + type);
                    System.out.println("Можливі варіанти: employment, gift, aid");
                    return;
                }
            }

            taxService.recalcTaxes();
            System.out.println("Податки перераховано відповідно до нового доходу.\n");

        } catch (Exception e) {
            System.out.println("Помилка при додаванні доходу: " + e.getMessage());
        }
    }

    private double askDouble(Scanner scanner, String message) {
        double value;
        while (true) {
            System.out.print(message);
            try {
                value = Double.parseDouble(scanner.nextLine().trim());
                if (value <= 0) {
                    System.out.println("Сума має бути більшою за 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Введіть правильне число (наприклад: 15000)");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Додати нове джерело доходу (через покрокове введення)";
    }
}
