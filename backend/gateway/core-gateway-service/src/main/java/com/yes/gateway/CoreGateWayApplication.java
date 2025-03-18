package com.yes.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CoreGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreGateWayApplication.class,args);
    }
}
