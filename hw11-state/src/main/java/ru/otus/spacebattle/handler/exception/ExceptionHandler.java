package ru.otus.spacebattle.handler.exception;

import ru.otus.spacebattle.command.Command;

/**
 * Интерфейс по обработке исключений
 */
public interface ExceptionHandler {

    /**
     * Обработать исключение
     * @param exception исключение
     * @param command команда
     */
    void handle(Exception exception, Command command);
}
