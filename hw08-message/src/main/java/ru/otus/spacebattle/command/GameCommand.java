package ru.otus.spacebattle.command;

import ru.otus.spacebattle.ioc.IoC;

/**
 * Команда игры со своей очередью и объектами
 */
public class GameCommand implements Command {

    private final String id;

    public GameCommand(String id) {
        this.id = id;
    }

    @Override
    public void execute() {
        new QueueProcessCommand(queue()).execute();
    }

    private CommandQueue queue() {
        return IoC.resolve(String.format("Games.%s.CommandQueue", id));
    }

    /**
     * Команда по добавлению команды в очередь игры
     */
    public class AddToGameQueueCommand implements Command {

        private final Command command;

        public AddToGameQueueCommand(Command command) {
            this.command = command;
        }

        @Override
        public void execute() {
            GameCommand.this.queue().addLast(command);
        }
    }
}
