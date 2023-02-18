package ru.otus.game.dto;

public class IntrospectResponse {

    private TokenDto token;

    public IntrospectResponse() {
    }

    public IntrospectResponse(TokenDto token) {
        this.token = token;
    }

    public TokenDto getToken() {
        return token;
    }

    public IntrospectResponse setToken(TokenDto token) {
        this.token = token;
        return this;
    }
}
