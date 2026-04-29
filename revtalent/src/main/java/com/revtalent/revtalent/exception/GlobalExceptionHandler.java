package com.revtalent.revtalent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "An error occurred";

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (message.toLowerCase().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message.toLowerCase().contains("already exists")
                || message.toLowerCase().contains("already checked in")
                || message.toLowerCase().contains("already checked out")
                || message.toLowerCase().contains("overlapping")) {
            status = HttpStatus.CONFLICT;
        } else if (message.toLowerCase().contains("only pending")
                || message.toLowerCase().contains("only processed")
                || message.toLowerCase().contains("cannot update")
                || message.toLowerCase().contains("invalid")) {
            status = HttpStatus.BAD_REQUEST;
        }

        return buildResponse(status, message);
    }


    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            org.springframework.web.server.ResponseStatusException ex) {
        return buildResponse(
                HttpStatus.valueOf(ex.getStatusCode().value()),
                ex.getReason() != null ? ex.getReason() : ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String message = "Invalid value '" + ex.getValue()
                + "' for parameter '" + ex.getName() + "'";
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }


    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request body");
    }


    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(
            org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED,
                "HTTP method not allowed: " + ex.getMethod());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error: " + ex.getMessage());
    }


    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}