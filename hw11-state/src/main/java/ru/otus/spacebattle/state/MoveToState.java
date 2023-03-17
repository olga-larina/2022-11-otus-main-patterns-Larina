package ru.otus.spacebattle.state;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.command.CommandQueue;
import ru.otus.spacebattle.command.HardStopCommand;
import ru.otus.spacebattle.command.RunCommand;

/**
 * Состояние, в котором команды извлекаются из очереди и перенаправляются в другую очередь.
 * Такое состояние может быть полезно, если хотите разгрузить сервер перед предстоящим его выключением.
 * Обработка команды HardStop приводит к тому, что будет возвращена "нулевая ссылка" на следующее состояние, что соответствует завершению работы потока.
 * Обработка команды RunCommand приводит к тому, что будет возвращена ссылка на "обычное" состояние.
 */
public class MoveToState implements State {

    private final CommandQueue otherQueue;

    public MoveToState(CommandQueue otherQueue) {
        this.otherQueue = otherQueue;
    }

    @Override
    public State handle(CommandQueue queue) {
        Command command = queue.readFirst();
        State nextState = this;
        if (command != null) {
            // команды HardStop и Run не нужно добавлять в другую очередь
            if (command instanceof HardStopCommand) {
                nextState = null;
            } else if (command instanceof RunCommand) {
                nextState = new DefaultState();
            } else {
                otherQueue.addLast(command);
            }
        }
        return nextState;
    }
}
