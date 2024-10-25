package com.example.sns_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 사용자 비밀번호 수정 정보를 전송하기 위한 데이터 전송 객체
public class UserPasswordUpdateDTO {
    private String password;      // 사용자 비밀번호
}
