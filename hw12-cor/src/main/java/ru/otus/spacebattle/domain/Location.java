package ru.otus.spacebattle.domain;

/**
 * Расположение в пространстве
 */
public interface Location {

    /**
     * Проверить, пересекаются ли 2 расположения в пространстве
     * @param location другое расположение
     * @return результат проверки
     */
    boolean testIntersection(Location location);
}
