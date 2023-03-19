package ru.otus.spacebattle.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Класс игрового поля должен")
class GameMapImplTest {

    @DisplayName("Обрабатывать коллизии для текущего игрового поля и всех последующих")
    @Test
    void shouldHandleCollisionsForAllGameMaps() {
        GameArea area1 = mock(GameArea.class);
        GameArea area2 = mock(GameArea.class);
        GameArea area3 = mock(GameArea.class);
        GameArea area4 = mock(GameArea.class);
        GameArea area5 = mock(GameArea.class);
        GameArea area6 = mock(GameArea.class);
        GameMap gameMap3 = spy(new GameMapImpl(List.of(area1, area2), null));
        GameMap gameMap2 = spy(new GameMapImpl(List.of(area3), gameMap3));
        GameMap gameMap1 = spy(new GameMapImpl(List.of(area4, area5, area6), gameMap2));

        gameMap1.handleCollisions();
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

    @DisplayName("Возвращать все окрестности, подходящие расположению объекта, из текущего игрового поля и всех последующих")
    @Test
    void shouldGetSuitableAreasForAllGameMaps() {
        Location location = mock(Location.class);

        GameArea area1 = mock(GameArea.class);
        GameArea area2 = mock(GameArea.class);
        GameArea area3 = mock(GameArea.class);
        GameArea area4 = mock(GameArea.class);
        GameArea area5 = mock(GameArea.class);
        GameArea area6 = mock(GameArea.class);
        when(area1.getId()).thenReturn("1");
        when(area2.getId()).thenReturn("2");
        when(area3.getId()).thenReturn("3");
        when(area4.getId()).thenReturn("4");
        when(area5.getId()).thenReturn("5");
        when(area6.getId()).thenReturn("6");

        GameMap gameMap3 = spy(new GameMapImpl(List.of(area1, area2), null));
        GameMap gameMap2 = spy(new GameMapImpl(List.of(area3), gameMap3));
        GameMap gameMap1 = spy(new GameMapImpl(List.of(area4, area5, area6), gameMap2));

        when(area1.testLocation(eq(location))).thenReturn(true);
        when(area2.testLocation(eq(location))).thenReturn(false);
        when(area3.testLocation(eq(location))).thenReturn(true);
        when(area4.testLocation(eq(location))).thenReturn(false);
        when(area5.testLocation(eq(location))).thenReturn(true);
        when(area6.testLocation(eq(location))).thenReturn(false);

        Map<String, Area> areas = gameMap1.getAreas(location);
        assertThat(areas).isEqualTo(Map.of(area1.getId(), area1, area3.getId(), area3, area5.getId(), area5));
        verify(gameMap1, times(1)).getAreas(eq(location));
        verify(gameMap2, times(1)).getAreas(eq(location));
        verify(gameMap3, times(1)).getAreas(eq(location));
        verify(area1, times(1)).testLocation(eq(location));
        verify(area2, times(1)).testLocation(eq(location));
        verify(area3, times(1)).testLocation(eq(location));
        verify(area4, times(1)).testLocation(eq(location));
        verify(area5, times(1)).testLocation(eq(location));
        verify(area6, times(1)).testLocation(eq(location));
    }
}
