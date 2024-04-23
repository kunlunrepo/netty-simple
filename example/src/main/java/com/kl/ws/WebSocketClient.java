package com.kl.ws;

import com.kl.ws.handler.WsClientHandshakeHandler;
import com.kl.ws.handler.WsClientMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * description : WebSocket客户端
 *
 * @author kunlunrepo
 * date :  2024-04-23 10:11
 */
@Slf4j
public class WebSocketClient {

    /**
     * IP
     */
    private static final String HOST = "127.0.0.1";

    /**
     * 端口号
     */
    private static final int PORT = 8080;

    /**
     * 服务端连接地址
     */
    private static final String WS_URL = "ws://localhost:8080/ws";

    public static void main(String[] args) {
        log.info("----------------------- WebSocketClient客户端启动 -----------------------");
        WebSocketClient webSocketClient = new WebSocketClient();
        webSocketClient.start();
    }

    /**
     * 启动
     */
    private void start() {
        // 内置握手工具
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                URI.create(WS_URL),
                WebSocketVersion.V13,
                null,
                true,
                new DefaultHttpHeaders());
        // 自定义握手处理器
        WsClientHandshakeHandler handshakeHandler = new WsClientHandshakeHandler(handshaker);
        // 工作线程
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            // 启动线程
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class) // 使用NIO进行网络传输
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加心跳检测处理器 (用于检测和管理网络连接空闲状态的关键组件。开发者可以实现心跳检测、资源清理、超时处理等功能)
                            pipeline.addLast(new IdleStateHandler(0, 20, 60));
                            // 添加HTTP客户端编解码器
                            pipeline.addLast(new HttpClientCodec());
                            // 添加HTTP聚合器(指定最大聚合字节数)
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            // 添加自定义客户端握手处理器
                            pipeline.addLast(handshakeHandler);
                            // 添加自定义客户端消息处理器
                            pipeline.addLast(new WsClientMessageHandler());
                        }
                    });
            // 连接服务器 (channel可以和服务器进行通信)
            Channel channel = bootstrap.connect(HOST, PORT).sync().channel();
            // 阻塞等待握手完成，才能发送数据
            handshakeHandler.handshakeResult().sync();
            log.info("WebSocketClient客户端={} 连接 服务端={} 启动成功", getLocal(channel), getRemote(channel));

            // 发送ping消息
//            while (true) {
//                channel.writeAndFlush(new PingWebSocketFrame());
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    log.error("WebSocketClient客户端ping异常", e);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("WebSocketClient客户端启动异常", e);
        } finally {
//            try {
//                bossGroup.shutdownGracefully().sync(); // 关闭
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                log.error("WebSocketClient客户端关闭异常", e);
//            }
        }
    }

    /**
     * 获取服务器地址
     */
    private String getRemote(Channel channel) {
        if (channel == null || !channel.isActive()) {
            return null;
        }
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        String remote = remoteAddress.getAddress() + ":" + remoteAddress.getPort();
        remote.substring(0, remote.length() - 1); // "截取/“
        return remote;
    }

    /**
     * 获取客户端地址
     */
    private String getLocal(Channel channel) {
        if (channel == null) {
            return null;
        }
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();
        String local = localAddress.getAddress() + ":" + localAddress.getPort();
        local.substring(0, local.length() - 1); // "截取/“
        return local;
    }
}
