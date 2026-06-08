package com.kxhy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "admin-system")
public class AdminSystemProperties {

    private String baseUrl;

}
