package com.example.sns_project.model;

// 알림 정보를 저장하는 엔티티 클래스
// 데이터베이스와 매핑됩니다.
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 알림 ID

    @Column(nullable = false)
    private String message;       // 알림 내용

    @Column(name = "user_id", nullable = false)
    private Long userId;          // 수신자 ID

    // Getter 및 Setter 메서드

    // 앞으로: JPA 어노테이션 추가 및 관계 설정
}
