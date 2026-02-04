package com.example.japanweb.security;

import com.example.japanweb.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String CLAIM_TOKEN_TYPE = "token_type";

    private final JwtProperties jwtProperties;
    private SecretKey signInKey;

    @PostConstruct
    public void init() {
        this.signInKey = Keys.hmacShaKeyFor(resolveSigningKeyBytes(jwtProperties.getSecretKey()));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateAccessToken(UserDetails userDetails, String tokenId) {
        return generateToken(new HashMap<>(), userDetails, jwtProperties.getAccessTokenExpiration(), tokenId, TokenType.ACCESS);
    }

    public String generateAccessToken(UserDetails userDetails, String tokenId, Map<String, Object> extraClaims) {
        return generateToken(extraClaims, userDetails, jwtProperties.getAccessTokenExpiration(), tokenId, TokenType.ACCESS);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtProperties.getRefreshTokenExpiration(), null, TokenType.REFRESH);
    }

    public String generateRefreshToken(UserDetails userDetails, String tokenId) {
        return generateToken(new HashMap<>(), userDetails, jwtProperties.getRefreshTokenExpiration(), tokenId, TokenType.REFRESH);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return generateToken(extraClaims, userDetails, jwtProperties.getAccessTokenExpiration());
    }

    private String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Duration expiration
    ) {
        return generateToken(extraClaims, userDetails, expiration, null, TokenType.ACCESS);
    }

    private String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Duration expiration,
            String tokenId,
            TokenType tokenType
    ) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                .setId(tokenId)
                .claim(CLAIM_TOKEN_TYPE, tokenType.name())
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public boolean isAccessToken(String token) {
        return extractTokenType(token) == TokenType.ACCESS;
    }

    public boolean isRefreshToken(String token) {
        return extractTokenType(token) == TokenType.REFRESH;
    }

    public Duration getAccessTokenExpiration() {
        return jwtProperties.getAccessTokenExpiration();
    }

    public Duration getRefreshTokenExpiration() {
        return jwtProperties.getRefreshTokenExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signInKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private TokenType extractTokenType(String token) {
        String rawType = extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
        if (rawType == null) {
            throw new JwtException("Token type is missing");
        }
        try {
            return TokenType.valueOf(rawType);
        } catch (IllegalArgumentException ex) {
            throw new JwtException("Token type is invalid", ex);
        }
    }

    private byte[] resolveSigningKeyBytes(String rawSecret) {
        try {
            return Decoders.BASE64.decode(rawSecret);
        } catch (IllegalArgumentException ex) {
            return rawSecret.getBytes(StandardCharsets.UTF_8);
        }
    }

    private enum TokenType {
        ACCESS,
        REFRESH
    }
}
