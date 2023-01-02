package ru.otus.spacebattle.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("Команда RepeatCommand должна")
class RepeatCommandTest {

    @DisplayName("Повторять команду, выбросившую исключение")
    @Test
    public void shouldRepeatFailedCommand() {
        Command failedCommand = mock(Command.class);
        RepeatCommand repeatCommand = new RepeatCommand(failedCommand);
        repeatCommand.execute();
        verify(failedCommand, times(1)).execute();
    }
}
