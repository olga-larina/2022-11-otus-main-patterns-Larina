package ru.otus.spacebattle.command;

/**
 * Команда, запускающая обработку очереди в отдельном потоке
 */
public class ThreadQueueProcessCommand implements Command {

    @Override
    public void execute() {
        new Thread(() -> {
            new QueueProcessCommand().execute();
        }).start();
    }
}