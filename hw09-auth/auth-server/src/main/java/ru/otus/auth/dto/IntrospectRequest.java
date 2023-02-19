package ru.otus.auth.dto;

public class IntrospectRequest {

    private String token;
    private String gameId;

    public IntrospectRequest() {
    }

    public IntrospectRequest(String token, String gameId) {
        this.token = token;
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public IntrospectRequest setGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }

    public String getToken() {
        return token;
    }

    public IntrospectRequest setToken(String token) {
        this.token = token;
        return this;
    }
}
