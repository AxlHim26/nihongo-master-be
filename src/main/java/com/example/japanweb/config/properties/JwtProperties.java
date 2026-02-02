package com.example.japanweb.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    @NotBlank
    private String secretKey;

    @NotNull
    private Duration accessTokenExpiration = Duration.ofHours(24);

    @NotNull
    private Duration refreshTokenExpiration = Duration.ofDays(7);
}
