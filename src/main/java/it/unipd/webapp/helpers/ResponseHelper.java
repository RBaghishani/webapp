package it.unipd.webapp.helpers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHelper {
    public static <T> ResponseEntity<T> okay(T data, HttpStatus status) {
        return new ResponseEntity<>(data, status);
    }

    public static ResponseEntity<Object> error(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("status", status.value());
        return new ResponseEntity<>(errorResponse, status);
    }
}
