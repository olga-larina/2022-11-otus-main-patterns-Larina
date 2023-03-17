package ru.otus.spacebattle.state;

import ru.otus.spacebattle.command.CommandQueue;

/**
 * Состояние режима обработки команд
 */
public interface State {

    /**
     * Обработать команду из очереди и вернуть ссылку на следующее состояние
     * @param queue очередь команд
     * @return ссылка на следующее состояние
     */
    State handle(CommandQueue queue);
}
