package ru.otus.spacebattle.command;

import ru.otus.spacebattle.ioc.IoC;

import java.util.function.Function;

/**
 * Команда по обработке очереди, которая обрабатывает команды по очереди
 * Если очередь пуста, то возвращает пустую команду. Т.е. обработка очереди никогда не прекращается
 */
public class ContinueCommand implements Command {
    @Override
    public void execute() {
        ((Command) IoC.resolve("IoC.Register","CommandQueue.NextCommand", (Function<Object[], Object>) args1 -> {
                Command command = ((CommandQueue) IoC.resolve("CommandQueue")).readFirst();
                return (command == null) ? IoC.resolve("CommandQueue.EmptyCommand") : command;
            })).execute();
    }
}
