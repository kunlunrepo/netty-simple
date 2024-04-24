package com.cm.client.config;

import com.cm.client.CmClient;
import com.cm.client.CmClientInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description : 通信客户端配置
 *
 * @author kunlunrepo
 * date :  2024-04-24 13:51
 */
@Configuration
@Slf4j
public class CmClientConfig implements CommandLineRunner {

    @Value("${cm.server.host}")
    private String host;

    @Value("${cm.server.port}")
    private int port;

    @Value("${cm.server.url}")
    private String url;

    /**
     * 客户端
     */
    private CmClient cmClient;

    /**
     * 初始化客户端容器
     */
    @Bean
    public CmClient cmClient() {
        cmClient = new CmClientInitializer(host, port, url);
        return cmClient;
    }

    /**
     * 启动服务
     */
    @Override
    public void run(String... args) throws Exception {
        cmClient.start();
    }

}
