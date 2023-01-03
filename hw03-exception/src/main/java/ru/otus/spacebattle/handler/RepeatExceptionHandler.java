package ru.otus.spacebattle.handler;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.command.CommandQueue;
import ru.otus.spacebattle.command.RepeatCommand;
import ru.otus.spacebattle.command.TwiceRepeatCommand;

/**
 * Обработчик исключения, который ставит в очередь Команду - повторитель команды, выбросившей исключение.
 * Если команда не выполнилась дважды, то повторитель будет иметь специальный тип
 */
public class RepeatExceptionHandler implements ExceptionHandler {

    private final CommandQueue commandQueue;

    public RepeatExceptionHandler(CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void handle(Exception exception, Command command) {
        if (command instanceof RepeatCommand) {
            commandQueue.addLast(new TwiceRepeatCommand(command));
        } else {
            commandQueue.addLast(new RepeatCommand(command));
        }
    }
}
