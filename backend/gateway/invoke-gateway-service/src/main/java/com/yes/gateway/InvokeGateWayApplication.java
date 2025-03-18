package com.yes.gateway;

import com.yes.common.config.WebConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@EnableDubbo
@ComponentScan(basePackages = {"com.yes.common","com.yes.gateway"},
        excludeFilters = {
                @ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.REGEX, pattern = "com.yes.common.config.security.*")
        })

public class InvokeGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(InvokeGateWayApplication.class,args);

    }
}
