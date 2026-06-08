package com.kxhy;

import com.opennovel.common.jwt.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;


@SpringBootApplication(scanBasePackages = {"com.kxhy", "com.opennovel.common"})
@MapperScan("com.kxhy.mapper")
@EnableConfigurationProperties(JwtProperties.class)
@EnableFeignClients
@EnableDiscoveryClient
public class NovelAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(NovelAdminApplication.class, args);
    }

    @Bean
    public ApplicationRunner checkConfig(Environment environment) {
        return args -> {
            System.out.println("debug.config-source-test = " + environment.getProperty("debug.config-source-test"));
            System.out.println("spring.datasource.url = " + environment.getProperty("spring.datasource.url"));
            System.out.println("jwt.secret exists = " + environment.containsProperty("jwt.secret"));
        };
    }

}



