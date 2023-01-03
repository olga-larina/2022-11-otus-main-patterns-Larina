package ru.otus.spacebattle.handler;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.command.RepeatCommand;

/**
 * Стратегия обработки исключений: при первом выбросе исключения повторить команду, при повторном выбросе исключения записать информацию в лог.
 */
public class RepeatThanLogExceptionHandlerStrategy implements ExceptionHandlerStrategy {

    @Override
    public String getExceptionHandler(Exception exception, Command command) {
        if (command instanceof RepeatCommand) {
            return LogExceptionHandler.class.getName();
        } else {
            return RepeatExceptionHandler.class.getName();
        }
    }
}
