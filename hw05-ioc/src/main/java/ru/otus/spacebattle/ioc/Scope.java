package ru.otus.spacebattle.ioc;

import java.util.function.Function;

/**
 * Скоуп
 */
interface Scope {

    /**
     * Получение зависимости
     */
    Object resolve(String key, Object... args);

    /**
     * Добавление зависимости
     */
     boolean addDependency(String key, Function<Object[], Object> strategy);
}
