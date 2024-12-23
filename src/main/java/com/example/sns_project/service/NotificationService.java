package com.example.sns_project.service;

// 알림 관련 비즈니스 로직을 처리하는 서비스
import com.example.sns_project.dto.NotificationDTO;
import com.example.sns_project.exception.ResourceNotFoundException; // 사용자 정의 예외 추가
import com.example.sns_project.exception.UnauthorizedException;
import com.example.sns_project.model.Notification;
import com.example.sns_project.enums.NotificationType; // NotificationType 열거형 추가
import com.example.sns_project.model.User;
import com.example.sns_project.repository.NotificationRepository;
import com.example.sns_project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 특정 사용자의 모든 알림을 조회
    public List<NotificationDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getUser().getId(),
                        notification.isRead()))
                .collect(Collectors.toList());
    }

    // 특정 알림을 읽음으로 마킹 (권한 검증 추가)
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // 알림의 소유자 검증
        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to access this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 특정 알림 삭제 (권한 검증 추가)
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // 알림의 소유자 검증
        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    // 특정 사용자의 읽지 않은 알림 조회
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        return notifications.stream()
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getUser().getId(),
                        notification.isRead()))
                .collect(Collectors.toList());
    }

    // 알림 전송 메서드들은 그대로 유지 (내부적으로 사용되므로 권한 검증 불필요)
    public void sendNotification(Long userId, String message, NotificationType notificationType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    // 이하 다른 알림 전송 메서드들은 동일하게 유지
    public void sendPostCommentNotification(Long postOwnerId, String username) {
        String message = username + "님이 당신의 포스트에 댓글을 달았습니다.";
        sendNotification(postOwnerId, message, NotificationType.COMMENT);
    }

    public void sendPostLikeNotification(Long postOwnerId, String username) {
        String message = username + "님이 당신의 포스트에 좋아요를 눌렀습니다.";
        sendNotification(postOwnerId, message, NotificationType.LIKE);
    }

    public void sendCommentNotification(Long postOwnerId, String username) {
        String message = username + "님이 당신의 댓글에 댓글을 달았습니다.";
        sendNotification(postOwnerId, message, NotificationType.COMMENT);
    }

    public void sendCommentLikeNotification(Long commentOwnerId, String username) {
        String message = username + "님이 당신의 댓글에 좋아요를 눌렀습니다.";
        sendNotification(commentOwnerId, message, NotificationType.LIKE);
    }

    public void sendFriendRequestNotification(Long senderId, Long receiverId) {
        String message = "사용자 " + senderId + "님이 친구 요청을 보냈습니다.";
        sendNotification(receiverId, message, NotificationType.FRIEND_REQUEST);
    }

    public void sendFriendAddedNotification(Long userId, Long friendId) {
        String message = "사용자 " + friendId + "님과 친구가 되었습니다.";
        sendNotification(userId, message, NotificationType.DEFAULT);
    }
}