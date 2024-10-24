package com.example.sns_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

// 게시글 정보를 전송하기 위한 데이터 전송 객체
// API 요청 및 응답에 사용됩니다.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;              // 게시글 ID
    private String title;         // 게시글 제목
    private String content;       // 게시글 내용
    private UserDTO author;       // 작성자 정보 (UserDTO로 변경)
    private Set<Long> likedBy;    // 좋아요 누른 사람 ID 목록

    // Getter 및 Setter 메서드

    // 앞으로: 유효성 검사 애너테이션 추가 (예: @NotEmpty)
}
