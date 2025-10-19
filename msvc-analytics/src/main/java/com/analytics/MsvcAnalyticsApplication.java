package com.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.analytics.client")
@EnableDiscoveryClient
public class MsvcAnalyticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsvcAnalyticsApplication.class, args);
    }
}
