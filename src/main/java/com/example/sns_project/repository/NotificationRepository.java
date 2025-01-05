package com.example.sns_project.repository;

// 알림 데이터 접근을 위한 JPA 레포지토리
import com.example.sns_project.enums.NotificationType;
import com.example.sns_project.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);  // 사용자 ID로 알림 조회
    List<Notification>findByUserIdAndIsRead(Long userId, boolean bool);

    Long countByUserIdAndIsRead(Long userId, boolean isRead);
    List<Notification> findByUserIdAndNotificationType(Long userId, NotificationType type);


    // 앞으로: 추가적인 쿼리 메서드 정의 (예: 알림 삭제 등)
}
