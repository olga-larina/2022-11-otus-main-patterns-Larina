package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.Coords;
import ru.otus.spacebattle.domain.Movable;
import ru.otus.spacebattle.exception.CommandException;

/**
 * Команда движения
 */
public class MoveCommand implements Command {
    private final Movable movable;

    public MoveCommand(Movable movable) {
        this.movable = movable;
    }

    @Override
    public void execute() {
        if (movable == null) {
            throw new CommandException("Object is null");
        }
        if (movable.getPosition() == null) {
            throw new CommandException("Can not get position");
        }
        if (movable.getVelocity() == null) {
            throw new CommandException("Can not get velocity");
        }
        movable.setPosition(Coords.plus(movable.getPosition(), movable.getVelocity()));
    }
}
