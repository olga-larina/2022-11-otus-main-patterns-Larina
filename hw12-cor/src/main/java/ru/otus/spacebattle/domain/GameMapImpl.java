package ru.otus.spacebattle.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Игровое поле
 */
public class GameMapImpl implements GameMap {

    private final List<Area> areas;
    private final GameMap next;

    public GameMapImpl(List<Area> areas, GameMap next) {
        this.areas = areas;
        this.next = next;
    }

    @Override
    public Map<String, Area> getAreas(Location location) {
        // поиск соответствующей области по координатам
        Map<String, Area> areaMap = new HashMap<>();
        areas.forEach(area -> {
            if (area.testLocation(location)) {
                areaMap.put(area.getId(), area);
            }
        });
        if (next != null) {
            areaMap.putAll(next.getAreas(location));
        }
        return areaMap;
    }

    @Override
    public void handleCollisions() {
        areas.forEach(Area::handleCollisions);
        if (next != null) {
            next.handleCollisions();
        }
    }
}
