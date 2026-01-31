package com.nishant.AuthKit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateValidDTO() {
        UserRegistrationRequestDTO dto = UserRegistrationRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldInvalidateBlankUsername() {
        UserRegistrationRequestDTO dto = UserRegistrationRequestDTO.builder()
                .username("")
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username is Required")));
    }

    @Test
    void shouldInvalidateShortUsername() {
        UserRegistrationRequestDTO dto = UserRegistrationRequestDTO.builder()
                .username("abc")
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 4 and 20 characters")));
    }

    @Test
    void shouldInvalidateLongUsername() {
        UserRegistrationRequestDTO dto = UserRegistrationRequestDTO.builder()
                .username("thisusernameiswaytoolongtobevalid")
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 4 and 20 characters")));
    }

    @Test
    void shouldInvalidateBlankEmail() {
        UserRegistrationRequestDTO dto = UserRegistrationRequestDTO.builder()
                .username("testuser")
                .email("")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is Required")));
    }

    @Test
    void shouldInvalidateInvalidEmail() {
        UserRegistrationRequestDTO dto = UserRegistrationRequestDTO.builder()
                .username("testuser")
                .email("invalid-email")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid Email")));
    }

    @Test
    void shouldInvalidateBlankPassword() {
        UserRegistrationRequestDTO dto = UserRegistrationRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("")
                .build();

        Set<ConstraintViolation<UserRegistrationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is Required")));
    }

    @Test
    void shouldInvalidateShortPassword() {
        UserRegistrationRequestDTO dto = UserRegistrationRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("short")
                .build();

        Set<ConstraintViolation<UserRegistrationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("at least 8 characters")));
    }
}
