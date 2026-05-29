package taxsystem.ui.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxsystem.service.PersonService;

import java.util.List;
import java.util.Scanner;

public class CreatePersonCommand implements Command {
    private static final Logger log = LogManager.getLogger(CreatePersonCommand.class);
    private final PersonService personService;

    public CreatePersonCommand(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public void execute(List<String> parameters) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Створення нової особи");

        System.out.print("Введіть ім'я: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Введіть прізвище: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Введіть ідентифікаційний номер (ІПН): ");
        String tin = scanner.nextLine().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || tin.isEmpty()) {
            log.warn("Спроба створити особу з порожніми полями.");
            System.out.println("Усі поля обов'язкові для заповнення.");
            return;
        }

        var person = personService.createPerson(firstName, lastName, tin);
        log.info("Команда CreatePerson виконана: {} {}", firstName, lastName);

        System.out.println("Особу успішно створено і встановлено як поточну:");
        System.out.printf("  %s %s (ІПН: %s)%n", firstName, lastName, tin);
    }

    @Override
    public String getDescription() {
        return "Створити нову особу для розрахунку податків";
    }
}
