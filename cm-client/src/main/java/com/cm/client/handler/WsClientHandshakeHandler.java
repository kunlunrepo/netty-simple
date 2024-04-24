package com.cm.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * description : 自定义客户端握手处理器
 *
 * @author kunlunrepo
 * date :  2024-04-23 15:14
 */
@Slf4j
public class WsClientHandshakeHandler extends ChannelInboundHandlerAdapter {

    /**
     * 处理客户端握手
     * 说明：在建立WebSocket连接时，客户端需要发送一个特殊的HTTP请求（通常是一个带有 Upgrade 头部的 GET 请求），与服务器协商升级到 WebSocket 协议。
     *      WebSocketClientHandshaker提供了这个握手过程所需的方法和状态管理，使得客户端能够方便、正确地完成握手，从而建立起有效的 WebSocket 连接。
     */
    private WebSocketClientHandshaker handshaker;

    /**
     * 握手结果
     * 说明：ChannelPromise是异步操作结果通知核心类
     */
    private ChannelPromise handshakeResult;

    /**
     * 构造函数
     */
    public WsClientHandshakeHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    /**
     * 获取握手结果
     * 说明：添加channel时初始化，供启动时订阅结果使用
     */
    public ChannelPromise handshakeResult() {
        return handshakeResult;
    }

    /**
     * 新增了处理器
     * 说明：当一个新的 ChannelInboundHandler被添加到ChannelPipeline中时，Netty会自动触发handlerAdded方法
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 初始化握手结果实例
        handshakeResult = ctx.newPromise();
    }

    /**
     * 通道连接
     * 说明：客户端主动连接服务端的链接后，触发该方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[WsClient][握手处理器][channelActive] 通道连接");
        // 构建和发送握手请求
        handshaker.handshake(ctx.channel());
        log.info("[WsClient][握手处理器][channelActive] 通道连接成功 channel={}", ctx.channel());
    }

    /**
     * 接收数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("[WsClient][握手处理器][channelRead] 接收到数据");
        // 判断握手是否完成
        if (!handshaker.isHandshakeComplete()) {
            try {
                // 如果握手未完成，则完成握手(验证响应、升级通道、设置握手完成标志)
                handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
                log.info("[WsClient][握手处理器][channelRead] 握手完成");
                // 设置握手结果
                handshakeResult.setSuccess();
            } catch (Exception e) {
                log.error("[WsClient][握手处理器][channelRead] 握手异常", e);
                // 设置握手结果
                handshakeResult.setFailure(e);
            }
            return;
        } else {
            // 握手完成不应该再收到HTTP报文
            if (msg instanceof FullHttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;
                throw new IllegalStateException("内部异常 FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
            }
        }
        super.channelRead(ctx, msg);
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[WsClient][握手处理器][exceptionCaught] 异常处理", cause);
        if (!handshakeResult.isDone()) {
            handshakeResult.setFailure(cause);
        }
        ctx.close();
    }

}
