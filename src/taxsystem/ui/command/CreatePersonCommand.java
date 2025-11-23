package taxsystem.ui.command;

import taxsystem.service.TaxCalculatorService;
import taxsystem.domain.Person;

import java.util.List;
import java.util.Scanner;

public class CreatePersonCommand implements Command {
    private final TaxCalculatorService taxService;

    public CreatePersonCommand(TaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    @Override
    public void execute(List<String> parameters) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Створення нової особи");
        System.out.print("Введіть ID особи (наприклад: P1): ");
        String id = scanner.nextLine().trim();

        System.out.print("Введіть ім’я: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Введіть прізвище: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Введіть ідентифікаційний номер (ІПН): ");
        String tin = scanner.nextLine().trim();

        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || tin.isEmpty()) {
            System.out.println("Усі поля обов’язкові для заповнення.");
            return;
        }

        Person newPerson = new Person(id, firstName, lastName, tin);
        taxService.setCurrentPerson(newPerson);

        System.out.println("Особу успішно створено і встановлено як поточну:");
        System.out.printf("  %s %s (ID: %s, ІПН: %s)%n", firstName, lastName, id, tin);
    }

    @Override
    public String getDescription() {
        return "Створити нову особу для розрахунку податків";
    }
}
