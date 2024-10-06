package com.example.sns_project.websocket;

// WebSocket 알림 처리 핸들러
import com.example.sns_project.dto.NotificationDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationSocketHandler {

    @MessageMapping("/notify")  // 클라이언트에서 /app/notify로 메시지를 전송하면 호출됨
    @SendTo("/topic/notifications")  // 해당 주제를 구독한 모든 클라이언트에게 메시지를 전송
    public NotificationDTO sendNotification(NotificationDTO notification) {
        // 알림 처리 로직
        return notification;  // 클라이언트에게 전송할 알림 반환
    }

    // 앞으로: 알림 필터링 및 전송 로직 추가
}
