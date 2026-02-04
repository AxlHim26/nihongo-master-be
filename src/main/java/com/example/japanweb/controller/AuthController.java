package com.example.japanweb.controller;

import com.example.japanweb.dto.common.ApiResponse;
import com.example.japanweb.config.properties.AdminBootstrapProperties;
import com.example.japanweb.dto.request.auth.AuthRequest;
import com.example.japanweb.dto.request.auth.RegisterAdminRequest;
import com.example.japanweb.dto.response.auth.AuthResponse;
import com.example.japanweb.dto.request.auth.RegisterRequest;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.security.RefreshTokenCookieService;
import com.example.japanweb.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;
    private final RefreshTokenCookieService refreshTokenCookieService;
    private final AdminBootstrapProperties adminBootstrapProperties;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthenticationService.IssuedTokens issuedTokens = service.register(request);
        ApiResponse<AuthResponse> response = ApiResponse.created(AuthResponse.builder()
                .token(issuedTokens.accessToken())
                .build());
        return withRefreshCookie(response, issuedTokens.refreshToken(), HttpStatus.CREATED);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        AuthenticationService.IssuedTokens issuedTokens = service.registerAdmin(
                request,
                adminBootstrapProperties.getKey()
        );
        ApiResponse<AuthResponse> response = ApiResponse.created(AuthResponse.builder()
                .token(issuedTokens.accessToken())
                .build());
        return withRefreshCookie(response, issuedTokens.refreshToken(), HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@Valid @RequestBody AuthRequest request) {
        AuthenticationService.IssuedTokens issuedTokens = service.authenticate(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                AuthResponse.builder().token(issuedTokens.accessToken()).build(),
                "Authentication successful"
        );
        return withRefreshCookie(response, issuedTokens.refreshToken(), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(HttpServletRequest request) {
        String refreshToken = refreshTokenCookieService.extractRefreshToken(request)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_TOKEN_INVALID, "Refresh token cookie is missing"));

        AuthenticationService.IssuedTokens issuedTokens = service.refresh(refreshToken);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                AuthResponse.builder().token(issuedTokens.accessToken()).build(),
                "Token refreshed successfully"
        );
        return withRefreshCookie(response, issuedTokens.refreshToken(), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String accessToken = extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        String refreshToken = refreshTokenCookieService.extractRefreshToken(request).orElse(null);
        service.logout(accessToken, refreshToken);
        ApiResponse<Void> response = ApiResponse.success(null, "Logout successful");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieService.buildClearRefreshTokenCookie().toString())
                .body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> withRefreshCookie(
            ApiResponse<T> response,
            String refreshToken,
            HttpStatus status
    ) {
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieService.buildRefreshTokenCookie(refreshToken).toString())
                .body(response);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }
}
