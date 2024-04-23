package com.kl.ws.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * description : 自定义服务端握手处理器
 *
 * @author kunlunrepo
 * date :  2024-04-23 18:10
 */
@Slf4j
public class WsServerHandshakeHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接收数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("[WsServerHandshakeHandler][channelRead]------接收数据 channel={}", ctx.channel());
        if (msg instanceof FullHttpRequest) {
            log.info("[WsServerHandshakeHandler][channelRead]------握手消息 channel={}", ctx.channel());
        }
        // 处理完握手消息需要向下传递
        super.channelRead(ctx, msg);
    }
}
