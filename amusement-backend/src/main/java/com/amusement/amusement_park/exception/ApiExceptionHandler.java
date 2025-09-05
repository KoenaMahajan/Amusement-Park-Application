package com.amusement.amusement_park.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({DuplicateFoodItemException.class, FoodItemNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleCustomExceptions(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        HttpStatus status = (ex instanceof DuplicateFoodItemException)
                ? HttpStatus.BAD_REQUEST
                : HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status).body(error);
    }
}
