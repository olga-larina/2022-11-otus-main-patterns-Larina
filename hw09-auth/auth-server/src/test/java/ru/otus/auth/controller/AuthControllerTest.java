package ru.otus.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.auth.config.SecurityConfig;
import ru.otus.auth.dto.*;
import ru.otus.auth.service.AuthService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контроллер авторизации")
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService authService;

    private final ObjectMapper mapper = new ObjectMapper();

    @DisplayName("Бросает ошибку для неаутентифицированных пользователей")
    @Test
    void shouldThrowIfNotAuthenticated() throws Exception {
        mvc.perform(post("/auth/registerGame")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new RegisterGameRequest(List.of("user1", "user2"))))
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());

        mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new TokenRequest("myGame")))
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());

        mvc.perform(post("/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new IntrospectRequest("myGame", "myToken")))
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @DisplayName("Бросает ошибку для пустого списка участников при регистрации игры")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    void shouldThrowIfNoUsersInRegisterGame() throws Exception {
        mvc.perform(post("/auth/registerGame")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new RegisterGameRequest(List.of())))
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("Регистрирует игру и возвращает id игры")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    void shouldRegisterGameAndReturnGameId() throws Exception {
        List<String> users = List.of("user1", "user2");
        String gameId = UUID.randomUUID().toString();
        when(authService.registerGame(eq(users))).thenReturn(gameId);

        mvc.perform(post("/auth/registerGame")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new RegisterGameRequest(users)))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(new RegisterGameResponse(gameId))))
        ;
    }

    @DisplayName("Бросает ошибку для пустой id игры при получении токена")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    void shouldThrowIfEmptyGameIdInToken() throws Exception {
        mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new TokenRequest("")))
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("Создаёт и возвращает токен")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    void shouldGenerateAndReturnToken() throws Exception {
        String gameId = "myGame";
        String token = UUID.randomUUID().toString();
        when(authService.token(eq(gameId))).thenReturn(token);

        mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new TokenRequest(gameId)))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(new TokenResponse(token))))
        ;
    }

    @DisplayName("Бросает ошибку для пустой id игры или токена при валидации токена")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    void shouldThrowIfEmptyGameIdOrTokenInIntrospect() throws Exception {
        mvc.perform(post("/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new IntrospectRequest()))
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("Валидирует токен и возвращает метаданные")
    @WithMockUser(value = "user1", password = "password1")
    @Test
    void shouldValidateTokenAndReturnInfo() throws Exception {
        String token = UUID.randomUUID().toString();
        String gameId = UUID.randomUUID().toString();
        String userName = "user1";
        when(authService.verify(eq(token), eq(gameId))).thenReturn(new TokenDto(userName, gameId));

        mvc.perform(post("/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new IntrospectRequest(token, gameId)))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(new IntrospectResponse(new TokenDto(userName, gameId)))))
        ;
    }
}
