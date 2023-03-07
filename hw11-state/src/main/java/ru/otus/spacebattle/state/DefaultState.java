package ru.otus.spacebattle.state;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.command.CommandQueue;
import ru.otus.spacebattle.command.HardStopCommand;
import ru.otus.spacebattle.command.MoveToCommand;
import ru.otus.spacebattle.handler.exception.ExceptionHandler;
import ru.otus.spacebattle.ioc.IoC;

/**
 * Обычное состояние режима обработки очереди.
 * Очередная команда извлекается из очереди и выполняется. По умолчанию возвращается ссылка на этот же экземпляр состояния (даже если команды нет).
 * Обработка команды HardStop приводит к тому, что будет возвращена "нулевая ссылка" на следующее состояние, что соответствует завершению работы потока.
 * Обработка команды MoveToCommand приводит к тому, что будет возвращена ссылка на состояние MoveTo.
 */
public class DefaultState implements State {

    @Override
    public State handle(CommandQueue queue) {
        Command command = queue.readFirst();
        State nextState = this;
        if (command != null) {
            try {
                // все команды (в т.ч., HardStop и MoveTo) нужно сначала выполнить
                command.execute();
                if (command instanceof HardStopCommand) {
                    nextState = null;
                } else if (command instanceof MoveToCommand) {
                    nextState = new MoveToState(IoC.resolve("CommandQueue.MoveTo"));
                }
            } catch (Exception exception) {
                ((ExceptionHandler) IoC.resolve("Exception.Handler")).handle(exception, command);
            }
        }
        return nextState;
    }
}
