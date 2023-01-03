package ru.otus.spacebattle.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.command.CommandQueue;
import ru.otus.spacebattle.command.RepeatCommand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("Обработчик RepeatExceptionHandler должен")
class RepeatExceptionHandlerTest {

    private CommandQueue commandQueue;
    private RepeatExceptionHandler repeatExceptionHandler;

    @BeforeEach
    void setUp() {
        commandQueue = mock(CommandQueue.class);
        repeatExceptionHandler = new RepeatExceptionHandler(commandQueue);
    }

    @DisplayName("Ставить команду RepeatCommand в очередь")
    @Test
    public void shouldAddRepeatCommandToQueue() {
        repeatExceptionHandler.handle(new Exception("Test"), () -> { });
        verify(commandQueue).addLast(any(RepeatCommand.class));
    }
}
