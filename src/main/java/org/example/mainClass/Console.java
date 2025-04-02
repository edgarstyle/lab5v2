package org.example.mainClass;

import java.util.Scanner;

/**
 * класс для взаимодействия консоли
 */
public class Console {
    private final Scanner scanner;
    private final CommandManager commandManager;

    public Console(Scanner scanner, CommandManager commandManager) {
        this.scanner = scanner;
        this.commandManager = commandManager;
    }

    public void startInteractiveMode() {
        System.out.println("Программа запущена. Введите 'help' для списка команд.");
        boolean shouldExit = false;
        while (!shouldExit && scanner.hasNext()) {
            String inputCommand = scanner.nextLine();
            shouldExit = commandManager.executeCommand(inputCommand);
        }
    }
}