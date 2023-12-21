package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.AuthRequest;
import com.vikram.blogapp.dto.AuthResponse;
import com.vikram.blogapp.dto.RegisterUserRequest;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.jwt.JWTService;
import com.vikram.blogapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public AuthResponse register(RegisterUserRequest registerUserRequest) {
        final User user = User.builder()
                .firstName(registerUserRequest.getFirstName())
                .lastName(registerUserRequest.getLastName())
                .email(registerUserRequest.getEmail())
                .password(passwordEncoder.encode(registerUserRequest.getPassword()))
                .about(registerUserRequest.getAbout())
                .role(registerUserRequest.getRole())
                .build();

        try{
            userRepository.save(user);
        }catch (Exception e){
            System.out.println("VIKRAM HELLO "+e);
            System.out.println(e.getMessage());
            throw e;
        }

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
        }catch (Exception e){
            System.out.println(e);
            throw e;
        }


        final User user =  userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
