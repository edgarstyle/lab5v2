package org.example.models;


/**
 * Координаты квартиры
 */
public class Coordinates {
    private Float x; //Поле не может быть null
    private Integer y; //Поле не может быть null

    public Coordinates(Float x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Float getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + "}";
    }
}