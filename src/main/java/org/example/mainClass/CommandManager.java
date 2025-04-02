package org.example.mainClass;

import org.example.FlatReader;
import org.example.exception.*;
import org.example.models.Flat;
import org.example.models.Furnish;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * класс, который выполняет команды пользователей
 */
public class CommandManager {
    private final Set<String> scriptNames;
    private final CollectionManager collectionManager;
    private final FlatReader flatReader;
    private final FileManager fileManager;
    private final Method[] methods;
    private boolean isScriptExecuting;

    public CommandManager(FileManager fileManager, FlatReader flatReader, CollectionManager collectionManager) {
        this.fileManager = fileManager;
        this.flatReader = flatReader;
        this.collectionManager = collectionManager;
        this.methods = CommandManager.class.getMethods();
        this.isScriptExecuting = false;
        scriptNames = new HashSet<>();
    }

    public void help() {
        System.out.println("info: Выводит информацию о коллекции");
        System.out.println("show: Выводит все элементы коллекции");
        System.out.println("add: Добавляет элемент в коллекцию");
        System.out.println("update id: Обновляет значение элемента коллекции, id которого равен заданному");
        System.out.println("remove_by_id id: Удаляет элемент из коллекции по его id");
        System.out.println("clear: Очищает коллекцию");
        System.out.println("save: Сохраняет коллекцию в файл");
        System.out.println("execute_script file_name: Считывает и исполняет скрипт из указанного файла");
        System.out.println("exit: Завершает выполнение программы без сохранения в файл");
        System.out.println("add_if_max: Добавляет новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции");
        System.out.println("remove_greater: Удаляет из коллекции все элементы, превышающие заданный");
        System.out.println("history: Вывести последние 6 команд (без их аргументов)");
        System.out.println("remove_any_by_furnish furnish: Удалить из коллекции один элемент, значение поля furnish которого эквивалентно заданному");
        System.out.println("min_by_creation_date: Вывести любой объект из коллекции, значение поля creationDate которого является минимальным");
        System.out.println("print_unique_view: Вывести уникальные значения поля view всех элементов в коллекции");
    }

    public void info() {
        System.out.println("Тип коллекции - " + collectionManager.getCollectionName());
        System.out.println("Количество элементов - " + collectionManager.getSize());
        System.out.println("Дата инициализации - " + collectionManager.getCreationDate());
    }

    public void show() {
        collectionManager.getCollection().forEach(System.out::println);
    }

    public void add() {
        collectionManager.add(getFlat());
        System.out.println("Элемент успешно добавлен");
    }

    public void update(String argument) {
        try {
            long id = Long.parseLong(argument);
            if (collectionManager.existElementWithId(id)) {
                collectionManager.updateById(id, getFlat());
                System.out.println("Элемент успешно обновлён.");
            } else {
                System.out.println("Элемента с таким id не существует.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при вводе целого числа.");
        }
    }

    public void removeById(String argument) {
        try {
            long id = Long.parseLong(argument);
            if (collectionManager.existElementWithId(id)) {
                collectionManager.removeById(id);
                System.out.println("Элемент успешно удалён");
            } else {
                System.out.println("Элемента с таким id не существует.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при вводе целого числа.");
        }
    }

    public void clear() {
        collectionManager.clear();
        System.out.println("Коллекция успешно очищена.");
    }

    public void save() {
        fileManager.saveToFile(collectionManager.getCollection());
    }

    public void executeScript(String scriptName) throws FileNotFoundException {
        File file = new File(scriptName);
        if (!file.exists()) {
            throw new FileNotFoundException("Скрипта с таким именем не существует");
        }
        if (!file.canRead()) {
            throw new FileReadPermissionException("Нет прав для чтения скрипта");
        }
        if (scriptNames.contains(scriptName)) {
            throw new RecursiveScriptException("Скрипты нельзя вызывать рекурсивно");
        }

        this.isScriptExecuting = true;
        scriptNames.add(scriptName);
        Scanner scannerToScript = new Scanner(file);
        Scanner consoleScanner = flatReader.getScanner();
        flatReader.setScanner(scannerToScript);

        System.out.println("Исполнение скрипта \"" + scriptName + "\"");
        while (scannerToScript.hasNext()) {
            String inputCommand = scannerToScript.nextLine();
            System.out.println("Исполнение команды \"" + inputCommand + "\"");
            this.executeCommand(inputCommand);
        }
        System.out.println("Исполнение скрипта \"" + scriptName + "\" завершено");

        flatReader.setScanner(consoleScanner);
        scriptNames.remove(scriptName);
        this.isScriptExecuting = false;
    }

    public void addIfMax() {
        if (collectionManager.addIfMax(getFlat())) {
            System.out.println("Элемент успешно добавлен.");
        } else {
            System.out.println("Элемент не больше наибольшего элемента коллекции.");
        }
    }

    public void removeGreater() {
        collectionManager.removeGreater(getFlat());
        System.out.println("Все элементы большие данного успешно удалены");
    }

    public void history() {
        System.out.println("Последние выполненные команды:");
        collectionManager.getHistory().forEach(System.out::println);
    }

    public void removeAnyByFurnish(String argument) {
        try {
            Furnish furnish = Furnish.valueOf(argument.toUpperCase());
            if (collectionManager.removeAnyByFurnish(furnish)) {
                System.out.println("Элемент с furnish = " + furnish + " успешно удалён");
            } else {
                System.out.println("Элемент с furnish = " + furnish + " не найден");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректное значение furnish. Допустимые значения: " + Arrays.toString(Furnish.values()));
        }
    }

    public void minByCreationDate() {
        collectionManager.minByCreationDate().ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Коллекция пуста")
        );
    }

    public void printUniqueView() {
        System.out.println("Уникальные значения view:");
        collectionManager.getUniqueViews().forEach(System.out::println);
    }

    public Flat getFlat() {
        if (isScriptExecuting) {
            System.out.println("Попытка чтения элемента из скрипта");
            return flatReader.readFlatFromScript();
        } else {
            return flatReader.readFlatFromConsole();
        }
    }

    public boolean executeCommand(String inputCommand) {
        String[] inputLineDivided = inputCommand.trim().split(" ", 2);
        String command = inputCommandToJavaStyle(inputLineDivided[0].toLowerCase());

        if ("exit".equals(command)) {
            return true;
        }

        collectionManager.addToHistory(inputLineDivided[0]);

        try {
            Method methodToInvoke = null;
            for (Method method : methods) {
                if (method.getName().equals(command)) {
                    methodToInvoke = method;
                    break;
                }
            }
            if (methodToInvoke == null) {
                throw new NoSuchMethodException();
            }
            if (inputLineDivided.length == 1) {
                methodToInvoke.invoke(this);
            } else {
                methodToInvoke.invoke(this, inputLineDivided[1]);
            }
        } catch (NoSuchMethodException | IllegalArgumentException e) {
            System.out.println("Такой команды не существует");
        } catch (InvocationTargetException e) {
            if (e.getCause().getClass().equals(NoSuchElementException.class)) {
                return true;
            }
            System.out.println(e.getCause().getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String inputCommandToJavaStyle(String str) {
        StringBuilder result = new StringBuilder();
        boolean needUpperCase = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != '_') {
                if (needUpperCase) {
                    c = Character.toUpperCase(c);
                    needUpperCase = false;
                }
                result.append(c);
            } else {
                needUpperCase = true;
            }
        }
        return result.toString();
    }
}