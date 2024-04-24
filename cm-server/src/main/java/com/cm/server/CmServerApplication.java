package com.cm.server;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 通信模块服务端
 */
@EnableDiscoveryClient
@SpringBootApplication
@Slf4j
public class CmServerApplication {

    public static void main(String[] args) {
        log.info("******************** [CmServer启动] ********************");
        SpringApplication.run(CmServerApplication.class, args);
        log.info("******************** [CmServer完成] ********************");
    }

}