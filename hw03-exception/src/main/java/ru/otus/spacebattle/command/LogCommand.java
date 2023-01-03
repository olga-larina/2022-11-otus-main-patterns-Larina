package ru.otus.spacebattle.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Команда, которая записывает информацию о выброшенном исключении в лог
 */
public class LogCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCommand.class);

    private final Exception exception;
    private final Command command;

    public LogCommand(Exception exception, Command command) {
        this.exception = exception;
        this.command = command;
    }

    @Override
    public void execute() {
        LOGGER.error("Exception type={} message={} was thrown while executing command={}",
            exception.getClass().getName(), exception.getMessage(), command.getClass().getName());
    }
}
