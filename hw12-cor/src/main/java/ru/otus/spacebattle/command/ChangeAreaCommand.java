package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.Area;
import ru.otus.spacebattle.domain.AreaObject;
import ru.otus.spacebattle.domain.GameMap;
import ru.otus.spacebattle.domain.Location;
import ru.otus.spacebattle.ioc.IoC;

import java.util.Map;

/**
 * Команда возможной смены окрестности
 */
public class ChangeAreaCommand implements Command {

    private final AreaObject object;
    private final Location previousLocation;

    public ChangeAreaCommand(AreaObject object, Location previousLocation) {
        this.object = object;
        this.previousLocation = previousLocation;
    }

    @Override
    public void execute() {
        GameMap gameMap = IoC.resolve("GameMap");
        // определяем предыдущие окрестности (несколько, т.к. каждая соответствует своему игровому полю)
        Map<String, Area> previousAreas;
        if (previousLocation != null) {
            previousAreas = gameMap.getAreas(previousLocation);
        } else {
            previousAreas = null;
        }
        // определяем окрестности, в которых сейчас присутствует объект
        Map<String, Area> currentAreas = gameMap.getAreas(object.getLocation());

        // если попал в новую окрестность

        // удаляем из списка объектов старой окрестности (+ обновляются макрокоманды проверки коллизий)
        if (previousAreas != null) {
            previousAreas.forEach((key, value) -> {
                if (!currentAreas.containsKey(key)) {
                    value.removeObject(object);
                }
            });
        }
        // добавляем в список объектов новой окрестности (+ обновляются макрокоманды проверки коллизий)
        currentAreas.forEach((key, value) -> {
            if (previousAreas == null || !previousAreas.containsKey(key)) {
                value.addObject(object);
            }
        });
    }
}
