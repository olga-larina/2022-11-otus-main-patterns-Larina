package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.domain.Rotatable;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Класс поворота RotateCommand должен")
class RotateCommandTest {

    private Rotatable rotatable;
    private RotateCommand rotateCommand;

    @BeforeEach
    void setUp() {
        rotatable = mock(Rotatable.class);
        rotateCommand = new RotateCommand(rotatable);
    }

    @DisplayName("Выполнять поворот на угол 120 при начальном положении 100 и угловой скорости 20")
    @Test
    public void shouldRotateObjectToCorrectPositionWhenLessThanDirectionsNum() {
        when(rotatable.getDirectionsNum()).thenReturn(360);
        when(rotatable.getDirection()).thenReturn(100);
        when(rotatable.getAngularVelocity()).thenReturn(20);
        rotateCommand.execute();

        verify(rotatable, timeout(1)).setDirection(eq(120));
    }

    @DisplayName("Выполнять поворот на угол 50 при начальном положении 350 и угловой скорости 60")
    @Test
    public void shouldRotateObjectToCorrectPositionWhenMoreThanDirectionsNum() {
        when(rotatable.getDirectionsNum()).thenReturn(360);
        when(rotatable.getDirection()).thenReturn(350);
        when(rotatable.getAngularVelocity()).thenReturn(60);
        rotateCommand.execute();

        verify(rotatable, timeout(1)).setDirection(eq(50));
    }

    @DisplayName("Бросать исключение при попытке повернуть объект с нулевым кол-вом направлений")
    @Test
    public void shouldThrowExceptionWhenDirectionsNumIsZero() {
        when(rotatable.getDirectionsNum()).thenReturn(0);
        when(rotatable.getDirection()).thenReturn(100);
        when(rotatable.getAngularVelocity()).thenReturn(20);
        assertThatThrownBy(() -> {
            rotateCommand.execute();
        }).isInstanceOf(IllegalStateException.class).hasMessageContaining("Directions num is zero");
    }

    @DisplayName("Бросать исключение при попытке повернуть объект, у которого невозможно прочитать начальное положение")
    @Test
    public void shouldThrowExceptionWhenCanNotReadDirection() {
        when(rotatable.getDirectionsNum()).thenReturn(360);
        when(rotatable.getDirection()).thenThrow(new RuntimeException("Can not get direction"));
        when(rotatable.getAngularVelocity()).thenReturn(20);
        assertThatThrownBy(() -> {
            rotateCommand.execute();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Can not get direction");
    }

    @DisplayName("Бросать исключение при попытке повернуть объект, у которого невозможно прочитать угловую скорость")
    @Test
    public void shouldThrowExceptionWhenCanNotReadAngularVelocity() {
        when(rotatable.getDirectionsNum()).thenReturn(360);
        when(rotatable.getDirection()).thenReturn(100);
        when(rotatable.getAngularVelocity()).thenThrow(new RuntimeException("Can not get angular velocity"));
        assertThatThrownBy(() -> {
            rotateCommand.execute();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Can not get angular velocity");
    }

    @DisplayName("Бросать исключение при попытке повернуть объект, у которого невозможно изменить положение в пространстве")
    @Test
    public void shouldThrowExceptionWhenCanNotChangeDirection() {
        when(rotatable.getDirectionsNum()).thenReturn(360);
        when(rotatable.getDirection()).thenReturn(100);
        when(rotatable.getAngularVelocity()).thenReturn(20);
        doThrow(new RuntimeException("Can not set direction")).when(rotatable).setDirection(anyInt());
        assertThatThrownBy(() -> {
            rotateCommand.execute();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Can not set direction");
    }
}