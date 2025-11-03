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
    }

    public void removeCommand(String commandName) {
    }

    public void run() {

    }

    public void showHelp() {

    }

    public void exit() {
        isRunning = false;
    }

    private List<String> parseInput(String input) {
        List<String> parts = new ArrayList<>();
        return parts;
    }

    public boolean isRunning() {
        return isRunning;
    }
}