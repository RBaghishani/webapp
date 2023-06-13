package it.unipd.webapp.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class DuplicateEmailException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final HttpStatus status;

    public DuplicateEmailException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
