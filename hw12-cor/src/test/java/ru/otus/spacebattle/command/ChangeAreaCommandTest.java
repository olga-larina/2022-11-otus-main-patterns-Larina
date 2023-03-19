package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.domain.*;
import ru.otus.spacebattle.ioc.IoC;
import ru.otus.spacebattle.ioc.ScopeBasedStrategy;

import java.util.Map;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@DisplayName("Команда возможной смены окрестности должна")
class ChangeAreaCommandTest {

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();
    }

    @DisplayName("При смене окрестности удалять объект из старой, добавлять в новую окрестность и обновлять макрокоманды проверки коллизий")
    @Test
    void shouldRemoveFromOldAddToNewAndUpdateMacrocommandsWhenAreaChanged() {
        Location previousLocation = mock(Location.class);
        Location currentLocation = mock(Location.class);

        AreaObject obj = mock(AreaObject.class);
        when(obj.getId()).thenReturn("100");
        when(obj.getLocation()).thenReturn(currentLocation);

        GameArea area1 = mock(GameArea.class);
        GameArea area2 = mock(GameArea.class);
        GameArea area3 = mock(GameArea.class);
        when(area1.getId()).thenReturn("1");
        when(area2.getId()).thenReturn("2");
        when(area3.getId()).thenReturn("3");

        Map<String, Area> previousAreas = Map.of(area1.getId(), area1, area2.getId(), area2);
        Map<String, Area> currentAreas = Map.of(area2.getId(), area2, area3.getId(), area3);

        GameMap gameMap = mock(GameMap.class);
        when(gameMap.getAreas(eq(previousLocation))).thenReturn(previousAreas);
        when(gameMap.getAreas(eq(currentLocation))).thenReturn(currentAreas);
        ((Command) IoC.resolve("IoC.Register","GameMap", (Function<Object[], Object>) args -> gameMap)).execute();

        ChangeAreaCommand command = new ChangeAreaCommand(obj, previousLocation);
        command.execute();
        verify(area1, times(1)).removeObject(eq(obj));
        verify(area3, times(1)).addObject(eq(obj));
        verify(area1, times(0)).addObject(any());
        verify(area3, times(0)).removeObject(any());
        verify(area2, times(0)).addObject(any());
        verify(area2, times(0)).removeObject(any());
    }

    @DisplayName("Добавлять в новые окрестности, если предыдущие отсутствуют (при инициализации игрового поля_")
    @Test
    void shouldAddToNewAreasIfPreviousIsNull() {
        Location currentLocation = mock(Location.class);

        AreaObject obj = mock(AreaObject.class);
        when(obj.getId()).thenReturn("100");
        when(obj.getLocation()).thenReturn(currentLocation);

        GameArea area1 = mock(GameArea.class);
        when(area1.getId()).thenReturn("1");

        Map<String, Area> currentAreas = Map.of(area1.getId(), area1);

        GameMap gameMap = mock(GameMap.class);
        when(gameMap.getAreas(eq(currentLocation))).thenReturn(currentAreas);
        ((Command) IoC.resolve("IoC.Register","GameMap", (Function<Object[], Object>) args -> gameMap)).execute();

        ChangeAreaCommand command = new ChangeAreaCommand(obj, null);
        command.execute();
        verify(area1, times(1)).addObject(eq(obj));
        verify(area1, times(0)).removeObject(any());
    }
}