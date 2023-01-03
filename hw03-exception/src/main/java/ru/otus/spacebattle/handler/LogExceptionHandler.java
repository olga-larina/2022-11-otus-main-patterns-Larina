package ru.otus.spacebattle.handler;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.command.CommandQueue;
import ru.otus.spacebattle.command.LogCommand;

/**
 * Обработчик исключения, который ставит Команду, пишущую в лог, в очередь Команд
 */
public class LogExceptionHandler implements ExceptionHandler {

    private final CommandQueue commandQueue;

    public LogExceptionHandler(CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void handle(Exception exception, Command command) {
        commandQueue.addLast(new LogCommand(exception, command));
    }
}
