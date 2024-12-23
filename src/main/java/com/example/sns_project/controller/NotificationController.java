package com.example.sns_project.controller;

// 알림 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.NotificationDTO;
import com.example.sns_project.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "내 알림 조회", description = "현재 사용자의 모든 알림을 조회합니다.")
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 조회", description = "현재 사용자의 읽지 않은 알림을 조회합니다.")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PostMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음으로 마킹합니다.")
    public ResponseEntity<Void> markAsRead(
            HttpServletRequest request,
            @Parameter(description = "알림 ID", required = true) @PathVariable Long notificationId) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    public ResponseEntity<Void> deleteNotification(
            HttpServletRequest request,
            @Parameter(description = "알림 ID", required = true) @PathVariable Long notificationId) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.deleteNotification(notificationId, userId);
        return ResponseEntity.ok().build();
    }
}
