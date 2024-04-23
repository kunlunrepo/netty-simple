package com.kl.echo;

import com.kl.echo.handler.EchoClientHandler;
import com.kl.echo.handler.EchoServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * description : Netty客户端
 *
 * @author kunlunrepo
 * date :  2024-04-23 10:11
 */
@Slf4j
public class EchoClient {

    /**
     * IP
     */
    private static final String HOST = "127.0.0.1";

    /**
     * 端口号
     */
    private static final int PORT = 9999;

    public static void main(String[] args) {
        log.info("----------------------- EchoClient客户端启动 -----------------------");
        EchoClient echoClient = new EchoClient();
        echoClient.start();
    }

    /**
     * 启动
     */
    private void start() {
        // 工作线程
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            // 启动线程
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class) // 使用NIO进行网络传输
                    .remoteAddress(new InetSocketAddress(HOST, PORT)) // 设置服务器地址和端口号
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler()); // 添加处理器
                        }
                    });
            // 连接服务器
            ChannelFuture future = bootstrap.connect().sync();
            // 阻塞直到服务器的channel关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("EchoClient客户端启动异常", e);
        } finally {
            try {
                bossGroup.shutdownGracefully().sync(); // 关闭
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("EchoClient客户端关闭异常", e);
            }
        }
    }
}
