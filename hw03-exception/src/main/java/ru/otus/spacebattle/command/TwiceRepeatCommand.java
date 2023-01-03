package ru.otus.spacebattle.command;

/**
 * Команда, которая повторяет другую Команду. При этом команду не удалось выполнить минимум два раза
 */
public class TwiceRepeatCommand extends RepeatCommand {

    public TwiceRepeatCommand(Command command) {
        super(command);
    }
}
