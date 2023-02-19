package ru.otus.auth.dto;

public class TokenDto {

    private String userName;
    private String gameId;

    public TokenDto() {
    }

    public TokenDto(String userName, String gameId) {
        this.userName = userName;
        this.gameId = gameId;
    }

    public String getUserName() {
        return userName;
    }

    public TokenDto setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getGameId() {
        return gameId;
    }

    public TokenDto setGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }
}
