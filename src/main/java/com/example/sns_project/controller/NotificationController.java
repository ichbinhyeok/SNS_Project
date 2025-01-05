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

    // 모든 알림 읽음 처리
    @PostMapping("/read-all")
    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음으로 표시합니다.")
    public ResponseEntity<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // 알림 개수 조회 (읽지 않은 알림 수)
    @GetMapping("/count")
    @Operation(summary = "읽지 않은 알림 개수 조회", description = "사용자의 읽지 않은 알림 개수를 조회합니다.")
    public ResponseEntity<Long> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(notificationService.getUnreadNotificationCount(userId));
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

    // 전체 사용자 이벤트 알림 발송 (관리자용)
    @PostMapping("/event/all")
    @Operation(summary = "전체 사용자 이벤트 알림 발송", description = "모든 사용자에게 이벤트 알림을 발송합니다. (관리자 전용)")
    public ResponseEntity<Void> sendEventToAll(
            @Parameter(description = "이벤트 메시지", required = true)
            @RequestParam String message,
            HttpServletRequest request) {
        // TODO: 관리자 권한 체크 로직 필요
        Long adminId = (Long) request.getAttribute("userId");
        notificationService.sendEventNotificationToAll(message);
        return ResponseEntity.ok().build();
    }
}
