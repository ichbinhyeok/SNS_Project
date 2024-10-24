package com.example.sns_project.service;

// 알림 관련 비즈니스 로직을 처리하는 서비스
import com.example.sns_project.dto.NotificationDTO;
import com.example.sns_project.exception.ResourceNotFoundException; // 사용자 정의 예외 추가
import com.example.sns_project.model.Notification;
import com.example.sns_project.enums.NotificationType; // NotificationType 열거형 추가
import com.example.sns_project.model.User;
import com.example.sns_project.repository.NotificationRepository;
import com.example.sns_project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository; // NotificationRepository 의존성 주입
    private final UserRepository userRepository; // UserRepository 의존성 주입

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

    // 특정 사용자에게 알림을 전송
    public void sendNotification(Long userId, String message, NotificationType notificationType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user); // User 객체 설정
        notification.setMessage(message);
        notification.setNotificationType(notificationType); // 열거형 사용
        notificationRepository.save(notification); // 알림 저장
    }

    // 특정 알림을 읽음으로 마킹
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found")); // 사용자 정의 예외 사용
        notification.setRead(true); // 읽음 상태로 변경
        notificationRepository.save(notification); // 업데이트 저장
    }

    // 특정 알림 삭제
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found"); // 사용자 정의 예외 사용
        }
        notificationRepository.deleteById(notificationId); // 알림 삭제
    }

    // 특정 사용자에게 모든 읽지 않은 알림을 조회
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

    // 특정 사용자에게 알림을 전송하고, 알림 타입을 기본값으로 설정
    public void sendDefaultNotification(Long userId, String message) {
        sendNotification(userId, message, NotificationType.DEFAULT); // 열거형 사용
    }
}
