package ru.otus.spacebattle.command;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Команда LogCommand должна")
class LogCommandTest {

    @DisplayName("Записывать информацию о выброшенном исключении в лог")
    @Test
    public void shouldLogCommandException() {
        Logger commandLogger = (Logger) LoggerFactory.getLogger(LogCommand.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        commandLogger.addAppender(listAppender);

        Command command = mock(Command.class);
        Exception exception = new IllegalStateException("shouldLogCommandException");

        LogCommand logCommand = new LogCommand(exception, command);
        logCommand.execute();

        List<ILoggingEvent> logsList = listAppender.list;

        assertThat(logsList.size()).isEqualTo(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.ERROR);
        assertThat(logsList.get(0).getFormattedMessage()).isEqualTo(String.format("Exception type=%s message=%s was thrown while executing command=%s",
            exception.getClass().getName(), exception.getMessage(), command.getClass().getName()));
    }
}
