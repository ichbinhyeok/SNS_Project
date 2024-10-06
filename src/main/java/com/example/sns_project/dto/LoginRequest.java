package com.example.sns_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 로그인 요청 정보를 담는 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    // Getter 및 Setter 메서드
    private String username;  // 사용자 이름 또는 이메일
    private String password;   // 사용자 비밀번호

}
