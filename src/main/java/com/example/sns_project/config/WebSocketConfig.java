package com.example.sns_project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커를 설정하는 메서드
        // 클라이언트에게 메시지를 전송할 경로를 설정
        config.enableSimpleBroker("/topic"); // 메시지 브로커 경로 설정
        config.setApplicationDestinationPrefixes("/app"); // 애플리케이션에서 사용할 경로 접두사 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트를 등록하는 메서드
        // 클라이언트가 WebSocket 서버에 연결할 수 있는 경로를 설정
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS(); // SockJS를 사용하여 WebSocket 연결
    }

    // 앞으로: WebSocket 핸들러 및 인증 설정 추가 필요
}