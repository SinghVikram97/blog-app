package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.AuthRequest;
import com.vikram.blogapp.dto.AuthResponse;
import com.vikram.blogapp.dto.RegisterUserRequest;

public interface AuthService {
    AuthResponse register(RegisterUserRequest registerUserRequest);
    AuthResponse login(AuthRequest authRequest);
}
