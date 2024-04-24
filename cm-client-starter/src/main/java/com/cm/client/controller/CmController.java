package com.cm.client.controller;

import com.cm.client.CmClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * description : 通信控制器
 *
 * @author kunlunrepo
 * date :  2024-04-24 11:48
 */
@RestController
@Slf4j
public class CmController {

    /**
     * 通信客户端
     */
    @Autowired
    private CmClient cmClient;

    /**
     * 发送消息
     */
    @GetMapping("send-msg")
    public void sendMsg(@RequestParam String msg) {
        boolean result = cmClient.sendMsg(msg);
        log.info("发送消息：msg={} result={}", msg, result);
    }

}
