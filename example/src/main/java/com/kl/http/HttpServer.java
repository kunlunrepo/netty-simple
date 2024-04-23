package com.kl.http;

import com.kl.http.handler.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * description : Netty的Http服务端
 *
 * @author kunlunrepo
 * date :  2024-04-23 11:24
 */
@Slf4j
public class HttpServer {

    /**
     * 端口号
     */
    private static final int PORT = 8080;

    public static void main(String[] args) {
        log.info("----------------------- HttpServer服务端启动 -----------------------");
        HttpServer httpServer = new HttpServer();
        httpServer.start();
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
            // 程序启动组件
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 使用NIO进行网络传输
                    .localAddress(new InetSocketAddress(PORT)) // 服务器监听端口
                    .handler(new LoggingHandler(LogLevel.INFO)) // 内置日志处理器
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new HttpServerCodec()); // http解码处理器
                            ch.pipeline().addLast(new HttpServerExpectContinueHandler()); // 处理客户端在发送带有Expect: 100-continue请求头的HTTP请求时的交互流程
                            ch.pipeline().addLast(new HttpServerHandler()); // Http自定义处理器
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync(); // 绑定到服务器
            future.channel().closeFuture().sync(); // 阻塞直到服务器的channel关闭
        } catch (Exception e) {
            e.printStackTrace();
            log.error("HttpServer服务端启动异常", e);
        } finally {
            try {
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync(); // 关闭
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("HttpServer服务端关闭异常", e);
            }
        }
    }
}
