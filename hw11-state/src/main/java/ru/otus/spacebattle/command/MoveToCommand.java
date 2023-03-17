package ru.otus.spacebattle.command;

import ru.otus.spacebattle.ioc.IoC;

import java.util.function.Function;

/**
 * Команда, которая регистрирует в IoC очередь для копирования. Её появление в очереди означает перевод в режим обработки MoveTo
 */
public class MoveToCommand implements Command {

    private final CommandQueue otherQueue;

    public MoveToCommand(CommandQueue otherQueue) {
        this.otherQueue = otherQueue;
    }

    @Override
    public void execute() {
        ((Command) IoC.resolve("IoC.Register","CommandQueue.MoveTo", (Function<Object[], Object>) args1 -> otherQueue)).execute();
    }
}
