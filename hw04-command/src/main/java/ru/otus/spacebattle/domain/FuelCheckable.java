package ru.otus.spacebattle.domain;

/**
 * Интерфейс объектов, разрешающих проверку топлива
 */
public interface FuelCheckable {

    int getFuelLevel();

    int getFuelBurnVelocity();
}
