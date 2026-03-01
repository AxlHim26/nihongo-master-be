package com.example.japanweb.service;

import com.example.japanweb.dto.request.auth.AuthRequest;
import com.example.japanweb.dto.request.auth.RegisterRequest;
import com.example.japanweb.entity.User;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.repository.UserRepository;
import com.example.japanweb.security.AuthTokenStore;
import com.example.japanweb.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthTokenStore authTokenStore;
    private final AuthenticationManager authenticationManager;

    public IssuedTokens register(RegisterRequest request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new ApiException(ErrorCode.AUTH_USERNAME_EXISTS);
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.AUTH_EMAIL_EXISTS);
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();
        repository.save(user);
        return issueTokens(user);
    }

    public IssuedTokens authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_INVALID_CREDENTIALS));
        return issueTokens(user);
    }

    public IssuedTokens refresh(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        if (username == null) {
            throw new ApiException(ErrorCode.AUTH_TOKEN_INVALID, "Refresh token subject is missing");
        }

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_USER_NOT_FOUND));

        if (!jwtService.isRefreshToken(refreshToken) || !jwtService.isTokenValid(refreshToken, user)) {
            throw new ApiException(ErrorCode.AUTH_TOKEN_INVALID, "Refresh token is invalid");
        }

        String refreshTokenId = jwtService.extractTokenId(refreshToken);
        if (refreshTokenId == null || !authTokenStore.isRefreshTokenActive(refreshTokenId, username)) {
            throw new ApiException(ErrorCode.AUTH_TOKEN_INVALID, "Refresh token is revoked or expired");
        }

        // Rotate refresh token to prevent replay.
        authTokenStore.revokeRefreshToken(refreshTokenId);
        return issueTokens(user);
    }

    public void logout(String accessToken, String refreshToken) {
        revokeAccessToken(accessToken);
        revokeRefreshToken(refreshToken);
    }

    private IssuedTokens issueTokens(User user) {
        String accessTokenId = UUID.randomUUID().toString();
        String refreshTokenId = UUID.randomUUID().toString();

        Map<String, Object> accessClaims = new HashMap<>();
        accessClaims.put("email", user.getEmail());
        accessClaims.put("role", user.getRole().name());
        String accessToken = jwtService.generateAccessToken(user, accessTokenId, accessClaims);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenId);

        authTokenStore.storeAccessToken(accessTokenId, user.getUsername(), jwtService.getAccessTokenExpiration());
        authTokenStore.storeRefreshToken(refreshTokenId, user.getUsername(), jwtService.getRefreshTokenExpiration());

        return new IssuedTokens(accessToken, refreshToken);
    }

    public record IssuedTokens(String accessToken, String refreshToken) {
    }

    private void revokeAccessToken(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return;
        }
        try {
            String accessTokenId = jwtService.extractTokenId(accessToken);
            if (accessTokenId != null) {
                authTokenStore.revokeAccessToken(accessTokenId);
            }
        } catch (RuntimeException ignored) {
            // Ignore malformed or expired access token during logout.
        }
    }

    private void revokeRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        try {
            String refreshTokenId = jwtService.extractTokenId(refreshToken);
            if (refreshTokenId != null) {
                authTokenStore.revokeRefreshToken(refreshTokenId);
            }
        } catch (RuntimeException ignored) {
            // Ignore malformed or expired refresh token during logout.
        }
    }
}
