package ru.otus.spacebattle.command;

/**
 * Команда-маркер, появление которой в очереди означает прекращение обработки
 */
public class HardStopCommand implements Command {

    @Override
    public void execute() {
    }

}
