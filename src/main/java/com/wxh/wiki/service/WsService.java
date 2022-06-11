package com.wxh.wiki.service;

import com.wxh.wiki.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author wxh
 * @date 2022-06-11 17:01
 */
@Service
public class WsService {

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     *加上Async注解，当调用这个方法时，就会另起线程去执行里面的内容。
     */
    @Async
    public void sendInfo(String message){

        webSocketServer.sendInfo(message);
    }
}
