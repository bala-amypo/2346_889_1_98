
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles specific Resource Not Found errors.
     * Triggers when a Service throws ResourceNotFoundException.
     * Returns HTTP 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles all Business Rule and Validation errors.
     * Triggers when a Service throws RuntimeException or IllegalArgumentException.
     * Returns HTTP 400.
     */
    @ExceptionHandler({RuntimeException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleBusinessRules(Exception ex) {
        // If the message contains "not found", we treat it as a 404 for safety
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Global fallback for any unhandled exceptions.
     * Returns HTTP 500 with a clean message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    /**
     * Helper method to build a consistent JSON error structure.
     */
    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}