package com.nishant.AuthKit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private String token;
    private Long userId;
    private Instant expiresAt;
    private Instant createdAt;
    private String ipAddress;
    private String userAgent;
}
