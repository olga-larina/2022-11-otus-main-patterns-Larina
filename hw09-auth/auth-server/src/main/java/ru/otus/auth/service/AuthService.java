package ru.otus.auth.service;

import ru.otus.auth.dto.TokenDto;

import java.util.List;

public interface AuthService {

    /**
     * Регистрация новой игры
     * @param users список участников
     * @return id новой игры
     */
    String registerGame(List<String> users);

    /**
     * Получить токен
     * @param gameId id игры
     * @return токен для данного пользователя и игры
     */
    String token(String gameId);

    /**
     * Валидация токена
     * @param token токен
     * @param gameId id игры
     * @return информация по токену и пользователю (если успешно прошёл верификацию)
     */
    TokenDto verify(String token, String gameId);
}
