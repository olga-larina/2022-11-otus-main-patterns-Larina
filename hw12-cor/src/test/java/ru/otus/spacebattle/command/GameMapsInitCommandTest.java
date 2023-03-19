package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.domain.GameArea;
import ru.otus.spacebattle.domain.GameMap;
import ru.otus.spacebattle.domain.GameMapImpl;
import ru.otus.spacebattle.domain.Location;
import ru.otus.spacebattle.ioc.IoC;
import ru.otus.spacebattle.ioc.ScopeBasedStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@DisplayName("Команда инициализации игрового поля должна")
class GameMapsInitCommandTest {

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();
    }

    @DisplayName("Создавать и регистрировать игровые поля со ссылками друг на друга")
    @Test
    void shouldRegisterGameMapsWithLinks() {
        int[] shifts = new int[] {2, 0, 1};
        int areaSize = 10;
        Location location = mock(Location.class);
        List<GameMap> expected = new ArrayList<>();
        GameMap gameMap1 = spy(new GameMapImpl(List.of(new GameArea("1", mock(Location.class)), new GameArea("2", mock(Location.class))), null));
        GameMap gameMap2 = spy(new GameMapImpl(List.of(new GameArea("3", mock(Location.class))), gameMap1));
        GameMap gameMap3 = spy(new GameMapImpl(List.of(new GameArea("4", mock(Location.class)), new GameArea("5", mock(Location.class)), new GameArea("6", mock(Location.class))), gameMap2));
        expected.add(gameMap2);
        expected.add(gameMap3);
        expected.add(gameMap1);
        ((Command) IoC.resolve("IoC.Register", "GameMap.Obtain", (Function<Object[], Object>) args -> {
            int shiftArg = (Integer) args[0];
            int areaSizeArg = (Integer) args[1];
            Location locationArg = (Location) args[2];
            if (areaSizeArg == areaSize && locationArg == location) {
                if (shiftArg == shifts[0]) {
                    return expected.get(0);
                } else if (shiftArg == shifts[1]) {
                    return expected.get(1);
                } else if (shiftArg == shifts[2]) {
                    return expected.get(2);
                }
            }
            return null;
        })).execute();

        GameMapsInitCommand command = new GameMapsInitCommand(shifts, areaSize, location);
        command.execute();
        GameMap actual = IoC.resolve("GameMap");
        assertThat(actual).isEqualTo(gameMap3); // сравниваем по ссылке
    }
}