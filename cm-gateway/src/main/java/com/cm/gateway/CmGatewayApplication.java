package com.cm.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * 通信模块网关
 */
@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class CmGatewayApplication {
    public static void main(String[] args) {
        log.info("******************** [CmGateway启动] ********************");
        SpringApplication.run(CmGatewayApplication.class, args);
        log.info("******************** [CmGateway完成] ********************");
    }
}