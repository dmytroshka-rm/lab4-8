package taxsystem.ui;

import taxsystem.ui.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Menu {

    private static final Logger log = LogManager.getLogger(Menu.class);

    private Map<String, Command> commands;
    private Scanner scanner;
    private boolean isRunning;

    public Menu() {
        this.commands = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.isRunning = false;

        log.info("Меню ініціалізовано. Команд зареєстровано: 0 (будуть додані у Application).");
    }

    public void addCommand(String commandName, Command command) {
        commands.put(commandName, command);
        log.debug("Додано команду '{}'", commandName);
    }

    public void run() {
        isRunning = true;
        log.info("Запуск головного меню системи розрахунку податків");

        System.out.println("Система розрахунку податків");
        System.out.println("Введіть 'help' для перегляду команд або 'exit' для виходу.");

        while (isRunning) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                log.debug("Порожній ввід — пропуск");
                continue;
            }

            log.info("Отримано команду від користувача: '{}'", input);

            if (input.equalsIgnoreCase("exit")) {
                log.info("Користувач завершив роботу програми командою 'exit'");
                System.out.println("Вихід із програми...");
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                log.debug("Виклик 'help'");
                showHelp();
                continue;
            }

            List<String> parts = parseInput(input);

            if (parts.isEmpty()) {
                log.warn("Користувач ввів команду без назви: '{}'", input);
                System.out.println("Не вказано команду. Введіть 'help' для списку доступних команд.");
                continue;
            }

            String commandName = parts.get(0);
            List<String> parameters = parts.subList(1, parts.size());

            Command command = commands.get(commandName);

            if (command != null) {
                try {
                    log.info("Виконання команди '{}'", commandName);
                    command.execute(parameters);
                } catch (Exception e) {
                    log.error("Помилка під час виконання команди '{}'", commandName, e);
                    System.out.println("Сталася помилка під час виконання команди. Деталі — у логах.");
                }
            } else {
                log.warn("Невідома команда: '{}'", commandName);
                System.out.println("Невідома команда. Введіть 'help' для списку доступних.");
            }
        }

        log.info("Завершення роботи меню.");
    }

    public void showHelp() {
        System.out.println("Доступні команди:");
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            System.out.println(" - " + entry.getKey() + ": " + entry.getValue().getDescription());
        }
        System.out.println(" - exit: Вийти з програми");

        log.debug("Користувач переглянув список команд.");
    }

    private List<String> parseInput(String input) {
        List<String> parts = new ArrayList<>();
        if (input == null || input.isBlank()) {
            log.debug("parseInput отримав порожній ввід.");
            return parts;
        }

        for (String part : input.split(" ")) {
            if (!part.isBlank()) {
                parts.add(part.trim());
            }
        }

        log.trace("Розібрано команду на частини: {}", parts);
        return parts;
    }
}
