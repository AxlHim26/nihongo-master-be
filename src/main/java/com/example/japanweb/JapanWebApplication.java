package com.example.japanweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JapanWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(JapanWebApplication.class, args);
    }

}
