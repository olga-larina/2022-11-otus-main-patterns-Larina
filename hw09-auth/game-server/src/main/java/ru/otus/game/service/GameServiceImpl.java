package ru.otus.game.service;

import org.springframework.stereotype.Component;
import ru.otus.game.dto.Message;
import ru.otus.game.dto.TokenDto;

@Component
public class GameServiceImpl implements GameService {
    @Override
    public boolean receive(Message message, TokenDto metadata) {
        return true;
    }
}
