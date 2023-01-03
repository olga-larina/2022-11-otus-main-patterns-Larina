package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.Rotatable;
import ru.otus.spacebattle.exception.CommandException;

/**
 * Команда по повороту
 */
public class RotateCommand implements Command {
    private final Rotatable rotatable;

    public RotateCommand(Rotatable rotatable) {
        this.rotatable = rotatable;
    }

    @Override
    public void execute() {
        if (rotatable == null) {
            throw new CommandException("Object is null");
        }
        if (rotatable.getDirectionsNum() == 0) {
            throw new CommandException("Directions num is zero");
        }
        rotatable.setDirection((rotatable.getDirection() + rotatable.getAngularVelocity()) % rotatable.getDirectionsNum());
    }
}
