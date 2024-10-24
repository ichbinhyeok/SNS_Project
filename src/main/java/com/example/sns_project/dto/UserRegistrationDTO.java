package com.example.sns_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 사용자 등록 정보를 전송하기 위한 데이터 전송 객체
public class UserRegistrationDTO {
    private String username;      // 사용자 이름
    private String email;         // 사용자 이메일
    private String password;      // 사용자 비밀번호
}
