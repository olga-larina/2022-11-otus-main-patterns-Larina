package ru.otus.spacebattle.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.command.CheckCollisionCommand;
import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.ioc.IoC;
import ru.otus.spacebattle.ioc.ScopeBasedStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Класс окрестности игрового поля должен")
class GameAreaTest {

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();
    }

    @DisplayName("Добавлять / удалять объект и обновлять команду проверки коллизий")
    @Test
    public void shouldAddRemoveObjectAndUpdateCheckCollisionsCommand() {
        List<CheckCollisionInfo> checkCollisionCommands = new ArrayList<>();

        ((Command) IoC.resolve("IoC.Register", "CheckCollisionCommand", (Function<Object[], Object>) args -> {
            AreaObject object1 = (AreaObject) args[0];
            AreaObject object2 = (AreaObject) args[1];
            Command command = spy(new CheckCollisionCommand(object1, object2));
            checkCollisionCommands.add(new CheckCollisionInfo(command, object1, object2));
            return command;
        })).execute();

        String id = "1";
        Location location = mock(Location.class);
        GameArea gameArea = new GameArea(id, location);

        AreaObject object1 = mock(AreaObject.class);
        when(object1.getId()).thenReturn("100");
        AreaObject object2 = mock(AreaObject.class);
        when(object2.getId()).thenReturn("200");
        AreaObject object3 = mock(AreaObject.class);
        when(object3.getId()).thenReturn("300");

        gameArea.addObject(object1);
        assertThat(checkCollisionCommands).isEmpty();

        gameArea.addObject(object2);
        assertThat(checkCollisionCommands.size()).isEqualTo(1);
        assertThat(checkCollisionCommands.get(0).obj1).isEqualTo(object1);
        assertThat(checkCollisionCommands.get(0).obj2).isEqualTo(object2);
        gameArea.handleCollisions();
        verify(checkCollisionCommands.get(0).command, times(1)).execute();

        gameArea.addObject(object3);
        assertThat(checkCollisionCommands.size()).isEqualTo(4);
        assertThat(checkCollisionCommands.get(1).obj1).isEqualTo(object1);
        assertThat(checkCollisionCommands.get(1).obj2).isEqualTo(object2);
        assertThat(checkCollisionCommands.get(2).obj1).isEqualTo(object1);
        assertThat(checkCollisionCommands.get(2).obj2).isEqualTo(object3);
        assertThat(checkCollisionCommands.get(3).obj1).isEqualTo(object2);
        assertThat(checkCollisionCommands.get(3).obj2).isEqualTo(object3);
        gameArea.handleCollisions();
        verify(checkCollisionCommands.get(0).command, times(1)).execute();
        verify(checkCollisionCommands.get(1).command, times(1)).execute();
        verify(checkCollisionCommands.get(2).command, times(1)).execute();
        verify(checkCollisionCommands.get(3).command, times(1)).execute();

        gameArea.removeObject(object2);
        assertThat(checkCollisionCommands.size()).isEqualTo(5);
        assertThat(checkCollisionCommands.get(4).obj1).isEqualTo(object1);
        assertThat(checkCollisionCommands.get(4).obj2).isEqualTo(object3);
        gameArea.handleCollisions();
        verify(checkCollisionCommands.get(0).command, times(1)).execute();
        verify(checkCollisionCommands.get(1).command, times(1)).execute();
        verify(checkCollisionCommands.get(2).command, times(1)).execute();
        verify(checkCollisionCommands.get(3).command, times(1)).execute();
        verify(checkCollisionCommands.get(4).command, times(1)).execute();
    }

    private static class CheckCollisionInfo {
        Command command;
        AreaObject obj1;
        AreaObject obj2;

        CheckCollisionInfo(Command command, AreaObject obj1, AreaObject obj2) {
            this.command = command;
            this.obj1 = obj1;
            this.obj2 = obj2;
        }
    }
}