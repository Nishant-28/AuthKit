package com.nishant.AuthKit.config;

import com.nishant.AuthKit.entity.Role;
import com.nishant.AuthKit.entity.User;
import com.nishant.AuthKit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    void shouldCreateUserDetailsService() {
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        assertNotNull(userDetailsService);
    }

    @Test
    void shouldFindUserByUsername() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        var result = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void shouldFindUserByEmail() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        var result = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        // UserDetails.getUsername() returns the username field from User entity, not the email
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nonexistent")).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });
    }

    @Test
    void shouldCreateAuthenticationProvider() {
        AuthenticationProvider provider = applicationConfig.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    void shouldCreatePasswordEncoder() {
        PasswordEncoder encoder = applicationConfig.passwordEncoder();
        assertNotNull(encoder);
        
        String rawPassword = "password123";
        String encoded = encoder.encode(rawPassword);
        assertTrue(encoder.matches(rawPassword, encoded));
    }

    @Test
    void shouldCreateAuthenticationManager() throws Exception {
        AuthenticationManager mockManager = org.mockito.Mockito.mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager manager = applicationConfig.authenticationManager(authenticationConfiguration);
        assertNotNull(manager);
    }
}
