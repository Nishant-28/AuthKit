package com.nishant.AuthKit.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseDTOTest {

    @Test
    void shouldCreateAuthResponseWithBuilder() {
        UserResponseDTO userDTO = UserResponseDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        AuthResponseDTO dto = AuthResponseDTO.builder()
                .accessToken("test-token")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(userDTO)
                .build();

        assertNotNull(dto);
        assertEquals("test-token", dto.getAccessToken());
        assertEquals("Bearer", dto.getTokenType());
        assertEquals(3600L, dto.getExpiresIn());
        assertNotNull(dto.getUser());
        assertEquals("testuser", dto.getUser().getUsername());
    }

    @Test
    void shouldCreateAuthResponseWithNullValues() {
        AuthResponseDTO dto = AuthResponseDTO.builder().build();

        assertNotNull(dto);
        assertNull(dto.getAccessToken());
        assertNull(dto.getTokenType());
        assertNull(dto.getExpiresIn());
        assertNull(dto.getUser());
    }
}
