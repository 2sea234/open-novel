package com.kxhy.ai.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ollama")
@Data
public class OllamaProperties {

    private String baseUrl;
    private String model;

}
