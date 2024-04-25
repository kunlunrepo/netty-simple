package com.cm.server.utils;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * description : 通道IP工具类
 *
 * @author kunlunrepo
 * date :  2024-04-25 11:18
 */
@Slf4j
public class ChannelIpUtil {

    /**
     * 获取本地地址(channel直接关联的本地网络地址)
     */
    public static String localAddress(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.localAddress();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        String localAddress = hostAddress + ":"+ socketAddress.getPort();
        return localAddress;
    }

    /**
     * 获取对端地址
     */
    public static String remoteAddress(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        String remoteAddress = hostAddress + ":"+ socketAddress.getPort();
        return remoteAddress;
    }

    /**
     * 获取本地地址(主机名对应的)
     * 说明：当前运行Java程序主机的IP地址，不关心特定的网络连接或端口
     */
    public static String localJavaAddress(Channel channel) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String localIp = localHost.getHostAddress();
            InetSocketAddress socketAddress = (InetSocketAddress) channel.localAddress();
            int port = socketAddress.getPort();
            return localIp+":"+port;
        } catch (Exception e) {
            log.error("[localJavaAddress]获取本地地址失败", e);
        }
        return null;
    }

}
