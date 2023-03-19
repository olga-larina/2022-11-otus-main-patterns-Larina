package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.GameMap;
import ru.otus.spacebattle.ioc.IoC;

/**
 * Команда проверки всех коллизий
 */
public class CheckAllCollisionsCommand implements Command {

    public CheckAllCollisionsCommand() {
    }

    @Override
    public void execute() {
        // Получаем первое игровое поле
        GameMap gameMap = IoC.resolve("GameMap");
        // Проверяем коллизии (будут проверяться по цепочке по областям и других полям со смещениями)
        gameMap.handleCollisions();
    }
}
