package ru.otus.game.service;

import ru.otus.game.dto.Message;
import ru.otus.game.dto.TokenDto;

public interface AuthService {

    /**
     * Проверяет токен
     * @param message сообщение
     * @param token токен
     * @return метаинформация
     */
    TokenDto verify(Message message, String token);
}
