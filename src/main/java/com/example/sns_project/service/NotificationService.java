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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService {

    private static final int BATCH_SIZE = 5000;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final List<Notification> notificationBuffer = new ArrayList<>();

    // 배치 처리를 위한 버퍼 플러시 메서드
    private synchronized void flushNotificationBuffer() {
        if (!notificationBuffer.isEmpty()) {
            notificationRepository.saveAll(notificationBuffer);
            notificationBuffer.clear();
        }
    }


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

    // 모든 알림 읽음 처리
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    // 읽지 않은 알림 개수 조회
    public Long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
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

    // 기본 알림 전송 메서드 (배치 처리)
    public void sendNotification(Long userId, String message, NotificationType notificationType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);

        synchronized (notificationBuffer) {
            notificationBuffer.add(notification);
            if (notificationBuffer.size() >= BATCH_SIZE) {
                flushNotificationBuffer();
            }
        }
    }

    // 남은 알림들 강제 저장
    public void flushRemainingNotifications() {
        flushNotificationBuffer();
    }


    // 전체 사용자에게 이벤트 알림 전송
    @Transactional
    public void sendEventNotificationToAll(String eventMessage) {
        int page = 0;
        int size = 1000; // 한 번에 조회할 사용자 수
        Page<User> userPage;

        do {
            userPage = userRepository.findAll(PageRequest.of(page, size));

            for (User user : userPage.getContent()) {
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setMessage(eventMessage);
                notification.setNotificationType(NotificationType.EVENT);

                synchronized(notificationBuffer) {
                    notificationBuffer.add(notification);
                    if (notificationBuffer.size() >= BATCH_SIZE) {
                        flushNotificationBuffer();
                    }
                }
            }

            page++;
        } while (userPage.hasNext());

        // 남은 알림들 처리
        flushNotificationBuffer();
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

    // 이벤트 알림 예시 메서드들
    public void sendNewFeatureEventNotification(String featureName) {
        String message = "새로운 기능이 추가되었습니다: " + featureName;
        sendEventNotificationToAll(message);
    }

    public void sendMaintenanceEventNotification(String maintenanceTime) {
        String message = "서버 점검 안내: " + maintenanceTime;
        sendEventNotificationToAll(message);
    }
}