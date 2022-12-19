package ru.otus;

/**
 * Интерфейс объектов, движущихся прямолинейно равномерно без деформации
 */
public interface Movable {

    Coords getPosition();

    Coords getVelocity();

    void setPosition(Coords newValue);

}
