package com.kxhy.ai;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NovelAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NovelAiApplication.class, args);
    }
}
