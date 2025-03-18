package com.yes.buy;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@EnableDubbo
@ComponentScan(basePackages = {"com.yes.common", "com.yes.buy"},
        excludeFilters = {
                @ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.REGEX, pattern = "com.yes.common.config.security.*")
        })
public class BuyApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuyApplication.class, args);
    }
}
