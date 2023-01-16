package ru.otus.spacebattle.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.otus.spacebattle.command.*;
import ru.otus.spacebattle.handler.RepeatThanLogExceptionHandlerStrategy;
import ru.otus.spacebattle.handler.RepeatTwiceThanLogExceptionHandlerStrategy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Класс SpaceBattleApp должен")
class SpaceBattleAppTest {

    @DisplayName("Реализовать обработку исключений: повторить, затем залогировать")
    @Test
    public void shouldRepeatThanLog() {
        CommandQueue commandQueue = spy(new CommandQueueImpl());
        SpaceBattleApp app = new SpaceBattleApp(commandQueue, new RepeatThanLogExceptionHandlerStrategy());

        RuntimeException thrownException = new RuntimeException("test");
        Command command = () -> { throw thrownException; };
        commandQueue.addLast(command);

        app.process();

        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandQueue, times(3)).addLast(commandCaptor.capture());
        List<Command> commandsAdded = commandCaptor.getAllValues();
        assertThat(commandsAdded.get(0)).isEqualTo(command);
        assertThat(commandsAdded.get(1).getClass()).isEqualTo(RepeatCommand.class);
        assertThat(commandsAdded.get(2).getClass()).isEqualTo(LogCommand.class);
    }

    @DisplayName("Реализовать обработку исключений: повторить дважды, затем залогировать")
    @Test
    public void shouldTwiceRepeatThanLog() {
        CommandQueue commandQueue = spy(new CommandQueueImpl());
        SpaceBattleApp app = new SpaceBattleApp(commandQueue, new RepeatTwiceThanLogExceptionHandlerStrategy());

        RuntimeException thrownException = new RuntimeException("test");
        Command command = () -> { throw thrownException; };
        commandQueue.addLast(command);

        app.process();

        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandQueue, times(4)).addLast(commandCaptor.capture());
        List<Command> commandsAdded = commandCaptor.getAllValues();
        assertThat(commandsAdded.get(0)).isEqualTo(command);
        assertThat(commandsAdded.get(1).getClass()).isEqualTo(RepeatCommand.class);
        assertThat(commandsAdded.get(2).getClass()).isEqualTo(TwiceRepeatCommand.class);
        assertThat(commandsAdded.get(3).getClass()).isEqualTo(LogCommand.class);
    }
}