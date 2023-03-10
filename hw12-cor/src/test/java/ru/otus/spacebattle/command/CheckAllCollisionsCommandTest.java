package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.domain.GameArea;
import ru.otus.spacebattle.domain.GameMap;
import ru.otus.spacebattle.domain.GameMapImpl;
import ru.otus.spacebattle.ioc.IoC;
import ru.otus.spacebattle.ioc.ScopeBasedStrategy;

import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@DisplayName("Команда проверки всех коллизий должна")
class CheckAllCollisionsCommandTest {

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();
    }

    @DisplayName("Вызывать проверку коллизий по всем областям всех игровых полей по цепочке")
    @Test
    void shouldHandleCollisionsForAllGameMapsAndGameAreas() {
        GameArea area1 = mock(GameArea.class);
        GameArea area2 = mock(GameArea.class);
        GameArea area3 = mock(GameArea.class);
        GameArea area4 = mock(GameArea.class);
        GameArea area5 = mock(GameArea.class);
        GameArea area6 = mock(GameArea.class);
        GameMap gameMap3 = spy(new GameMapImpl(List.of(area1, area2), null));
        GameMap gameMap2 = spy(new GameMapImpl(List.of(area3), gameMap3));
        GameMap gameMap1 = spy(new GameMapImpl(List.of(area4, area5, area6), gameMap2));
        ((Command) IoC.resolve("IoC.Register","GameMap", (Function<Object[], Object>) args -> gameMap1)).execute();

        CheckAllCollisionsCommand command = new CheckAllCollisionsCommand();
        command.execute();
        verify(gameMap1, times(1)).handleCollisions();
        verify(gameMap2, times(1)).handleCollisions();
        verify(gameMap3, times(1)).handleCollisions();
        verify(area1, times(1)).handleCollisions();
        verify(area2, times(1)).handleCollisions();
        verify(area3, times(1)).handleCollisions();
        verify(area4, times(1)).handleCollisions();
        verify(area5, times(1)).handleCollisions();
        verify(area6, times(1)).handleCollisions();
    }
}