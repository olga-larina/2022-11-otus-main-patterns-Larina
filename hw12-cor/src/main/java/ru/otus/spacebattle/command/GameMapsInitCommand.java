package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.GameMap;
import ru.otus.spacebattle.domain.Location;
import ru.otus.spacebattle.ioc.IoC;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Инициализация игровых полей со смещениями shifts
 * Каждое поле имеет ссылку на следующее
 */
public class GameMapsInitCommand implements Command {

    private final int[] shifts;
    private final int areaSize;
    private final Location location;

    public GameMapsInitCommand(int[] shifts, int areaSize, Location location) {
        this.shifts = shifts;
        this.areaSize = areaSize;
        this.location = location;
    }

    @Override
    public void execute() {
        // Каждое игровое поле имеет ссылку на следующее. Начинаем с конца, т.к. последнее ссылки не имеет
        AtomicReference<GameMap> gameMapNext = new AtomicReference<>();
        Arrays.stream(shifts).boxed().sorted(Collections.reverseOrder()).forEach(shift -> {
            gameMapNext.set(IoC.resolve("GameMap.Obtain", shift, areaSize, location, gameMapNext.get()));
        });
        // Регистрируем первое игровое поле (будет содержать ссылку на следующее и т.п.)
        ((Command) IoC.resolve("IoC.Register", "GameMap", (Function<Object[], Object>) args -> gameMapNext.get())).execute();
    }
}
