package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.UObject;
import ru.otus.spacebattle.ioc.IoC;

/**
 * Команда-интерпретатор.
 * Она будет выполняться из очереди GameCommand => на том же потоке => будет видеть все ThreadLocal (в т.ч., объекты, очередь)
 */
public class InterpretCommand implements Command {

    private final String gameId;
    private final String objectId;
    private final String operationId;
    private final Object[] args;

    public InterpretCommand(String gameId, String objectId, String operationId, Object[] args) {
        this.gameId = gameId;
        this.objectId = objectId;
        this.operationId = operationId;
        this.args = args;
    }

    /**
     * Получает объект
     * Проверяет допустимость операции
     * Получает команду
     * Кладёт команду в очередь игры
     */
    @Override
    public void execute() {
        UObject object = IoC.resolve(String.format("Games.%s.Objects.Get", gameId), objectId);
        if (object == null) {
            throw new IllegalArgumentException("Object not found");
        }
        boolean isAllowed = IoC.resolve(String.format("Games.%s.AllowedOperations.Get", gameId), operationId);
        if (!isAllowed) {
            throw new IllegalStateException("Operation not allowed");
        }
        Command command = IoC.resolve(operationId, object, args);
        CommandQueue commandQueue = IoC.resolve(String.format("Games.%s.CommandQueue", gameId));
        commandQueue.addLast(command);
    }

}
