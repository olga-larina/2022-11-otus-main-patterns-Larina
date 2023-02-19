package ru.otus.auth.dto;

public class RegisterGameResponse {

    private String gameId;

    public RegisterGameResponse() {
    }

    public RegisterGameResponse(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public RegisterGameResponse setGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }
}
