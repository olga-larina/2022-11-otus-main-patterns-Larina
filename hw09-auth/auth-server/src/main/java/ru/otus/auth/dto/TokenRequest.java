package ru.otus.auth.dto;

public class TokenRequest {

    private String gameId;

    public TokenRequest() {
    }

    public TokenRequest(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public TokenRequest setGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }
}
