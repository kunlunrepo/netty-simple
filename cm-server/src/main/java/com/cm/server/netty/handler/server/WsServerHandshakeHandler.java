package com.cm.server.netty.handler.server;

import com.cm.server.service.NettyCacheService;
import com.cm.server.utils.ChannelIpUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * description : 自定义服务端握手处理器
 *
 * @author kunlunrepo
 * date :  2024-04-23 18:10
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WsServerHandshakeHandler extends ChannelInboundHandlerAdapter {

    /**
     * netty缓存服务
     */
    @Autowired
    private NettyCacheService nettyCacheService;

    /**
     * 接收数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("[WsServerHandshakeHandler][channelRead]------接收数据 channel={}", ctx.channel());
        if (msg instanceof FullHttpRequest) {
            log.info("[WsServer][握手处理器][channelRead]------握手消息 channel={}", ctx.channel());
            // 握手成功后
            handshakeSuccessAfter(ctx.channel());
        }
        // 处理完握手消息需要向下传递
        super.channelRead(ctx, msg);
    }

    /**
     * 握手成功后
     */
    private void handshakeSuccessAfter(Channel channel) {
        // 获取服务器IP
        String localAddress = ChannelIpUtil.localAddress(channel);
        // 上报连接数
        nettyCacheService.addServerConnectCount(localAddress);
    }
}
