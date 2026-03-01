package com.example.japanweb.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security.admin-seed")
public class AdminSeedProperties {
    private String username = "";
    private String email = "";
    private String password = "";
}
