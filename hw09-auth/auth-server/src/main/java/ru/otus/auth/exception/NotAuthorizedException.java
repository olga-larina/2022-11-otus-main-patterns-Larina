package ru.otus.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotAuthorizedException extends ResponseStatusException {

    public NotAuthorizedException() {
        this("Not authorized");

    }
    public NotAuthorizedException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }
}
