package com.example.japanweb.config.properties;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.observability.sql")
public class SqlObservabilityProperties {

    private boolean enabled = true;

    private boolean slowQueryLogEnabled = true;

    @Min(1)
    private long slowQueryThresholdMs = 300;

    @Min(120)
    private int maxSqlLength = 800;
}
