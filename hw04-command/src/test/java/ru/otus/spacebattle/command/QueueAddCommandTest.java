package ru.otus.spacebattle.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("Команда QueueAddCommand должна")
class QueueAddCommandTest {

    @DisplayName("Ставить в очередь другую команду")
    @Test
    public void shouldAddToQueueOtherCommand() {
        Command command = mock(Command.class);
        CommandQueue commandQueue = mock(CommandQueue.class);
        QueueAddCommand queueAddCommand = new QueueAddCommand(command, commandQueue);
        queueAddCommand.execute();
        verify(commandQueue, times(1)).addLast(argThat(c -> c == command));
    }
}
