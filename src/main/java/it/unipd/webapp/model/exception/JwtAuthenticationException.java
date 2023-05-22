package it.unipd.webapp.model.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class JwtAuthenticationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final HttpStatus status;

    public JwtAuthenticationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
