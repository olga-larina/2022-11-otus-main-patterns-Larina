package ru.otus.spacebattle.handler;

import ru.otus.spacebattle.command.Command;

/**
 * Стратегии выбора подходящего обработчика исключения на основе экземпляра перехваченного исключения и команды, которая выбросила исключение
 */
public interface ExceptionHandlerStrategy {

    /**
     * Получить обработчик исключения на основании исключения и команды
     * @param exception исключение
     * @param command команда
     * @return класс обработчика исключения
     */
    String getExceptionHandler(Exception exception, Command command);
}
