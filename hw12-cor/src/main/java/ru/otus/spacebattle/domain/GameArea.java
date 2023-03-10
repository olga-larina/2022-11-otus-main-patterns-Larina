package ru.otus.spacebattle.domain;

import ru.otus.spacebattle.command.CheckCollisionCommand;
import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.command.MacroCommand;
import ru.otus.spacebattle.ioc.IoC;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Окрестность игрового поля
 */
public class GameArea implements Area {

    private final String id;
    private final Location location;
    private final Map<String, AreaObject> objects;
    private volatile Command checkCollisionsCommand = () -> {};

    public GameArea(String id, Location location) {
        this.id = id;
        this.location = location;
        this.objects = new ConcurrentHashMap<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void addObject(AreaObject object) {
        objects.put(object.getId(), object);
        updateCheckCollisionCommand();
    }

    @Override
    public void removeObject(AreaObject object) {
        objects.remove(object.getId());
        updateCheckCollisionCommand();
    }

    @Override
    public Collection<AreaObject> getObjects() {
        return Collections.unmodifiableCollection(objects.values());
    }

    @Override
    public void handleCollisions() {
        checkCollisionsCommand.execute();
    }

    @Override
    public boolean testLocation(Location location) {
        return this.location.testIntersection(location);
    }

    private void updateCheckCollisionCommand() {
        // для каждого объекта окрестности создает команду проверки коллизии этих двух объектов, помещает в макрокоманду
        List<Command> checkCollisionsCommands = new ArrayList<>();
        List<AreaObject> objectsList = objects.values().stream().sorted(Comparator.comparing(AreaObject::getId)).collect(Collectors.toList());
        for (int i = 0; i < objectsList.size(); i++) {
            for (int j = i + 1; j < objectsList.size(); j++) {
                Command command = IoC.resolve("CheckCollisionCommand", objectsList.get(i), objectsList.get(j));
                checkCollisionsCommands.add(command);
            }
        }
        // записывает макрокоманду проверки коллизий объектов
        this.checkCollisionsCommand = new MacroCommand(checkCollisionsCommands);
    }
}
