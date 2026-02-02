package com.example.japanweb.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AuthTokenStore {

    private static final String ACCESS_PREFIX = "auth:access:";
    private static final String REFRESH_PREFIX = "auth:refresh:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void storeAccessToken(String tokenId, String username, Duration ttl) {
        redisTemplate.opsForValue().set(accessKey(tokenId), username, ttl);
    }

    public void storeRefreshToken(String tokenId, String username, Duration ttl) {
        redisTemplate.opsForValue().set(refreshKey(tokenId), username, ttl);
    }

    public boolean isAccessTokenActive(String tokenId, String username) {
        Object value = redisTemplate.opsForValue().get(accessKey(tokenId));
        return username.equals(value);
    }

    public boolean isRefreshTokenActive(String tokenId, String username) {
        Object value = redisTemplate.opsForValue().get(refreshKey(tokenId));
        return username.equals(value);
    }

    public void revokeAccessToken(String tokenId) {
        redisTemplate.delete(accessKey(tokenId));
    }

    public void revokeRefreshToken(String tokenId) {
        redisTemplate.delete(refreshKey(tokenId));
    }

    private String accessKey(String tokenId) {
        return ACCESS_PREFIX + tokenId;
    }

    private String refreshKey(String tokenId) {
        return REFRESH_PREFIX + tokenId;
    }
}
