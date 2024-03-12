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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

class ExceptionHandlerFilterTest {

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private MockHttpServletRequest request;

    @Mock
    private MockHttpServletResponse response;

    @Mock
    private MockFilterChain filterChain;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private ExceptionHandlerFilter exceptionHandlerFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNoExceptionThrown() throws ServletException, IOException {
        doNothing().when(filterChain).doFilter(request, response);

        exceptionHandlerFilter.doFilterInternal(request, response, filterChain);

        verify(response, never()).setStatus(anyInt());
        verify(response, never()).setContentType(anyString());
        verify(printWriter, never()).write(anyString());
    }

    @Test
    void testInvalidAuthHeaderException() throws ServletException, IOException {
        String authHeaderValue = "authHeaderValue";
        when(response.getWriter()).thenReturn(printWriter);
        doThrow(new InvalidAuthHeaderException(authHeaderValue)).when(filterChain).doFilter(request, response);

        exceptionHandlerFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
        verify(response).setContentType("application/json");
        ErrorResponse expectedResponse = new ErrorResponse("Missing or Invalid Auth Header: "+authHeaderValue);
        verify(printWriter).write(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    void testMalformedJwtException() throws ServletException, IOException {
        String jwtExceptionMessage = "jwtExceptionMessage";
        when(response.getWriter()).thenReturn(printWriter);
        doThrow(new MalformedJwtException(jwtExceptionMessage)).when(filterChain).doFilter(request, response);

        exceptionHandlerFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
        verify(response).setContentType("application/json");
        ErrorResponse expectedResponse = new ErrorResponse("Invalid JWT token: "+jwtExceptionMessage);
        verify(printWriter).write(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    void testSignatureExceptionException() throws ServletException, IOException {
        String jwtExceptionMessage = "jwtExceptionMessage";
        when(response.getWriter()).thenReturn(printWriter);
        doThrow(new SignatureException(jwtExceptionMessage)).when(filterChain).doFilter(request, response);

        exceptionHandlerFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
        verify(response).setContentType("application/json");
        ErrorResponse expectedResponse = new ErrorResponse("Invalid JWT signature: "+jwtExceptionMessage);
        verify(printWriter).write(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    void testExpiredJwtException() throws ServletException, IOException {
        String jwtExceptionMessage = "jwtExceptionMessage";
        when(response.getWriter()).thenReturn(printWriter);
        doThrow(new ExpiredJwtException(null, null, jwtExceptionMessage)).when(filterChain).doFilter(request, response);

        exceptionHandlerFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
        verify(response).setContentType("application/json");
        ErrorResponse expectedResponse = new ErrorResponse("Expired JWT signature: "+jwtExceptionMessage);
        verify(printWriter).write(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    void test_JwtException() throws ServletException, IOException {
        String jwtExceptionMessage = "jwtExceptionMessage";
        when(response.getWriter()).thenReturn(printWriter);
        doThrow(new JwtException(jwtExceptionMessage)).when(filterChain).doFilter(request, response);

        exceptionHandlerFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
        verify(response).setContentType("application/json");
        ErrorResponse expectedResponse = new ErrorResponse("JWT exception: "+jwtExceptionMessage);
        verify(printWriter).write(objectMapper.writeValueAsString(expectedResponse));
    }
}
