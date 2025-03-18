package com.yes.interfaces;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.yes.interfaces","com.yes.common"})
@MapperScan(value = {"com.yes.interfaces.mapper","com.yes.common.mapper"})
@EnableDubbo
@EnableDiscoveryClient
public class InterfaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterfaceApplication.class,args);
    }
}
