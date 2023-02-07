package ru.otus.spacebattle.message;

public interface Endpoint {

    void receive(Message message);
}