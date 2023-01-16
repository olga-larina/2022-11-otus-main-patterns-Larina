package ru.otus.spacebattle.handler;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.command.CommandQueue;
import ru.otus.spacebattle.exception.NoSuchExceptionHandlerException;

import java.util.HashMap;
import java.util.Map;

/**
 * Верхнеуровневый обработчик исключений, направляющий выполнение тому обработчику, который он получает согласно стратегии
 */
public class RouterExceptionHandler implements ExceptionHandler {

    private final ExceptionHandlerStrategy exceptionHandlerStrategy;
    private final Map<String, ExceptionHandler> exceptionHandlers;

    public RouterExceptionHandler(CommandQueue commandQueue, ExceptionHandlerStrategy exceptionHandlerStrategy) {
        this.exceptionHandlerStrategy = exceptionHandlerStrategy;
        this.exceptionHandlers = new HashMap<>();
        // в следующих ДЗ будет вынесено в IoC
        this.exceptionHandlers.put(LogExceptionHandler.class.getName(), new LogExceptionHandler(commandQueue));
        this.exceptionHandlers.put(RepeatExceptionHandler.class.getName(), new RepeatExceptionHandler(commandQueue));
    }

    @Override
    public void handle(Exception exception, Command command) {
        ExceptionHandler exceptionHandler = exceptionHandlers.get(exceptionHandlerStrategy.getExceptionHandler(exception, command));
        if (exceptionHandler == null) {
            throw new NoSuchExceptionHandlerException("No exception handler found");
        }
        exceptionHandler.handle(exception, command);
    }
}
