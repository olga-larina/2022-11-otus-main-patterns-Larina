package ru.otus;

/**
 * Интерфейс объектов, поворачивающих вокруг собственной оси
 */
public interface Rotatable {

    int getDirection();

    void setDirection(int direction);

    int getAngularVelocity();

    int getDirectionsNum();

}
