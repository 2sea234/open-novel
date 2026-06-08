package com.kxhy.admin;


import com.opennovel.common.jwt.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.kxhy", "com.opennovel.common"})
@EnableConfigurationProperties(JwtProperties.class)
@MapperScan("com.kxhy.admin.mapper")
@EnableDiscoveryClient
public class AdminSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminSystemApplication.class, args);
    }
}
