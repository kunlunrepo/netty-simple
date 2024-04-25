package com.cm.server.netty.handler.server;

import com.cm.server.service.NettyCacheService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * description : 自定义服务端通道关闭处理器
 *
 * @author kunlunrepo
 * date :  2024-04-25 09:26
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WsServerCloseHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private NettyCacheService nettyCacheService;

    /**
     * 关闭通道
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("[WsServer][通道关闭处理器][handlerRemoved]------ channel={}", ctx.channel());
        // 删除服务器连接数
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        String serverIp = hostAddress + ":"+ socketAddress.getPort();
        nettyCacheService.removeServerConnectCount(serverIp);
        // 关闭通道
        ctx.close();
    }
}
