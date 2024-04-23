package com.kl.ws.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.UUID;

/**
 * description : 服务端通道初始化
 *
 * @author kunlunrepo
 * date :  2024-04-23 14:00
 */
public class WsServerChannelInit extends ChannelInitializer<SocketChannel> {

    /**
     * websocket路径
     */
    private final String WS_PATH = "/ws";

    /**
     * SSL上下文
     */
    private final SslContext sslContext;

    /**
     * 初始化时传递部分参数
     */
    public WsServerChannelInit(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    /**
     * 添加通道
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 添加SSL通道
        if (null != sslContext) {
            pipeline.addLast(sslContext.newHandler(ch.alloc()));
        }
        // 添加心跳检测处理器 (用于检测和管理网络连接空闲状态的关键组件。开发者可以实现心跳检测、资源清理、超时处理等功能)
        pipeline.addLast(new IdleStateHandler(20, 0, 60));
        // 添加心跳处理器

        // 添加HTTP服务端编解码器
        pipeline.addLast(new HttpServerCodec());
        // 添加HTTP聚合器(指定最大聚合字节数)
        pipeline.addLast(new HttpObjectAggregator(65536));
        // 添加自定义服务端握手处理器
        pipeline.addLast(new WsServerHandshakeHandler());
        // 添加websocket应答数据压缩器
        pipeline.addLast(new WebSocketServerCompressionHandler());
        // 添加websocket握手(协议升级)，帧解码等处理器 (会自动截获ping消息)
        pipeline.addLast(new WebSocketServerProtocolHandler(WS_PATH, null, true));
        // 添加自定义消息处理器
        pipeline.addLast(new WebSocketMessageHandler());
    }
}
