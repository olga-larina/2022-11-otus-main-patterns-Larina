package ru.otus.spacebattle.handler.queue;

import ru.otus.spacebattle.command.CommandQueue;
import ru.otus.spacebattle.state.State;

/**
 * Обработчик очереди, который использует паттерн Состояние для выбора режима обработки.
 * У каждого потока есть своя потокобезопасная очередь и свой обработчик.
 */
public class BasicQueueHandler implements QueueHandler {

    private volatile State state;
    private final CommandQueue commandQueue;

    public BasicQueueHandler(State initialState, CommandQueue commandQueue) {
        this.state = initialState;
        this.commandQueue = commandQueue;
    }

    @Override
    public void handle() {
        State currentState;
        while ((currentState = state) != null) {
            state = currentState.handle(commandQueue);
        }
    }
}
