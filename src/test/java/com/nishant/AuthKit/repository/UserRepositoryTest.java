package com.nishant.AuthKit.repository;

import com.nishant.AuthKit.entity.Role;
import com.nishant.AuthKit.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "jwt.secret=testsecretkeythatisatleast32characterslong",
    "jwt.expiration=86400000"
})
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUsername() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void shouldFindUserByEmail() {
        User user = User.builder()
                .username("testuser2")
                .email("test2@example.com")
                .password("password123")
                .role(Role.USER)
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test2@example.com");

        assertTrue(found.isPresent());
        assertEquals("test2@example.com", found.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void shouldSaveUserSuccessfully() {
        User user = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .role(Role.ADMIN)
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertEquals(Role.ADMIN, saved.getRole());
    }
}
