package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.UObject;

/**
 * Старт движения
 */
public class BeginMoveCommand implements Command {

    private final UObject object;
    private final double initialVelocity;

    public BeginMoveCommand(UObject object, double initialVelocity) {
        this.object = object;
        this.initialVelocity = initialVelocity;
    }

    @Override
    public void execute() {
        object.setProperty("initialVelocity", initialVelocity);
    }
}
