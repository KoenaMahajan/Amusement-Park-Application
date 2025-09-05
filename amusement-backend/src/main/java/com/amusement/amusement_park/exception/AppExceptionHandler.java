package com.amusement.amusement_park.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                           .stream()
                           .findFirst()
                           .map(err -> err.getDefaultMessage())
                           .orElse("Invalid input");
        return ResponseEntity.badRequest().body(message);
    }
}
