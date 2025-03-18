package com.yes.product.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 启用 WebSocket 消息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 配置消息代理
        config.enableSimpleBroker("/pd"); // 客户端可以订阅以 /topic 开头的地址
        config.setApplicationDestinationPrefixes("/pd"); // 客户端发送消息时需要以 /app 开头
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册 STOMP 端点，客户端将通过这个端点连接到 WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 允许所有来源
                .withSockJS(); // 使用 SockJS 作为后备选项
    }
}