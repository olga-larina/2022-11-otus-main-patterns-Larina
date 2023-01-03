package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.domain.FuelCheckable;
import ru.otus.spacebattle.exception.CommandException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Класс проверки топлива CheckFuelCommand должен")
class CheckFuelCommandTest {

    private FuelCheckable fuelCheckable;
    private CheckFuelCommand checkFuelCommand;

    @BeforeEach
    void setUp() {
        fuelCheckable = mock(FuelCheckable.class);
        checkFuelCommand = new CheckFuelCommand(fuelCheckable);
    }

    @DisplayName("Ничего не делать, если топлива достаточно для движения")
    @Test
    public void shouldDoNothingWhenFuelIsEnough() {
        when(fuelCheckable.getFuelLevel()).thenReturn(10);
        when(fuelCheckable.getFuelBurnVelocity()).thenReturn(2);
        checkFuelCommand.execute();

        verify(fuelCheckable, times(1)).getFuelLevel();
        verify(fuelCheckable, times(1)).getFuelBurnVelocity();
    }

    @DisplayName("Бросать исключение, если топлива недостаточно")
    @Test
    public void shouldThrowExceptionWhenNotEnoughFuelToBurn() {
        when(fuelCheckable.getFuelLevel()).thenReturn(1);
        when(fuelCheckable.getFuelBurnVelocity()).thenReturn(2);
        assertThatThrownBy(() -> {
            checkFuelCommand.execute();
        }).isInstanceOf(CommandException.class).hasMessageContaining("Not enough fuel");
    }
}