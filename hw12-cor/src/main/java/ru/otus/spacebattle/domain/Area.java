package ru.otus.spacebattle.domain;

import java.util.Collection;

/**
 * Окрестность
 */
public interface Area {

    /**
     * Получить id
     * @return уникальный id окрестности
     */
    String getId();

    /**
     * Добавить объект
     * @param object новый объект
     */
    void addObject(AreaObject object);

    /**
     * Удалить объект
     * @param object объект
     */
    void removeObject(AreaObject object);

    /**
     * Получить список объектов
     * @return список объектов области
     */
    Collection<AreaObject> getObjects();

    /**
     * Проверить коллизии
     */
    void handleCollisions();

    /**
     * Проверить, соответствует ли расположение данной области
     * @param location расположение в пространстве
     * @return результат
     */
    boolean testLocation(Location location);

}
