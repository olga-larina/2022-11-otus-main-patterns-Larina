package ru.otus.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.auth.dto.TokenDto;
import ru.otus.auth.exception.NotAuthorizedException;
import ru.otus.auth.exception.NotFoundException;
import ru.otus.auth.service.jwt.JwtService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("Авторизационный сервис")
@SpringBootTest
public class AuthServiceImplTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @DisplayName("Регистрирует игру и возвращает id")
    @Test
    public void shouldRegisterGameAndReturnId() {
        String gameId = authService.registerGame(List.of("user1", "user2"));
        assertThat(gameId).isNotNull().isNotEmpty();
    }

    @DisplayName("Бросает ошибку при создании токена для неаутентифицированного пользователя")
    @Test
    public void shouldThrowWhenTokenCreatingIfNotAuthenticated() {
        assertThatThrownBy(() -> {
            authService.token(UUID.randomUUID().toString());
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @DisplayName("Бросает ошибку при создании токена, если не найдена игра")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    public void shouldThrowWhenTokenCreatingIfGameNotFound() {
        String gameId = authService.registerGame(List.of("user1", "user2"));

        assertThatThrownBy(() -> {
            authService.token(gameId + "!");
        }).isInstanceOf(NotFoundException.class);
    }

    @DisplayName("Бросает ошибку при создании токена, если пользователь не в списке участников игры")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    public void shouldThrowWhenTokenCreatingIfUserNotInParticipants() {
        String gameId = authService.registerGame(List.of("user3", "user2"));

        assertThatThrownBy(() -> {
            authService.token(gameId);
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @DisplayName("Создаёт токен, если все параметры корректны")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    public void shouldCreateTokenIfParametersValid() {
        String gameId = authService.registerGame(List.of("user1", "user2"));
        String token = UUID.randomUUID().toString();
        when(jwtService.token(eq("user1"), eq(gameId))).thenReturn(token);

        assertThat(authService.token(gameId)).isEqualTo(token);
    }

    @DisplayName("Верифицирует токен и возвращает метаданные")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    public void shouldVerifyAndReturnMetadata() {
        String gameId = authService.registerGame(List.of("user1", "user2"));
        String token = UUID.randomUUID().toString();
        when(jwtService.verify(eq(token), eq(gameId))).thenReturn(new TokenDto("user1", gameId));

        TokenDto tokenDto = authService.verify(token, gameId);
        assertThat(tokenDto.getUserName()).isEqualTo("user1");
        assertThat(tokenDto.getGameId()).isEqualTo(gameId);
    }
}
