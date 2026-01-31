package com.nishant.AuthKit.security;

import com.nishant.AuthKit.entity.Role;
import com.nishant.AuthKit.entity.User;
import com.nishant.AuthKit.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;
    private final String validToken = "valid.jwt.token";
    private final String validUsername = "testuser";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        
        userDetails = User.builder()
                .id(1L)
                .username(validUsername)
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldAuthenticateWithValidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn(validUsername);
        when(userDetailsService.loadUserByUsername(validUsername)).thenReturn(userDetails);
        when(jwtService.isTokenValid(validToken, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipFilterWhenNoAuthHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipFilterWhenAuthHeaderNotBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipFilterWhenTokenExtractionFails() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenThrow(new RuntimeException("Invalid token"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipFilterWhenUserNotFound() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn(validUsername);
        when(userDetailsService.loadUserByUsername(validUsername)).thenThrow(new RuntimeException("User not found"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipFilterWhenTokenInvalid() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn(validUsername);
        when(userDetailsService.loadUserByUsername(validUsername)).thenReturn(userDetails);
        when(jwtService.isTokenValid(validToken, userDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
