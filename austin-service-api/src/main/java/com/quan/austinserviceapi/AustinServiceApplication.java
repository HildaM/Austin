package com.quan.austinserviceapi;

import com.dtp.core.spring.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDynamicTp
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.quan")
public class AustinServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AustinServiceApplication.class, args);
    }
}
