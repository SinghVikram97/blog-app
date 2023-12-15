package com.vikram.blogapp.exception;

import com.vikram.blogapp.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException) {
        String message = resourceNotFoundException.getMessage();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorMessage(message)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CREATED);    }
}
