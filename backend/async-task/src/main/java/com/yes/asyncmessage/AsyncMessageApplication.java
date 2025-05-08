package com.yes.asyncmessage;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.yes.common","com.yes.asyncmessage"})
@MapperScan(basePackages = {"com.yes.common.mapper"})
@EnableDubbo
@EnableDiscoveryClient
public class AsyncMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(AsyncMessageApplication.class,args);
    }
}
