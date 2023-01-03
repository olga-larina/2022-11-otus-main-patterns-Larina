package ru.otus.spacebattle.command;

/**
 * Очередь команд
 */
public interface CommandQueue {

    /**
     * Добавить в конец очереди
     * @param command команда
     */
    void addLast(Command command);

    /**
     * Прочитать из начала очереди
     * @return команда из начала очереди или null, если он отсутствует
     */
    Command readFirst();
}
