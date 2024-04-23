package com.kl.ws;

import com.kl.ws.handler.WsServerChannelInit;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * description : WebSocket服务端
 *
 * @author kunlunrepo
 * date :  2024-04-23 13:51
 */
@Slf4j
public class WebSocketServer {

    /**
     * 端口号
     */
    private static final int PORT = 8080;

    /**
     * 是否启用SSL
     */
    private static final boolean SSL = false;

    public static void main(String[] args) {
        log.info("----------------------- WebSocketServer服务端启动 -----------------------");
        WebSocketServer webSocketServer = new WebSocketServer();
        webSocketServer.start();
    }

    /**
     * 启动
     */
    private void start() {
        // 连接线程  (接收新客户端连接请求)
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 工作线程 (处理已接受的客户端连接及其后续的 I/O 事件和业务逻辑)
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            // 处理SSL上下文
            SslContext sslContext = null;
            if (SSL) {
                // 开发环境生成自签名的X.509证书和对应的私钥对
                SelfSignedCertificate cert = new SelfSignedCertificate();
                sslContext = SslContextBuilder
                        .forServer(cert.certificate(), cert.privateKey())
                        .build();
            }
            // 程序启动组件
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 使用NIO进行网络传输
                    .localAddress(new InetSocketAddress(PORT)) // 服务器监听端口
                    .handler(new LoggingHandler(LogLevel.INFO)) // 内置日志处理器
                    .childHandler(new WsServerChannelInit(sslContext));
            ChannelFuture future = bootstrap.bind().sync(); // 绑定到服务器
            future.channel().closeFuture().sync(); // 阻塞直到服务器的channel关闭
        } catch (Exception e) {
            e.printStackTrace();
            log.error("WebSocketServer服务端启动异常", e);
        } finally {
            try {
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync(); // 关闭
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("WebSocketServer服务端关闭异常", e);
            }
        }
    }
}
