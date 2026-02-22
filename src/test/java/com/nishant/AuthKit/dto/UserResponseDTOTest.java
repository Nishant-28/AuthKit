package com.nishant.AuthKit.dto;

import com.nishant.AuthKit.entity.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseDTOTest {

    @Test
    void shouldCreateUserResponseWithBuilder() {
        LocalDateTime now = LocalDateTime.now();

        UserResponseDTO dto = UserResponseDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .name("Test User")
                .role(Role.USER)
                .profilePicUrl("http://example.com/pic.jpg")
                .createdAt(now)
                .updatedAt(now)
                .lastLoginAt(now)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("Test User", dto.getName());
        assertEquals(Role.USER, dto.getRole());
        assertEquals("http://example.com/pic.jpg", dto.getProfilePicUrl());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
        assertEquals(now, dto.getLastLoginAt());
    }

    @Test
    void shouldCreateUserResponseWithNullValues() {
        UserResponseDTO dto = UserResponseDTO.builder().build();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getUsername());
        assertNull(dto.getEmail());
        assertNull(dto.getName());
        assertNull(dto.getRole());
        assertNull(dto.getProfilePicUrl());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getLastLoginAt());
    }
}
