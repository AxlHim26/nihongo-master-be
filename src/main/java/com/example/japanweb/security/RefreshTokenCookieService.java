package com.example.japanweb.security;

import com.example.japanweb.config.properties.AuthCookieProperties;
import com.example.japanweb.config.properties.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieService {

    private final AuthCookieProperties authCookieProperties;
    private final JwtProperties jwtProperties;

    public ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(authCookieProperties.getRefreshTokenName(), refreshToken)
                .httpOnly(authCookieProperties.isHttpOnly())
                .secure(authCookieProperties.isSecure())
                .sameSite(authCookieProperties.getSameSite())
                .path(authCookieProperties.getPath())
                .maxAge(jwtProperties.getRefreshTokenExpiration());

        if (StringUtils.hasText(authCookieProperties.getDomain())) {
            builder.domain(authCookieProperties.getDomain());
        }

        return builder.build();
    }

    public ResponseCookie buildClearRefreshTokenCookie() {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(authCookieProperties.getRefreshTokenName(), "")
                .httpOnly(authCookieProperties.isHttpOnly())
                .secure(authCookieProperties.isSecure())
                .sameSite(authCookieProperties.getSameSite())
                .path(authCookieProperties.getPath())
                .maxAge(0);

        if (StringUtils.hasText(authCookieProperties.getDomain())) {
            builder.domain(authCookieProperties.getDomain());
        }

        return builder.build();
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> authCookieProperties.getRefreshTokenName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .findFirst();
    }
}
