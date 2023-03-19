package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.AreaObject;

/**
 * Команда проверки коллизии двух объектов
 */
public class CheckCollisionCommand implements Command {

    public CheckCollisionCommand(AreaObject object1, AreaObject object2) {

    }

    @Override
    public void execute() {
        // без реализации
    }
}
