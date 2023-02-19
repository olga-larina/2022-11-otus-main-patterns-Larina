package ru.otus.auth.dto;

public class TokenResponse {

    private String token;

    public TokenResponse() {
    }

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public TokenResponse setToken(String token) {
        this.token = token;
        return this;
    }
}
