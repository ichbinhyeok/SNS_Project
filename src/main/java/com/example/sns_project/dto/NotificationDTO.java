package com.example.sns_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 알림 정보를 전송하기 위한 데이터 전송 객체
// API 요청 및 응답에 사용됩니다.
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;              // 알림 ID
    private String message;       // 알림 내용
    private Long userId;          // 수신자 ID

    // Getter 및 Setter 메서드

    // 앞으로: 유효성 검사 애너테이션 추가 (예: @NotEmpty)
}
