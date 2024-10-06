package com.example.sns_project.controller;

// 알림 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.NotificationDTO;
import com.example.sns_project.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;  // NotificationService 의존성 주입

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {
        // 사용자 알림 조회 로직
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    // 앞으로: WebSocket 연동 처리 및 예외 처리 추가 필요
}
