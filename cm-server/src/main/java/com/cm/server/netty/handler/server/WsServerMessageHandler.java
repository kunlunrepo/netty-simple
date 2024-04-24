package com.cm.server.netty.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

/**
 * description : WebSocket服务端消息处理器
 *
 * WebSocketFrame是WebSocket协议中的数据帧，在WebSocket通信中，数据不是直接以原始字节流传输，而是封装成特定格式的帧进行发送和接收
 *
 * @author kunlunrepo
 * date :  2024-04-23 14:36
 */
@Slf4j
public class WsServerMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    /**
     * 接收数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        log.info("[WsServer][消息处理器][channelRead0]------[有消息]");
        // 消息分类处理
        if(msg instanceof PingWebSocketFrame) {
            // ping消息
            // 如果收到ping消息，则返回pong消息
            log.info("[WsServer][消息处理器][channelRead0]------[Ping] channel={}", ctx.channel());
//            ctx.channel().writeAndFlush(new PongWebSocketFrame(msg.content().retain()));
        } else if (msg instanceof CloseWebSocketFrame) {
            // 关闭帧消息
            log.info("[WsServer][消息处理器][channelRead0]------[Close] channel={}", ctx.channel());
        } else if (msg instanceof TextWebSocketFrame) {
            // 文本消息
            log.info("[WsServer][消息处理器][channelRead0]------[Text]");
        } else if (msg instanceof BinaryWebSocketFrame) {
            // 二进制数据帧消息 (如图片、音频、视频、序列化对象等)
            log.info("[WsServer][消息处理器][channelRead0]------[Binary]");
        }

    }
}
