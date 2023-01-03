package ru.otus.spacebattle.handler;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.command.TwiceRepeatCommand;

/**
 * Стратегия обработки исключений: повторить команду 2 раза, а затем записать информацию в лог.
 */
public class RepeatTwiceThanLogExceptionHandlerStrategy implements ExceptionHandlerStrategy {

    @Override
    public String getExceptionHandler(Exception exception, Command command) {
        if (command instanceof TwiceRepeatCommand) {
            return LogExceptionHandler.class.getName();
        } else {
            return RepeatExceptionHandler.class.getName();
        }
    }
}
