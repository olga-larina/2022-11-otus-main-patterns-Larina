package ru.otus.spacebattle.handler.queue;

import ru.otus.spacebattle.command.CommandQueue;

/**
 * Интерфейс по обработке очереди
 */
public interface QueueHandler {

    /**
     * Обработать очередь команд
     */
    void handle(CommandQueue queue);
}

