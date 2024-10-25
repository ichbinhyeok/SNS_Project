package com.example.sns_project.controller;

// 알림 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.NotificationDTO;
import com.example.sns_project.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "사용자 알림 조회", description = "특정 사용자의 모든 알림을 조회합니다.")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PostMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음으로 마킹합니다.")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "알림 ID", required = true) @PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "알림 ID", required = true) @PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    // 앞으로: WebSocket 연동 처리 및 예외 처리 추가 필요
}
