package ru.otus.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InternalException extends ResponseStatusException {
    public InternalException() {
        this(null);
    }

    public InternalException(Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, null, cause);
    }
}
