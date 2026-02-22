package com.nishant.AuthKit.service;

import com.nishant.AuthKit.dto.*;
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

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    /**
     * Register Method for AuthService
     */
    @Transactional
    public AuthResponseDTO register(UserRegistrationRequestDTO request, String ipAddress, String userAgent) {
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

        String accessToken = jwtService.generateToken(createdUser);
        String refreshToken = refreshTokenService.createRefreshToken(savedUser.getId(), ipAddress,userAgent);
        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }

    /**
     * Login Method for AuthService
     */
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request, String ipAddress, String userAgent) {
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

            String accessToken = jwtService.generateToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(user.getId(), ipAddress, userAgent);

            log.info("User logged in successfully: {}", user.getUsername());
            return buildAuthResponse(user, accessToken, refreshToken);
        } catch (AuthenticationException e) {
            log.error("Login failed for User: {}", request.getUsernameOrEmail());
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Transactional
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request, String ipAddress, String userAgent) {
        String currRefreshToken = request.getRefreshToken();

        RefreshToken storedToken = refreshTokenService.findByToken(currRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Refresh Token"));

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenService.deleteRefreshToken(currRefreshToken);
            throw new IllegalArgumentException("Refresh token has expired. Please sign in again");
        }

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User associated with token not found"));

        refreshTokenService.deleteRefreshToken(currRefreshToken);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user.getId(), ipAddress, userAgent);

        log.info("Successfully rotated refresh token for user: {}", user.getUsername());
        return buildAuthResponse(user, accessToken, refreshToken);
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            boolean res = refreshTokenService.deleteRefreshToken(refreshToken);
            if (res) {
                log.info("Successfully logged out and deleted refresh token.");
            } else {
                log.warn("Logout called but refresh token was not found in Redis.");
            }
        }
    }

//    public void logoutAllDevices(Long userId) {}

    /**
     * Helper method to build AuthResponseDTO
     */
    private AuthResponseDTO buildAuthResponse(User user, String accessToken, String refreshToken) {
        AuthResponseDTO auth = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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
