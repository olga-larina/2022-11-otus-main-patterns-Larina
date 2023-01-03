package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.Coords;
import ru.otus.spacebattle.domain.Movable;

/**
 * Движение
 */
public class MoveCommand implements Command {
    private final Movable movable;

    public MoveCommand(Movable movable) {
        this.movable = movable;
    }

    @Override
    public void execute() {
        if (movable == null) {
            throw new IllegalStateException("Object is null");
        }
        if (movable.getPosition() == null) {
            throw new IllegalStateException("Can not get position");
        }
        if (movable.getVelocity() == null) {
            throw new IllegalStateException("Can not get velocity");
        }
        movable.setPosition(Coords.plus(movable.getPosition(), movable.getVelocity()));
    }
}
