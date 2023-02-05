package ru.otus.spacebattle.command;

import ru.otus.spacebattle.ioc.IoC;

import java.util.function.Function;

/**
 * Команда по обработке очереди, которая дожидается, пока очередь не станет пуста, а потом прекращает обработку
 * Т.е. в качестве следующей возвращает команду из очереди, если она существует. Если не существует, то null
 */
public class SoftStopCommand implements Command {
    @Override
    public void execute() {
        ((Command) IoC.resolve("IoC.Register","CommandQueue.NextCommand", (Function<Object[], Object>) args1 ->
            ((CommandQueue) IoC.resolve("CommandQueue")).readFirst())).execute();
    }
}
