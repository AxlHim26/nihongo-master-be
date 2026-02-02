package com.example.japanweb.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.security.auth-cookie")
public class AuthCookieProperties {

    @NotBlank
    private String refreshTokenName = "nm_refresh_token";

    @NotBlank
    private String path = "/api/v1/auth";

    private String domain;

    private boolean httpOnly = true;

    private boolean secure = false;

    @NotBlank
    private String sameSite = "Lax";
}
