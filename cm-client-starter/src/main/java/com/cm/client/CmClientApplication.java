package com.cm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 通信模块客户端
 */
@EnableDiscoveryClient
@SpringBootApplication
@Slf4j
public class CmClientApplication {
    public static void main(String[] args) {
        log.info("******************** [CmClient启动] ********************");
        SpringApplication.run(CmClientApplication.class, args);
        log.info("******************** [CmClient完成] ********************");
    }
}