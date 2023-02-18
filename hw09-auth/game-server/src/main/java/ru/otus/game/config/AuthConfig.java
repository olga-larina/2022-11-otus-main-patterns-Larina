package ru.otus.game.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "otus.game.auth")
@Configuration
public class AuthConfig {

    private String uri;
    private String user;
    private String password;

    public String getUri() {
        return uri;
    }

    public AuthConfig setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getUser() {
        return user;
    }

    public AuthConfig setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AuthConfig setPassword(String password) {
        this.password = password;
        return this;
    }
}
