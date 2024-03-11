package com.vikram.blogapp.controller;

import com.vikram.blogapp.dto.AuthRequest;
import com.vikram.blogapp.dto.AuthResponse;
import com.vikram.blogapp.dto.RegisterUserRequest;
import com.vikram.blogapp.service.AuthService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_returnsAuthResponse() {
        RegisterUserRequest request = Instancio.create(RegisterUserRequest.class);
        AuthResponse authResponse = Instancio.create(AuthResponse.class);

        when(authService.register(request)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> responseEntity = authController.register(request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(authResponse, responseEntity.getBody());

        verify(authService, times(1)).register(request);
    }

    @Test
    void testLoginUser_returnsAuthResponse() {
        AuthRequest request = Instancio.create(AuthRequest.class);
        AuthResponse authResponse = Instancio.create(AuthResponse.class);

        when(authService.login(request)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> responseEntity = authController.login(request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(authResponse, responseEntity.getBody());

        verify(authService, times(1)).login(request);
    }
}
