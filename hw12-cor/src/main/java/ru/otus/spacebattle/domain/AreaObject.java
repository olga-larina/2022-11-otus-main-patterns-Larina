package ru.otus.spacebattle.domain;

public interface AreaObject {

    /**
     * Получить id
     * @return уникальный id объекта
     */
    String getId();

    /**
     * Текущее положение
     * @return текущее расположение в пространстве
     */
    Location getLocation();
}
