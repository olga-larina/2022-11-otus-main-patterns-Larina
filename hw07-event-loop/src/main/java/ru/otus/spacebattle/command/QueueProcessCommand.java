package ru.otus.spacebattle.command;

import ru.otus.spacebattle.handler.queue.QueueHandler;
import ru.otus.spacebattle.ioc.IoC;

/**
 * Команда, запускающая обработку очереди
 */
public class QueueProcessCommand implements Command {

    @Override
    public void execute() {
        ((QueueHandler) IoC.resolve("Queue.Handler")).handle();
    }
}