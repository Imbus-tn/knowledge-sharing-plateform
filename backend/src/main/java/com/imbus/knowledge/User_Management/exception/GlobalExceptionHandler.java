package com.imbus.knowledge.User_Management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, ?>> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(ex.getErrors(), HttpStatus.BAD_REQUEST);
    }

    // Handle InvalidOtpException
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<Map<String, List<String>>> handleInvalidOtpException(InvalidOtpException ex) {
        Map<String, List<String>> errorResponse = Map.of("error", List.of(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle OtpExpiredException
    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<Map<String, List<String>>> handleOtpExpiredException(OtpExpiredException ex) {
        Map<String, List<String>> errorResponse = Map.of("error", List.of(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<String>>> handleGenericException(Exception ex) {
        Map<String, List<String>> errorResponse = Map.of("error", List.of("An unexpected error occurred: " + ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
