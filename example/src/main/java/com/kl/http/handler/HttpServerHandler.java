package com.kl.http.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * description : 自定义Http处理器
 *
 * @author kunlunrepo
 * date :  2024-04-23 11:44
 */
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 应答消息
     */
    private static final String RES_MSG = "你好，我是HTTP服务端";

    /**
     * 通道连接
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[HttpServerHandler][channelActive] 通道连接");
        super.channelActive(ctx);
    }

    /**
     * 接收数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        log.info("[HttpServerHandler][channelRead0] 接收数据开始");
        if (msg instanceof HttpRequest) {
            // 请求
            HttpRequest httpRequest = (HttpRequest) msg;
            HttpMethod method = httpRequest.method();
            String uri = httpRequest.uri();
            log.info("[HttpServerHandler][channelRead0] uri={} method={} httpRequest={}", uri, method, httpRequest);
            // 响应
            FullHttpResponse response = new DefaultFullHttpResponse(
                    httpRequest.protocolVersion(),
                    OK,
                    Unpooled.copiedBuffer(RES_MSG.getBytes())
            );
            // 设置请求头
            response.headers().set("Content-Type", "text/plain; charset=UTF-8");
            response.headers().set("Content-Length", response.content().readableBytes());
            // 发送消息
            ChannelFuture future = ctx.writeAndFlush(response);
            log.info("[HttpServerHandler][channelRead0] 返回响应");
        } else {
            // 会返回这个 EmptyLastHttpContent 标记HTTP消息结束的HTTP块
            log.info("[HttpServerHandler][channelRead0] 接收数据非HttpRequest");
        }
        log.info("[HttpServerHandler][channelRead0] 接收数据结束");
    }

    /**
     * 接收数据完成
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("[HttpServerHandler][channelReadComplete] 接收数据完成");
        super.channelReadComplete(ctx);
    }

    /**
     * 处理异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常后关闭通道
        log.error("[HttpServerHandler][exceptionCaught] 处理异常", cause);
        ctx.close();
    }


}
