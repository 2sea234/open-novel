package com.kxhy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "comfyui")
public class ComfyuiProperties {

    private String baseUrl;
    private String workflowPath;
    private String checkpointName;
    private Integer width = 832;
    private Integer height = 1216;
    private Integer steps = 30;
    private Double cfg = 5.0;
    private String samplerName = "dpm_2";
    private String scheduler = "normal";
    private Long timeoutSeconds = 300L;
    private Long pollIntervalMillis = 1000L;

}
