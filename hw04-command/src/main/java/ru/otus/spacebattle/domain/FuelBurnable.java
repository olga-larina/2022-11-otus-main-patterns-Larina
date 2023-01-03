package ru.otus.spacebattle.domain;

/**
 * Интерфейс объектов, сжигающих топливо
 */
public interface FuelBurnable {

    int getFuelLevel();

    void setFuelLevel(int level);

    int getFuelBurnVelocity();
}
