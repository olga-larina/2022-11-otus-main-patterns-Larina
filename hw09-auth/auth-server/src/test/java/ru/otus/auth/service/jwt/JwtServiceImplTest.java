package ru.otus.auth.service.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.auth.config.AppConfig;
import ru.otus.auth.dto.TokenDto;
import ru.otus.auth.exception.InvalidTokenException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис токенов")
public class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(new AppConfig().setIssuer("test"));
    }

    @DisplayName("Создаёт токен")
    @Test
    public void shouldCreateToken() {
        String token = jwtService.token("myName", "myGame");
        assertThat(token).isNotNull().isNotEmpty();
    }

    @DisplayName("Создаёт корректный токен, который проходит валидацию с теми же пользователем и игрой")
    @Test
    public void shouldCreateValidToken() {
        String userName = "myName";
        String gameId = "myGame";
        String token = jwtService.token(userName, gameId);
        TokenDto tokenDto = jwtService.verify(token, gameId);
        assertThat(tokenDto.getUserName()).isEqualTo(userName);
        assertThat(tokenDto.getGameId()).isEqualTo(gameId);
    }

    @DisplayName("Бросает ошибку при валидации с некорректными параметрами")
    @Test
    public void shouldNotVerifyIfNotValidData() {
        String userName = "myName";
        String gameId = "myGame";
        String token = jwtService.token(userName, gameId);

        assertThatThrownBy(() -> {
            jwtService.verify(token, gameId + "!!!");
        }).isInstanceOf(InvalidTokenException.class);

        assertThatThrownBy(() -> {
            jwtService.verify(token + "!!!", gameId);
        }).isInstanceOf(InvalidTokenException.class);
    }
}
