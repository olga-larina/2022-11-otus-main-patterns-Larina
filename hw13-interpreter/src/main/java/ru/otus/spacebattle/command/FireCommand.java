package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.UObject;

/**
 * Выстрел
 */
public class FireCommand implements Command {

    private final UObject object;
    private final int fireDirection;

    public FireCommand(UObject object, int fireDirection) {
        this.object = object;
        this.fireDirection = fireDirection;
    }

    @Override
    public void execute() {
        object.setProperty("fireDirection", fireDirection);
    }
}
