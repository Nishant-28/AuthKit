package com.nishant.AuthKit.service;

import com.nishant.AuthKit.dto.AuthResponseDTO;
import com.nishant.AuthKit.dto.LoginRequestDTO;
import com.nishant.AuthKit.dto.UserRegistrationRequestDTO;
import com.nishant.AuthKit.dto.UserResponseDTO;
import com.nishant.AuthKit.entity.Role;
import com.nishant.AuthKit.entity.User;
import com.nishant.AuthKit.exception.EmailAlreadyExistsException;
import com.nishant.AuthKit.exception.UsernameAlreadyExistsException;
import com.nishant.AuthKit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register Method for AuthService
     */
    @Transactional
    public AuthResponseDTO register(UserRegistrationRequestDTO request) {
        log.info("Attempting to register user: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed: Username {} already exists", request.getUsername());
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User createdUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .lastLoginAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(createdUser);
        log.info("User registered successfully: {}", savedUser.getUsername());

        String token = jwtService.generateToken(createdUser);
        return buildAuthResponse(savedUser, token);
    }

    /**
     * Login Method for AuthService
     */
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Attempting login for: {}", request.getUsernameOrEmail());
        try {
            /**
             * Why? We are wrapping the raw credentials (username + password from the
             * request) into an object that Spring Security understands.
             * At this point, this object is unauthenticated. It just holds data.
             */
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    request.getUsernameOrEmail(),
                    request.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            User user = userRepository.findByEmail(request.getUsernameOrEmail())
                    .orElseThrow(() -> new EmailAlreadyExistsException("User not found with email: " + request.getUsernameOrEmail()));

            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            String token = jwtService.generateToken(user);

            log.info("User logged in successfully: {}", user.getUsername());
            return buildAuthResponse(user, token);
        } catch (AuthenticationException e) {
            log.error("Login failed for User: {}", request.getUsernameOrEmail());
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    /**
     * Helper method to build AuthResponseDTO
     */
    private AuthResponseDTO buildAuthResponse(User user, String token) {
        AuthResponseDTO auth = AuthResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration())
                .user(mapToUserResponseDTO(user))
                .build();
        return auth;
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .profilePicUrl(user.getProfilePicUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
