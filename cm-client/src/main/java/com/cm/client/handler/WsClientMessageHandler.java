package com.cm.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * description : WebSocket客户端消息处理器
 *
 * @author kunlunrepo
 * date :  2024-04-23 16:31
 */
@Slf4j
public class WsClientMessageHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接收消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PongWebSocketFrame) {
            log.info("[WsClient][消息处理器][channelRead]------[Pong] channel={}", ctx.channel());
        } else if (msg instanceof CloseWebSocketFrame) {
            log.info("[WsClient][消息处理器][channelRead]------[Close] channel={}", ctx.channel());
            ctx.channel().close();
        } else if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) msg;
            log.info("[WsClient][消息处理器][channelRead]------[Text] channel={}", ctx.channel());
        }
    }
}
