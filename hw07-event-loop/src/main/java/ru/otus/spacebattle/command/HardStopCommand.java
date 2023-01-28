package ru.otus.spacebattle.command;

import ru.otus.spacebattle.ioc.IoC;

import java.util.function.Function;

/**
 * Команда по обработке очереди, которая прерывает обработку команд из очереди
 * Т.е. в качестве следующей возвращает null
 */
public class HardStopCommand implements Command {
    @Override
    public void execute() {
        ((Command) IoC.resolve("IoC.Register","CommandQueue.NextCommand", (Function<Object[], Object>) args1 -> (Command) null)).execute();
    }
}
