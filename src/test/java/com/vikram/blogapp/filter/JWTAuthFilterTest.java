package com.vikram.blogapp.filter;

import com.vikram.blogapp.entities.Role;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.exception.InvalidAuthHeaderException;
import com.vikram.blogapp.jwt.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JWTAuthFilterTest {
    @Mock
    private MockHttpServletRequest request;

    @Mock
    private MockHttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JWTAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testMissingJWTToken() {
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(InvalidAuthHeaderException.class, () -> {
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    void testEmptyJWTToken() {
        when(request.getHeader("Authorization")).thenReturn("");

        assertThrows(InvalidAuthHeaderException.class, () -> {
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    void testInvalidJWTToken() throws ServletException, java.io.IOException {
        User userDetails = new User();
        userDetails.setEmail("vikram@test.com");
        userDetails.setPassword("test_password");
        userDetails.setRole(Role.ROLE_USER);

        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");
        when(jwtService.isValidToken("invalid_token", userDetails)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    @Test
    public void testValidJWTToken() throws ServletException, java.io.IOException {
        String jwtToken="jwtToken";
        String email="vikram@test.com";

        User userDetails = new User();
        userDetails.setEmail(email);
        userDetails.setPassword("test_password");
        userDetails.setRole(Role.ROLE_USER);

        when(request.getHeader("Authorization")).thenReturn("Bearer "+jwtToken);
        when(jwtService.extractUsername(jwtToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.isValidToken(jwtToken, userDetails)).thenReturn(true);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService).isValidToken(jwtToken, userDetails);
        verify(userDetailsService).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication actualAuthentication = securityContext.getAuthentication();
        assertEquals(authenticationToken.getPrincipal(), actualAuthentication.getPrincipal());
        assertEquals(authenticationToken.getAuthorities(), actualAuthentication.getAuthorities());
    }
}
