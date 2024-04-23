package com.kl.echo;

import com.kl.echo.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * description : Netty服务端
 *
 * @author kunlunrepo
 * date :  2024-04-23 10:11
 */
@Slf4j
public class EchoServer {

    /**
     * 端口号
     */
    private static final int PORT = 9999;

    public static void main(String[] args) {
        log.info("----------------------- EchoServer服务端启动 -----------------------");
        EchoServer echoServer = new EchoServer();
        echoServer.start();
    }

    /**
     * 启动
     */
    private void start() {
        // 连接线程 (接收新客户端连接请求)
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            // 程序启动组件
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioServerSocketChannel.class) // 使用NIO进行网络传输
                    .localAddress(new InetSocketAddress(PORT)) // 服务器监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler()); // 添加处理器
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync(); // 绑定到服务器
            future.channel().closeFuture().sync(); // 阻塞直到服务器的channel关闭
        } catch (Exception e) {
            e.printStackTrace();
            log.error("EchoServer服务端启动异常", e);
        } finally {
            try {
                bossGroup.shutdownGracefully().sync(); // 关闭
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("EchoServer服务端关闭异常", e);
            }
        }
    }
}
