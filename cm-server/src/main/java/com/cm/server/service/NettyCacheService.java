package com.cm.server.service;

/**
 * description : netty缓存服务
 *
 * @author kunlunrepo
 * date :  2024-04-24 16:47
 */
public interface NettyCacheService {

    /**
     * 新增服务地址
     */
    void addServerAddress(String serverAddress);

    /**
     * 新增服务器连接数
     */
    void addServerConnectCount(String serverAddress);

    /**
     * 删除服务器连接数
     */
    void removeServerConnectCount(String serverAddress);

}
