package com.cm.client;

import com.cm.client.handler.WsClientHandshakeHandler;
import com.cm.client.handler.WsClientMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * description : 通信模块客户端入口
 *
 * @author kunlunrepo
 * date :  2024-04-24 10:55
 */
@Slf4j
public class CmClientInitializer implements CmClient{

    /**
     * 服务端IP
     */
    private static String HOST = "";

    /**
     * 服务端port
     */
    private static int PORT = 9010;

    /**
     * 服务端连接地址
     */
    private static String WS_URL = "";

    /**
     * 通道
     * 说明: 负责和服务端发送消息
     */
    private Channel channel;

    /**
     * 构造方法
     */
    public CmClientInitializer(String host, int port, String wsUrl) {
        // 接收参数
        this.HOST = host;
        this.PORT = port;
        this.WS_URL = wsUrl;
    }

    /**
     * 启动
     */
    @Override
    public boolean start() {
        log.info("******************** [通信模块客户端]启动 ********************");
        log.info("[通信模块客户端] 连接host={}", this.HOST);
        log.info("[通信模块客户端] 连接port={}", this.PORT);
        log.info("[通信模块客户端] 连接ws_url={}", this.WS_URL);
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
            // 连接服务器 (连接后，channel可以和服务器进行通信)
            channel = bootstrap.connect(HOST, PORT).sync().channel();
            // 阻塞等待握手完成，才能发送数据
            handshakeHandler.handshakeResult().sync();
            log.info("[通信模块客户端] 客户端={}至服务端={}连接成功", getLocal(channel), getRemote(channel));
            log.info("******************** [通信模块客户端]启动成功 ********************");
            return true;
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
            log.error("[通信模块客户端]启动异常", e);
        } finally {
//            try {
//                bossGroup.shutdownGracefully().sync(); // 关闭
//            log.info("******************** [通信模块客户端]关闭 ********************");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                log.error("WebSocketClient客户端关闭异常", e);
//            }
        }
        return false;
    }

    /**
     * 停止
     */
    @Override
    public void stop() {

    }

    /**
     * 发送文本消息
     */
    @Override
    public boolean sendMsg(String msg) {
        // 判断通道是否可用
        if(channel != null && channel.isActive()) {
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(msg);
            channel.writeAndFlush(textWebSocketFrame);
            log.info("[通信模块客户端] 发送消息成功 msg={}", msg);
            return true;
        } else {
            log.error("[通信模块客户端] 通道不可用");
            return false;
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
