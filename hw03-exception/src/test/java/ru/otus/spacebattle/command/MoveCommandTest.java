package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.domain.Coords;
import ru.otus.spacebattle.domain.Movable;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Класс движения MoveCommand должен")
class MoveCommandTest {

    private Movable movable;
    private MoveCommand moveCommand;

    @BeforeEach
    void setUp() {
        movable = mock(Movable.class);
        moveCommand = new MoveCommand(movable);
    }

    @DisplayName("Менять положение на (5, 8) для объекта, находящегося в точке (12, 5) и движущегося со скоростью (-7, 3)")
    @Test
    public void shouldMoveObjectToCorrectPosition() {
        when(movable.getPosition()).thenReturn(new Coords(12, 5));
        when(movable.getVelocity()).thenReturn(new Coords(-7, 3));
        moveCommand.execute();

        verify(movable, timeout(1)).setPosition(argThat(coords -> coords.getCoord(0) == 5 && coords.getCoord(1) == 8));
    }

    @DisplayName("Бросать исключение при попытке сдвинуть объект, у которого невозможно прочитать положение в пространстве")
    @Test
    public void shouldThrowExceptionWhenCanNotReadPosition() {
        when(movable.getPosition()).thenReturn(null);
        assertThatThrownBy(() -> {
            moveCommand.execute();
        }).isInstanceOf(IllegalStateException.class).hasMessageContaining("Can not get position");
    }

    @DisplayName("Бросать исключение при попытке сдвинуть объект, у которого невозможно прочитать значение мгновенной скорости")
    @Test
    public void shouldThrowExceptionWhenCanNotReadVelocity() {
        when(movable.getPosition()).thenReturn(new Coords(12, 5));
        when(movable.getVelocity()).thenReturn(null);
        assertThatThrownBy(() -> {
            moveCommand.execute();
        }).isInstanceOf(IllegalStateException.class).hasMessageContaining("Can not get velocity");
    }

    @DisplayName("Бросать исключение при попытке сдвинуть объект, у которого невозможно изменить положение в пространстве")
    @Test
    public void shouldThrowExceptionWhenCanNotChangePosition() {
        when(movable.getPosition()).thenReturn(new Coords(12, 5));
        when(movable.getVelocity()).thenReturn(new Coords(-7, 3));
        doThrow(new RuntimeException("Can not set position")).when(movable).setPosition(any());
        assertThatThrownBy(() -> {
            moveCommand.execute();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Can not set position");
    }
}