package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.UObject;

/**
 * Стоп движения
 */
public class EndMoveCommand implements Command {

    private final UObject object;

    public EndMoveCommand(UObject object) {
        this.object = object;
    }

    @Override
    public void execute() {
        object.setProperty("velocity", 0);
    }
}
