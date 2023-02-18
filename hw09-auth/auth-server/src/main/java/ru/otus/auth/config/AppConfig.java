package ru.otus.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "otus.auth")
@Configuration
public class AppConfig {

    private String issuer;

    public String getIssuer() {
        return issuer;
    }

    public AppConfig setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }
}
