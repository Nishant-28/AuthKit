package com.nishant.AuthKit.service;

import com.nishant.AuthKit.entity.Role;
import com.nishant.AuthKit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User userDetails;
    private final String testSecret = "thisIsAVeryLongSecretKeyForTestingPurposes12345678901234567890";
    private final long testExpiration = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);
        jwtService.init();

        userDetails = User.builder()
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
    }

    @Test
    void shouldGenerateTokenSuccessfully() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(userDetails.getUsername(), extractedUsername);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void shouldInvalidateTokenForDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        
        User differentUser = User.builder()
                .id(2L)
                .username("differentuser")
                .email("different@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    void shouldThrowExceptionForShortSecretKey() {
        JwtService newService = new JwtService();
        ReflectionTestUtils.setField(newService, "secretKey", "short");
        ReflectionTestUtils.setField(newService, "jwtExpiration", testExpiration);

        assertThrows(IllegalArgumentException.class, () -> newService.init());
    }

    @Test
    void shouldExtractExpirationDate() {
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractClaim(token, claims -> claims.getExpiration());

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void shouldExtractAuthoritiesFromToken() {
        String token = jwtService.generateToken(userDetails);
        var authorities = jwtService.extractClaim(token, claims -> claims.get("authorities"));

        assertNotNull(authorities);
    }
}
