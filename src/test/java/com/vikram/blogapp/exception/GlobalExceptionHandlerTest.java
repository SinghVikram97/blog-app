package com.vikram.blogapp.exception;

import com.vikram.blogapp.dto.ErrorResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void test_handleResourceNotFoundException() {
        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("User","Id",123);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleResourceNotFoundException(resourceNotFoundException);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        ErrorResponse errorResponse = responseEntity.getBody();
        assertEquals(resourceNotFoundException.getMessage(), errorResponse.getErrorMessage());
    }

    @Test
    void test_handleMethodArgumentNotValidException() {
        String objectName="objectName";
        String fieldName="fieldName";
        String errorMessage="error message";
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(new FieldError(objectName, fieldName, errorMessage)));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        Map<String, String> errorResponseMap = responseEntity.getBody();
        assertEquals(1, errorResponseMap.size());
        assertEquals(errorMessage, errorResponseMap.get(fieldName));
    }

    @Test
    void handleUserAlreadyExistsException() {
        String username="vikram@gmail.com";
        UserAlreadyExistsException exception = new UserAlreadyExistsException(username);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleUserAlreadyExistsException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("User already exists with the username: "+username, responseEntity.getBody().getErrorMessage());
    }

    @Test
    void handleUserNotAuthorizedException() {
        UserNotAuthorizedException exception = new UserNotAuthorizedException();

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleUserNotAuthorizedException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("The user is not authorized to perform this action", responseEntity.getBody().getErrorMessage());
    }
}
