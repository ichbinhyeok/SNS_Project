package com.example.sns_project.service;

// 알림 관련 비즈니스 로직을 처리하는 서비스
import com.example.sns_project.dto.NotificationDTO;
import com.example.sns_project.model.Notification;
import com.example.sns_project.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;  // NotificationRepository 의존성 주입

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        // 사용자 알림 조회 로직
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(notification -> new NotificationDTO(notification.getId(), notification.getMessage(), notification.getUserId()))
                .collect(Collectors.toList());
    }

    // 앞으로: 알림 전송 메서드 추가
}
