package ru.otus.spacebattle.message;

import ru.otus.spacebattle.command.GameCommand;
import ru.otus.spacebattle.ioc.IoC;

/**
 * Принимает входящее сообщение и конвертирует в InterpretCommand.
 * Определяет игру, которой адресовано сообщение (из рутового скоупа), создаёт InterpretCommand и ставит в очередь этой игры.
 */
public class GameEndpoint implements Endpoint {

    public void receive(Message message) {
        GameCommand game = IoC.resolve("Games.GetById", message.getGameId());
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }
        game.new AddToGameQueueCommand(IoC.resolve("InterpretCommand", message.getGameId(), message.getObjectId(), message.getOperationId(), message.getArgs())).execute();
    }
}