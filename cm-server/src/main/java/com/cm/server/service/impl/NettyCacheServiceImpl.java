package com.cm.server.service.impl;

import com.cm.common.constant.RedisKey;
import com.cm.server.service.NettyCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * description : netty缓存服务实现
 *
 * @author kunlunrepo
 * date :  2024-04-24 16:47
 */
@Slf4j
@Service
public class NettyCacheServiceImpl implements NettyCacheService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增服务端地址
     */
    @Override
    public void addServerAddress(String serverAddress) {
        // 向redis添加服务端地址
        redisTemplate.opsForZSet().add(RedisKey.SERVER_ADDRESS_KEY, serverAddress, 0L);
    }

    /**
     * 新增服务器连接数
     */
    @Override
    public void addServerConnectCount(String serverAddress) {
        // 向redis添加服务器连接数
        redisTemplate.opsForZSet().incrementScore(RedisKey.SERVER_ADDRESS_KEY, serverAddress, 1d);
    }


}
