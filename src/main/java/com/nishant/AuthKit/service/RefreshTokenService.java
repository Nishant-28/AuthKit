package com.nishant.AuthKit.service;

import com.nishant.AuthKit.dto.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String createRefreshToken(Long userId, String ipAddress, String userAgent) {
        String token = UUID.randomUUID().toString();
        Instant createdAt = Instant.now();
        Instant expiresAt = createdAt.plusMillis(refreshTokenExpiration);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userId(userId)
                .expiresAt(expiresAt)
                .createdAt(createdAt)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        String key = "refresh_token:" + token;

        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        log.info("Created refresh token for User: {}", userId);
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        String key = "refresh_token:" + token;
        RefreshToken refreshToken = (RefreshToken) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(refreshToken);
    }

    public boolean validateRefreshToken(String token, Long userId) {
        Optional<RefreshToken> storedTokenOpt = findByToken(token);

        if (storedTokenOpt.isEmpty()) {
            log.warn("Refresh token not found in Redis: token ending: ...{})", token.substring(token.length() - 4));
            return false;
        }

        RefreshToken storedToken = storedTokenOpt.get();

        if (!storedToken.getUserId().equals(userId)) {
            log.warn("User ID mismatch for refresh token. Expected: {}, Actual: {}", storedToken.getUserId(), userId);
            return false;
        }

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Refresh token expired for user: {}", userId);
            return false;
        }

        return true;
    }

    public boolean deleteRefreshToken(String token) {
        String key = "refresh_token:" + token;
        boolean res = redisTemplate.delete(key);
        log.info("Deleted refresh token from Redis (token ending: ...{})", token.substring(token.length() - 4));
        return res;
    }

//    public void deleteAllUserTokens(Long userId) {
//
//    }
}
