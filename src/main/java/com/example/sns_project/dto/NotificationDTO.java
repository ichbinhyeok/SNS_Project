package com.example.sns_project.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 알림 정보를 전송하기 위한 데이터 전송 객체
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id; // 알림 ID

    @NotEmpty(message = "알림 내용은 필수입니다.")
    private String message; // 알림 내용

    @NotNull(message = "수신자 ID는 필수입니다.")
    private Long userId; // 수신자 ID

    private boolean isRead; // 알림 읽음 여부

    private String notificationType; // 알림 유형

    // 이 생성자 추가
    public NotificationDTO(Long id, String message, Long userId, boolean isRead) {
        this.id = id;
        this.message = message;
        this.userId = userId;
        this.isRead = isRead;
    }
}
