package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import ru.otus.spacebattle.exception.CommandException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Класс движения по прямой с расходом топлива BurnFuelMoveCommand должен")
class BurnFuelMoveCommandTest {

    private CheckFuelCommand checkFuelCommand;
    private MoveCommand moveCommand;
    private BurnFuelCommand burnFuelCommand;
    private BurnFuelMoveCommand burnFuelMoveCommand;

    @BeforeEach
    void setUp() {
        checkFuelCommand = mock(CheckFuelCommand.class);
        moveCommand = mock(MoveCommand.class);
        burnFuelCommand = mock(BurnFuelCommand.class);
        burnFuelMoveCommand = new BurnFuelMoveCommand(checkFuelCommand, moveCommand, burnFuelCommand);
    }

    @DisplayName("Выполнять по очереди все команды (проверка топлива, движение, сжигание топлива)")
    @Test
    public void shouldExecuteCheckFuelMoveBurnFuelCommands() {
        burnFuelMoveCommand.execute();

        verify(checkFuelCommand, times(1)).execute();
        verify(moveCommand, times(1)).execute();
        verify(burnFuelCommand, times(1)).execute();

        InOrder inOrder = inOrder(checkFuelCommand, moveCommand, burnFuelCommand);
        inOrder.verify(checkFuelCommand).execute();
        inOrder.verify(moveCommand).execute();
        inOrder.verify(burnFuelCommand).execute();
    }

    @DisplayName("Прекращать выполнение всей макрокоманды, если хотя бы одна бросает исключение, и бросать исключение CommandException")
    @Test
    public void shouldStopMacroCommandInCaseOfExceptionAndThrowCommandException() {
        RuntimeException exception = new RuntimeException("test");
        doThrow(exception).when(checkFuelCommand).execute();
        assertThatThrownBy(() -> {
            burnFuelMoveCommand.execute();
        }).isInstanceOf(CommandException.class).hasMessageContaining(exception.getMessage()).hasCause(exception);

        verify(checkFuelCommand, times(1)).execute();
        verify(moveCommand, times(0)).execute();
        verify(burnFuelCommand, times(0)).execute();
    }
}