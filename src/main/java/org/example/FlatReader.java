package org.example;
import org.example.exception.*;

import org.example.models.*;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Class for reading Flat objects from console or script
 */
public class FlatReader {
    private Scanner scanner;

    public FlatReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public Flat readFlatFromConsole() {
        System.out.println("Введите данные квартиры:");

        String name = readString("Введите название квартиры:", false);

        System.out.println("Введите координаты:");
        Float x = readFloat("Введите координату X (не null):", false);
        Integer y = readInteger("Введите координату Y (не null):", false);
        Coordinates coordinates = new Coordinates(x, y);

        Float area = readFloat("Введите площадь (должна быть > 0):", true);
        int numberOfRooms = readInteger("Введите количество комнат (1-11):", true);

        System.out.println("Доступные варианты отделки:");
        for (Furnish f : Furnish.values()) {
            System.out.println("- " + f);
        }
        Furnish furnish = readEnum("Введите отделку (не null):", Furnish.class, false);

        System.out.println("Доступные варианты вида:");
        for (View v : View.values()) {
            System.out.println("- " + v);
        }
        View view = readEnum("Введите вид (не null):", View.class, false);

        System.out.println("Доступные варианты транспорта (может быть null, оставьте пустым):");
        for (Transport t : Transport.values()) {
            System.out.println("- " + t);
        }
        Transport transport = readEnum("Введите транспорт:", Transport.class, true);

        House house = null;
        if (readBoolean("Добавить дом? (y/n)")) {
            String houseName = readString("Введите название дома (не null):", false);
            int year = readInteger("Введите год постройки (> 0):", true);
            long numberOfLifts = readLong("Введите количество лифтов (> 0):", true);
            house = new House(houseName, year, numberOfLifts);
        }

        return new Flat(name, coordinates, area, numberOfRooms, furnish, view, transport, house);
    }

    public Flat readFlatFromScript() {
        try {
            String name = scanner.nextLine();
            if (name == null || name.isEmpty()) {
                throw new FlatValidateException("Название квартиры не может быть пустым");
            }

            Float x = Float.parseFloat(scanner.nextLine());
            Integer y = Integer.parseInt(scanner.nextLine());
            Coordinates coordinates = new Coordinates(x, y);

            Float area = Float.parseFloat(scanner.nextLine());
            if (area <= 0) {
                throw new FlatValidateException("Площадь должна быть больше 0");
            }

            int numberOfRooms = Integer.parseInt(scanner.nextLine());
            if (numberOfRooms < 1 || numberOfRooms > 11) {
                throw new FlatValidateException("Количество комнат должно быть от 1 до 11");
            }

            Furnish furnish = Furnish.valueOf(scanner.nextLine().toUpperCase());
            View view = View.valueOf(scanner.nextLine().toUpperCase());

            String transportStr = scanner.nextLine();
            Transport transport = transportStr.isEmpty() ? null : Transport.valueOf(transportStr.toUpperCase());

            String houseStr = scanner.nextLine();
            House house = null;
            if (!houseStr.isEmpty()) {
                String houseName = scanner.nextLine();
                int year = Integer.parseInt(scanner.nextLine());
                long numberOfLifts = Long.parseLong(scanner.nextLine());
                house = new House(houseName, year, numberOfLifts);
            }

            return new Flat(name, coordinates, area, numberOfRooms, furnish, view, transport, house);
        } catch (Exception e) {
            throw new ReadElementFromScriptException("Ошибка при чтении элемента из скрипта", e);
        }
    }

    private String readString(String prompt, boolean canBeNull) {
        System.out.println(prompt);
        while (true) {
            String input = scanner.nextLine();
            if (!input.isEmpty() || canBeNull) {
                return input.isEmpty() ? null : input;
            }
            System.out.println("Значение не может быть пустым. " + prompt);
        }
    }

    private Float readFloat(String prompt, boolean positive) {
        System.out.println(prompt);
        while (true) {
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    throw new NumberFormatException();
                }
                float value = Float.parseFloat(input);
                if (positive && value <= 0) {
                    System.out.println("Значение должно быть больше 0. " + prompt);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Введите число. " + prompt);
            }
        }
    }

    private Integer readInteger(String prompt, boolean positive) {
        System.out.println(prompt);
        while (true) {
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    throw new NumberFormatException();
                }
                int value = Integer.parseInt(input);
                if (positive && value <= 0) {
                    System.out.println("Значение должно быть больше 0. " + prompt);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Введите целое число. " + prompt);
            }
        }
    }

    private Long readLong(String prompt, boolean positive) {
        System.out.println(prompt);
        while (true) {
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    throw new NumberFormatException();
                }
                long value = Long.parseLong(input);
                if (positive && value <= 0) {
                    System.out.println("Значение должно быть больше 0. " + prompt);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Введите целое число. " + prompt);
            }
        }
    }

    private <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass, boolean canBeNull) {
        System.out.println(prompt);
        while (true) {
            String input = scanner.nextLine();
            if (input.isEmpty() && canBeNull) {
                return null;
            }
            try {
                return Enum.valueOf(enumClass, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Некорректный ввод. Допустимые значения: " + Arrays.toString(enumClass.getEnumConstants()));
            }
        }
    }

    private boolean readBoolean(String prompt) {
        System.out.println(prompt);
        while (true) {
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Некорректный ввод. Введите y/n или yes/no");
        }
    }
}
