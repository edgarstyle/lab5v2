package org.example.models;



/**
 * Дом квартиры
 */
public class House {
    private String name; //Поле не может быть null
    private int year; //Значение поля должно быть больше 0
    private long numberOfLifts; //Значение поля должно быть больше 0

    public House(String name, int year, long numberOfLifts) {
        this.name = name;
        this.year = year;
        this.numberOfLifts = numberOfLifts;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public long getNumberOfLifts() {
        return numberOfLifts;
    }

    @Override
    public String toString() {
        return "{name=\"" + name + "\", year=" + year + ", numberOfLifts=" + numberOfLifts + "}";
    }
}