package ru.otus.auth.service.jwt;

import ru.otus.auth.dto.TokenDto;

public interface JwtService {

    /**
     * Выдача токена на участие в конкретной игре
     * @param userName имя пользователя
     * @param gameId id игры
     * @return токен
     */
    String token(String userName, String gameId);

    /**
     * Проверка токена на участие пользователя в игре
     * @param token токен
     * @param gameId id игры
     * @return информация по пользователю и токену
     */
    TokenDto verify(String token, String gameId);
}
