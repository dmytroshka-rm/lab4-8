package taxsystem.ui.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.service.PersonService;
import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.*;

import java.util.List;
import java.util.Scanner;

public class AddIncomeCommand implements Command {
    private static final Logger log = LogManager.getLogger(AddIncomeCommand.class);
    private final PersonService personService;
    private final TaxCalculatorService taxService;

    public AddIncomeCommand(PersonService personService, TaxCalculatorService taxService) {
        this.personService = personService;
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (personService.getCurrentPerson() == null) {
            log.warn("Команда AddIncome: особу не задано.");
            System.out.println("Спочатку створіть особу (команда create_person).");
            return;
        }

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

                    personService.addIncome(new EmploymentIncome(id, amount, description, employer, isMainJob));
                    System.out.println("\nДохід від роботи успішно додано!");
                }

                case "gift" -> {
                    System.out.print("Введіть ID джерела доходу: ");
                    id = scanner.nextLine().trim();

                    amount = askDouble(scanner, "Введіть суму подарунку (грн): ");

                    System.out.print("Введіть ім'я дарувальника: ");
                    String donorName = scanner.nextLine().trim();

                    System.out.print("Введіть ваш родинний зв'язок із дарувальником (наприклад: батько, друг, сестра): ");
                    String relationship = scanner.nextLine().trim();

                    System.out.print("Введіть короткий опис подарунку: ");
                    description = scanner.nextLine().trim();

                    personService.addIncome(new GiftIncome(id, amount, description, donorName, relationship));
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

                    personService.addIncome(new MaterialAid(id, amount, description, aidType, isTaxable));
                    System.out.println("\nМатеріальну допомогу успішно додано!");
                }

                case "royalty" -> {
                    System.out.print("Введіть ID джерела доходу: ");
                    id = scanner.nextLine().trim();
                    amount = askDouble(scanner, "Введіть суму винагороди (грн): ");
                    System.out.print("Введіть назву твору: ");
                    String workTitle = scanner.nextLine().trim();
                    System.out.print("Введіть тип твору (книга, стаття, програма): ");
                    String workType = scanner.nextLine().trim();
                    System.out.print("Введіть короткий опис: ");
                    description = scanner.nextLine().trim();
                    personService.addIncome(new RoyaltyIncome(id, amount, description, workTitle, workType));
                    System.out.println("\nАвторську винагороду успішно додано!");
                }

                case "sale" -> {
                    System.out.print("Введіть ID джерела доходу: ");
                    id = scanner.nextLine().trim();
                    amount = askDouble(scanner, "Введіть суму продажу (грн): ");
                    System.out.print("Введіть тип майна (квартира, авто, земля): ");
                    String propType = scanner.nextLine().trim();
                    System.out.print("Це перший продаж за рік? (true/false): ");
                    boolean firstSale = Boolean.parseBoolean(scanner.nextLine().trim());
                    System.out.print("Введіть короткий опис: ");
                    description = scanner.nextLine().trim();
                    personService.addIncome(new PropertySaleIncome(id, amount, description, propType, firstSale));
                    System.out.println("\nПродаж майна успішно додано!");
                }

                case "transfer" -> {
                    System.out.print("Введіть ID джерела доходу: ");
                    id = scanner.nextLine().trim();
                    amount = askDouble(scanner, "Введіть суму переказу (грн): ");
                    System.out.print("Введіть країну: ");
                    String country = scanner.nextLine().trim();
                    System.out.print("Введіть валюту (EUR, USD): ");
                    String currency = scanner.nextLine().trim();
                    System.out.print("Введіть короткий опис: ");
                    description = scanner.nextLine().trim();
                    personService.addIncome(new ForeignTransferIncome(id, amount, description, country, currency));
                    System.out.println("\nПереказ з-за кордону успішно додано!");
                }

                default -> {
                    System.out.println("Невідомий тип доходу: " + type);
                    System.out.println("Можливі варіанти: employment, gift, aid, royalty, sale, transfer");
                    return;
                }
            }

            taxService.recalcTaxes(personService.getCurrentPerson());
            System.out.println("Податки перераховано відповідно до нового доходу.\n");

        } catch (Exception e) {
            log.error("Помилка при додаванні доходу", e);
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
