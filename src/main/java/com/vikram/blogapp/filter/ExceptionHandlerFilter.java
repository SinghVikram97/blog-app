package com.vikram.blogapp.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vikram.blogapp.dto.ErrorResponse;
import com.vikram.blogapp.exception.InvalidAuthHeaderException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch (InvalidAuthHeaderException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (MalformedJwtException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse("Invalid JWT token: "+e.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (SignatureException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse("Invalid JWT signature: "+e.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (ExpiredJwtException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse("Expired JWT signature: "+e.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (JwtException e){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse("JWT exception: "+e.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
