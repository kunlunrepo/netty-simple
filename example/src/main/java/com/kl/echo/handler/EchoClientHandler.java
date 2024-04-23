package com.kl.echo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * description : 自定义客户端处理器
 *
 * @author kunlunrepo
 * date :  2024-04-23 10:52
 */
@Slf4j
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 通道连接
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[EchoClientHandler][channelActive] 通道连接");
        // 向服务端发送数据
        ctx.writeAndFlush(Unpooled.copiedBuffer("你好 服务端！", CharsetUtil.UTF_8));
        log.info("[EchoClientHandler][channelActive] 发送消息");
    }

    /**
     * 接收数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        log.info("[EchoClientHandler][channelRead0] 接收数据开始");
        ByteBuf in = (ByteBuf) byteBuf;
        String msgStr = in.toString(CharsetUtil.UTF_8);
        log.info("[EchoClientHandler][channelRead0] 接收数据结束 msg={}", msgStr);
    }

    /**
     * 接收数据完成
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("[EchoClientHandler][channelReadComplete] 接收数据完成");
        super.channelReadComplete(ctx);
    }

    /**
     * 处理用户自定义事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("[EchoClientHandler][userEventTriggered] 处理用户自定义事件");
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 处理异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常后关闭通道
        log.error("[EchoClientHandler][exceptionCaught] 处理异常", cause);
        ctx.close();
    }


}
