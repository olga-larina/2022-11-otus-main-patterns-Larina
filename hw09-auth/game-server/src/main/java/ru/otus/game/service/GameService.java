package ru.otus.game.service;

import ru.otus.game.dto.Message;
import ru.otus.game.dto.TokenDto;

public interface GameService {

    boolean receive(Message message, TokenDto metadata);
}
