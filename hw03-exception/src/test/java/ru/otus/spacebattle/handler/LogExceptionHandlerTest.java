package ru.otus.spacebattle.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.command.CommandQueue;
import ru.otus.spacebattle.command.LogCommand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("Обработчик LogExceptionHandler должен")
class LogExceptionHandlerTest {

    private CommandQueue commandQueue;
    private LogExceptionHandler logExceptionHandler;

    @BeforeEach
    void setUp() {
        commandQueue = mock(CommandQueue.class);
        logExceptionHandler = new LogExceptionHandler(commandQueue);
    }

    @DisplayName("Ставить команду LogCommand в очередь")
    @Test
    public void shouldAddLogCommandToQueue() {
        logExceptionHandler.handle(new Exception("Test"), () -> { });
        verify(commandQueue).addLast(any(LogCommand.class));
    }
}
