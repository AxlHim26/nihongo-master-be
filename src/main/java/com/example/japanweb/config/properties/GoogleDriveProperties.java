package com.example.japanweb.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.integrations.google-drive")
public class GoogleDriveProperties {

    private String applicationName = "Japience";
    private String credentialsPath;
}
