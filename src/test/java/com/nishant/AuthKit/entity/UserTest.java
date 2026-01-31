package com.nishant.AuthKit.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithBuilder() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .name("Test User")
                .phoneNumber("1234567890")
                .profilePicUrl("http://example.com/pic.jpg")
                .bio("Test bio")
                .enabled(true)
                .locked(false)
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(Role.USER, user.getRole());
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void shouldReturnCorrectAuthorities() {
        User user = User.builder()
                .username("testuser")
                .role(Role.USER)
                .build();

        Collection<?> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void shouldReturnAdminAuthorities() {
        User user = User.builder()
                .username("admin")
                .role(Role.ADMIN)
                .build();

        Collection<?> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void shouldReturnCorrectPassword() {
        User user = User.builder()
                .username("testuser")
                .password("secretpassword")
                .build();

        assertEquals("secretpassword", user.getPassword());
    }

    @Test
    void shouldAccountNeverExpire() {
        User user = User.builder().build();
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void shouldAccountBeNonLockedWhenLockedFalse() {
        User user = User.builder()
                .locked(false)
                .build();
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void shouldAccountBeLockedWhenLockedTrue() {
        User user = User.builder()
                .locked(true)
                .build();
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void shouldCredentialsNeverExpire() {
        User user = User.builder().build();
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void shouldBeEnabledWhenEnabledTrue() {
        User user = User.builder()
                .enabled(true)
                .build();
        assertTrue(user.isEnabled());
    }

    @Test
    void shouldBeDisabledWhenEnabledFalse() {
        User user = User.builder()
                .enabled(false)
                .build();
        assertFalse(user.isEnabled());
    }

    @Test
    void shouldHaveDefaultEnabledTrue() {
        User user = User.builder().build();
        assertTrue(user.isEnabled());
    }

    @Test
    void shouldHaveDefaultLockedFalse() {
        User user = User.builder().build();
        assertTrue(user.isAccountNonLocked());
    }
}
