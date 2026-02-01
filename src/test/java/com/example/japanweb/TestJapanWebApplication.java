package com.example.japanweb;

import org.springframework.boot.SpringApplication;

public class TestJapanWebApplication {

    public static void main(String[] args) {
        SpringApplication.from(JapanWebApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
