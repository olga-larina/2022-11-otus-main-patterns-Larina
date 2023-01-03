package ru.otus.spacebattle.command;

import java.util.ArrayList;

/**
 * Макрокоманда длительного движения по прямой (движение + повторитель, который ставит макрокоманду длительного движения в очередь)
 */
public class LongMoveCommand extends MacroCommand {

    public LongMoveCommand(MoveCommand moveCommand, CommandQueue commandQueue) {
        super(new ArrayList<>());
        commands.add(moveCommand);
        commands.add(new QueueAddCommand(this, commandQueue));
    }

}
