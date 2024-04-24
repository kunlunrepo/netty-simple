package com.cm.server.netty;

import com.cm.server.netty.handler.server.WsServerChannelInit;
import com.cm.server.service.NettyCacheService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * description : 通信模块服务端入口
 *
 * @author kunlunrepo
 * date :  2024-04-24 10:09
 */
@Configuration
@Slf4j
public class CmServerInitializer implements CommandLineRunner {

    @Value("${cm.server.port:9000}")
    private int CM_SERVER_PORT;

    /**
     * 服务器channel
     */
    private Channel serverChannel;

    /**
     * netty缓存服务
     */
    @Autowired
    private NettyCacheService nettyCacheService;

    /**
     * 通道初始化
     * 说明：统一交给spring管理是为了在其中使用spring的bean
     */
    @Autowired
    private WsServerChannelInit wsServerChannelInit;


    /**
     * 程序启动后执行
     * 说明：启动Netty服务
     */
    @Override
    public void run(String... args) throws Exception {
        this.start();
    }

    /**
     * 启动
     */
    private void start() {
        log.info("******************** [通信模块服务端]启动 ********************");
        log.info("[通信模块服务端] port={}", CM_SERVER_PORT);
        // 连接线程  (接收新客户端连接请求)
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 工作线程 (处理已接受的客户端连接及其后续的 I/O 事件和业务逻辑)
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            // 处理SSL上下文
            // 程序启动组件
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 使用NIO进行网络传输
                    .localAddress(new InetSocketAddress(CM_SERVER_PORT)) // 服务器监听端口
                    .handler(new LoggingHandler(LogLevel.INFO)) // 内置日志处理器
                    .childHandler(wsServerChannelInit);
            ChannelFuture future = bootstrap.bind().sync(); // 绑定到服务器
            String serverIp = InetAddress.getLocalHost().getHostAddress() +":"+ CM_SERVER_PORT;
            // 服务器启动后的操作
            startAfter(serverIp);
            log.info("******************** [通信模块服务端] "+serverIp+" 启动成功 ********************");
            serverChannel = future.channel();
            serverChannel.closeFuture().sync(); // 阻塞直到服务器的channel关闭


        } catch (Exception e) {
            e.printStackTrace();
            log.error("[通信模块服务端]启动异常", e);
        } finally {
            try {
                // 关闭
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync(); // 关闭
                log.info("******************** [通信模块服务端]关闭 ********************");
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("[通信模块服务端]关闭异常", e);
            }
        }
    }

    /**
     * 服务启动后
     */
    private void startAfter(String serverIp) {
        // 上报当前服务器地址
        CompletableFuture.supplyAsync(() -> {
            nettyCacheService.addServerAddress(serverIp);
            return null;
        }).handle((result, ex)  -> {
            if (null != ex) {
                log.error("[通信模块服务端] 上报当前服务器地址异常 ", ex);
            }
            return null;
        });
    }

}
