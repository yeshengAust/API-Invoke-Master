package com.yes.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate; // 注入 SimpMessagingTemplate
    @MessageMapping("/pd/senMessage") // 处理客户端发送到 /app/hello 的消息
    @SendTo("/pd/pushProduct") // 将返回的消息发送到 /topic/greetings
    public String greeting(String message) {
        return "Hello, " + message + "!";
    }

    // 添加一个 REST API 用于主动推送消息
    @GetMapping("/push")
    public String pushMessage( String message) {
        // 使用 SimpMessagingTemplate 向 /topic/greetings 发送消息
        simpMessagingTemplate.convertAndSend("/pd/pushProduct", "Server says: " + message);
        return "Message pushed: " + message;
    }
}