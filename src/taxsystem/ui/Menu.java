package taxsystem.ui;

import taxsystem.ui.command.Command;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Menu {
    private Map<String, Command> commands;
    private Scanner scanner;
    private boolean isRunning;

    public Menu() {
        this.commands = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.isRunning = false;
    }

    public void addCommand(String commandName, Command command) {
        commands.put(commandName,command);
    }

    public void removeCommand(String commandName) {
        commands.remove(commandName);
    }

    public void run() {
        isRunning = true;
        System.out.println("=== Система розрахунку податків ===");
        System.out.println("Введіть 'help' для перегляду команд або 'exit' для виходу.");

        while (isRunning) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Вихід із програми...");
                break; // вихід із while
            }

            if (input.equalsIgnoreCase("help")) {
                showHelp();
                continue;
            }

            List<String> parts = parseInput(input);

            if (parts.isEmpty()) {
                System.out.println("Не вказано команду. Введіть 'help' для списку доступних команд.");
                continue;
            }

            String commandName = parts.get(0);
            List<String> parameters = parts.subList(1, parts.size());

            Command command = commands.get(commandName);
            if (command != null) {
                command.execute(parameters);
            } else {
                System.out.println("Невідома команда. Введіть 'help' для списку доступних.");
            }
        }
    }

    public void showHelp() {
        System.out.println("Доступні команди:");
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            System.out.println(" - " + entry.getKey() + ": " + entry.getValue().getDescription());
        }
        System.out.println(" - exit: Вийти з програми");
    }

    private List<String> parseInput(String input) {
        List<String> parts = new ArrayList<>();
        if (input == null || input.isBlank()) return parts;

        for (String part : input.split(" ")) {
            if (!part.isBlank()) {
                parts.add(part.trim());
            }
        }
        return parts;
    }
    public boolean isRunning() {
        return isRunning;
    }
}