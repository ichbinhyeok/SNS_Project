package com.example.sns_project.model;

// 알림 정보를 저장하는 엔티티 클래스
import com.example.sns_project.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {
    // ID 필드는 BaseEntity에서 상속받음


    @Column(nullable = false)
    private String message;       // 알림 내용

    @ManyToOne(fetch = FetchType.LAZY) // User와의 연관관계 설정
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 설정
    private User user;            // 수신자 객체

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false; // 알림 읽음 여부

    @Enumerated(EnumType.STRING) // 열거형으로 설정
    @Column(name = "notification_type") // 알림 유형
    private NotificationType notificationType; // 알림 유형을 열거형으로 변경

    // JPA 어노테이션 추가 및 관계 설정
}
