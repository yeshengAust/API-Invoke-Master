package com.yes.permission;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.yes.permission","com.yes.common"})
@MapperScan(value = {"com.yes.permission.mapper","com.yes.common.mapper"})
@EnableDubbo
@EnableDiscoveryClient
public class PermissionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PermissionApplication.class,args);
    }
}
