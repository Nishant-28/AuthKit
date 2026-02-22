package com.nishant.AuthKit.controller;

import com.nishant.AuthKit.dto.AuthResponseDTO;
import com.nishant.AuthKit.dto.LoginRequestDTO;
import com.nishant.AuthKit.dto.RefreshTokenRequestDTO;
import com.nishant.AuthKit.dto.UserRegistrationRequestDTO;
import com.nishant.AuthKit.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody UserRegistrationRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        log.info("Registration request for username: {}", request.getUsername());

        AuthResponseDTO response = authService.register(request, ipAddress, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        log.info("Login request for username: {}", request.getUsernameOrEmail());

        AuthResponseDTO response = authService.login(request, ipAddress, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponseDTO response = authService.refreshToken(request, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Boolean> logout(
            @Valid @RequestBody RefreshTokenRequestDTO request
    ) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
