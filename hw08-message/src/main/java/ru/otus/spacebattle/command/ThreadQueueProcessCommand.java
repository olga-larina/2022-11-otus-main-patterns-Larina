package ru.otus.spacebattle.command;

/**
 * Команда, запускающая обработку очереди в отдельном потоке
 */
public class ThreadQueueProcessCommand implements Command {

    private final CommandQueue queue;

    public ThreadQueueProcessCommand(CommandQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute() {
        new Thread(() -> {
            new QueueProcessCommand(queue).execute();
        }).start();
    }
}