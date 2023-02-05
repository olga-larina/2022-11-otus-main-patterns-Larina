package ru.otus.spacebattle.handler.queue;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.handler.exception.ExceptionHandler;
import ru.otus.spacebattle.ioc.IoC;

/**
 * Базовая стратегия по обработке очереди - прочитать очередную команду и выполнить, пока команда не равна null
 * Случай бесконечной обработки очереди можно реализовать, например, добавляя пустую команду в очередь (см. ContinueCommand)
 */
public class BasicQueueHandler implements QueueHandler {

    @Override
    public void handle() {
        Command command;
        while ((command = IoC.resolve("CommandQueue.NextCommand")) != null) {
            try {
                command.execute();
            } catch (Exception exception) {
                ((ExceptionHandler) IoC.resolve("Exception.Handler")).handle(exception, command);
            }
        }
    }
}
