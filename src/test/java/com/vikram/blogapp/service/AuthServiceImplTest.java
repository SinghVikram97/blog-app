package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.AuthRequest;
import com.vikram.blogapp.dto.AuthResponse;
import com.vikram.blogapp.dto.RegisterUserRequest;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.exception.UserAlreadyExistsException;
import com.vikram.blogapp.jwt.JWTService;
import com.vikram.blogapp.repository.UserRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        RegisterUserRequest registerUserRequest = Instancio.create(RegisterUserRequest.class);
        User user = Instancio.create(User.class);
        String jwtToken = "jwtToken";

        when(passwordEncoder.encode(registerUserRequest.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn(jwtToken);

        AuthResponse authResponse = authService.register(registerUserRequest);

        assertNotNull(authResponse);
        assertEquals(jwtToken, authResponse.getToken());
    }

    @Test
    void testRegister_UserAlreadyExistsException() {
        RegisterUserRequest registerUserRequest = Instancio.create(RegisterUserRequest.class);

        when(passwordEncoder.encode(registerUserRequest.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.register(registerUserRequest);
        });
    }

    @Test
    void testLogin_Success() {
        AuthRequest authRequest = Instancio.create(AuthRequest.class);
        User user = Instancio.create(User.class);
        String jwtToken = "jwtToken";

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        AuthResponse authResponse = authService.login(authRequest);

        assertNotNull(authResponse);
        assertEquals(jwtToken, authResponse.getToken());
    }

    @Test
    void testLogin_BadCredentialsException() {
        AuthRequest authRequest = Instancio.create(AuthRequest.class);

        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(authRequest);
        });
    }

}