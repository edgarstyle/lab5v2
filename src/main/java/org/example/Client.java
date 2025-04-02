package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.example.exception.*;
import org.example.mainClass.CollectionManager;
import org.example.mainClass.CommandManager;
import org.example.mainClass.Console;
import org.example.mainClass.FileManager;

/**
 * Main class that starts interactive mode
 */
public final class Client {
    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        try {
            String fileName ="C:\\Users\\a\\IdeaProjects\\lab5v2\\src\\main\\java\\org\\example\\f.txt" ;
            File file = new File(fileName);
            Scanner scanner = new Scanner(System.in);
            FlatReader flatReader = new FlatReader(scanner);
            FileManager fileManager = new FileManager(file);
            CollectionManager collectionManager = new CollectionManager(fileManager.readElementsFromFile());
            CommandManager commandManager = new CommandManager(fileManager, flatReader, collectionManager);

            Console console = new Console(scanner, commandManager);
            console.startInteractiveMode();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Имя файла не указано");
        } catch (FileNotFoundException | FileReadPermissionException | FlatValidateException e) {
            System.out.println(e.getMessage());
        }
    }
}
