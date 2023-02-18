package ru.otus.game.service;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.otus.game.dto.IntrospectRequest;
import ru.otus.game.dto.IntrospectResponse;
import ru.otus.game.dto.Message;
import ru.otus.game.dto.TokenDto;

@Component
public class AuthServiceImpl implements AuthService {

    private final WebClient webClient;

    public AuthServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public TokenDto verify(Message message, String token) {
        return webClient.post()
            .bodyValue(new IntrospectRequest(token, message.getGameId()))
            .retrieve()
            .bodyToMono(IntrospectResponse.class)
            .block()
            .getToken();
    }
}
