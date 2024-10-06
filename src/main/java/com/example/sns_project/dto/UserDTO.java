package com.example.sns_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 사용자 정보를 전송하기 위한 데이터 전송 객체
// API 요청 및 응답에 사용됩니다.
public class UserDTO {
    private Long id;              // 사용자 ID
    private String username;      // 사용자 이름
    private String email;         // 사용자 이메일
    private String password;      // 사용자 비밀번호 (가입 시)

    // Getter 및 Setter 메서드

    // 앞으로: 유효성 검사 애너테이션 추가 (예: @NotNull, @Email)
}
