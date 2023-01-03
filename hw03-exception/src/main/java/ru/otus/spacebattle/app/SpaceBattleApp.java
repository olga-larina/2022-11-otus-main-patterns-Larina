package ru.otus.spacebattle.app;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.command.CommandQueue;
import ru.otus.spacebattle.handler.ExceptionHandler;
import ru.otus.spacebattle.handler.ExceptionHandlerStrategy;
import ru.otus.spacebattle.handler.RouterExceptionHandler;

/**
 * Класс для обработки очереди команд.
 * Все команды находятся в некоторой очереди. Обработка очереди заключается в чтении очередной команды
 * из головы очереди и вызова метода Execute извлеченной команды. Метод Execute() может выбросить любое произвольное исключение.
 */
public class SpaceBattleApp {

    private final CommandQueue commandQueue;
    private final ExceptionHandler exceptionHandler;

    public SpaceBattleApp(CommandQueue commandQueue, ExceptionHandlerStrategy exceptionHandlerStrategy) {
        this.commandQueue = commandQueue;
        this.exceptionHandler = new RouterExceptionHandler(commandQueue, exceptionHandlerStrategy);
    }

    public void process() {
        Command command;
        while ((command = commandQueue.readFirst()) != null) {
            try {
                command.execute();
            } catch (Exception exception) {
                exceptionHandler.handle(exception, command);
            }
        }
    }
}
