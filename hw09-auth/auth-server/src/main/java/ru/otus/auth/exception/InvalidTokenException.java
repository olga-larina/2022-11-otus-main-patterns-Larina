package ru.otus.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidTokenException extends ResponseStatusException {

    public InvalidTokenException() {
        this(null);
    }

    public InvalidTokenException(Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, null, cause);
    }

}
