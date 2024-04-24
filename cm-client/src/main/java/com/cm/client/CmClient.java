package com.cm.client;

/**
 * description : 通信模块客户端入口
 *
 * @author kunlunrepo
 * date :  2024-04-24 11:58
 */
public interface CmClient {

    /**
     * 启动客户端
     */
    boolean start();

    /**
     * 关闭客户端
     */
    void stop();

    /**
     * 发送文本消息
     */
    boolean sendMsg(String msg);
}
