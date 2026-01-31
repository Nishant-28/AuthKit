package com.nishant.AuthKit.config;

import com.nishant.AuthKit.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void shouldCreateSecurityConfig() {
        assertNotNull(securityConfig);
        assertNotNull(jwtAuthenticationFilter);
        assertNotNull(authenticationProvider);
    }
}
