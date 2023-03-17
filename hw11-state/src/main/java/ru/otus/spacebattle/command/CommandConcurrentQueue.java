package ru.otus.spacebattle.command;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CommandConcurrentQueue implements CommandQueue {

    private final Deque<Command> deque;

    public CommandConcurrentQueue() {
        this.deque = new ConcurrentLinkedDeque<>();
    }

    @Override
    public void addLast(Command object) {
        deque.addLast(object);
    }

    @Override
    public Command readFirst() {
        return deque.pollFirst();
    }

}
