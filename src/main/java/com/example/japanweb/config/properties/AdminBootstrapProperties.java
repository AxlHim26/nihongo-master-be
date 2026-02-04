package com.example.japanweb.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security.admin-bootstrap")
public class AdminBootstrapProperties {
    /**
     * Secret key required to register the first admin account.
     * If empty, admin registration is disabled.
     */
    private String key = "";
}
