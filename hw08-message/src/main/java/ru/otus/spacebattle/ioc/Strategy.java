package ru.otus.spacebattle.ioc;

/**
 * Стратегия разрешения зависимостей
 */
interface Strategy {

    Object resolve(String key, Object... args);

}
