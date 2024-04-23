package com.kl.echo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * description : 自定义服务端处理器
 *
 * @author kunlunrepo
 * date :  2024-04-23 10:35
 */
//@ChannelHandler.Sharable /*不加这个注解那么在增加到childHandler时就必须new出来*/
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 通道连接
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[EchoServerHandler][channelActive] 通道连接");
        super.channelActive(ctx);
    }

    /**
     * 接收数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("[EchoServerHandler][channelRead] 接收数据开始");
        // 字节流数组
        ByteBuf in = (ByteBuf) msg;
        String msgStr = in.toString(CharsetUtil.UTF_8);
        log.info("[EchoServerHandler][channelRead] 接收数据结束 msg={}", msgStr);
        super.channelRead(ctx, msg);
    }

    /**
     * 接收数据完成
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("[EchoServerHandler][channelReadComplete] 接收数据完成");
        // 向客户端发送数据并释放资源
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
//                .addListener(ChannelFutureListener.CLOSE);
        ctx.writeAndFlush(Unpooled.copiedBuffer("你也好,客户端！", CharsetUtil.UTF_8));
        log.info("[EchoServerHandler][channelReadComplete] 发送消息");
    }

    /**
     * 处理用户自定义事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("[EchoServerHandler][userEventTriggered] 处理用户自定义事件");
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 处理异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常后关闭通道
        log.error("[EchoServerHandler][exceptionCaught] 处理异常", cause);
        ctx.close();
    }
}
