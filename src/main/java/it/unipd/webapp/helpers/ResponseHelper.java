package it.unipd.webapp.helpers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {
    public static <T> ResponseEntity<T> okay(T data, HttpStatus status) {
        return new ResponseEntity<>(data, status);
    }

}
