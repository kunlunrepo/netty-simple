package com.cm.server.netty.handler.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * description : 自定义服务端心跳处理器
 *
 * @author kunlunrepo
 * date :  2024-04-25 10:42
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WsServerHeartBeatHandler extends ChannelDuplexHandler {

    /**
     * 自定义用户事件 (IdleStateHandler内部会发出该事件)
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 判断事件类型
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            // 读空闲
            if (IdleState.READER_IDLE.equals(event.state())) {
                log.info("[WsServer][心跳处理器][userEventTriggered]------ channel={}", ctx.channel());
                return;
            }
        }
        // 继续执行
        super.userEventTriggered(ctx, evt);
    }
}
