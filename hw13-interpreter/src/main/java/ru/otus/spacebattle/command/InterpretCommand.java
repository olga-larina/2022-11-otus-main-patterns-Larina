package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.UObject;
import ru.otus.spacebattle.ioc.IoC;

/**
 * Команда-интерпретатор.
 */
public class InterpretCommand implements Command {

    private final String gameId;
    private final UObject order;

    public InterpretCommand(String gameId, UObject order) {
        this.gameId = gameId;
        this.order = order;
    }

    /**
     * Вызывает интерпретацию приказа
     * Выполняет полученную команду
     */
    @Override
    public void execute() {
        Command command = IoC.resolve("Interpreter.Command.Execute", gameId, order);
        command.execute();
    }

}
