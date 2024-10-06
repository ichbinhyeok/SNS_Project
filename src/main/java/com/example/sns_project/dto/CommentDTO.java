package com.example.sns_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 댓글 정보를 전송하기 위한 데이터 전송 객체
// API 요청 및 응답에 사용됩니다.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;              // 댓글 ID
    private Long postId;          // 게시글 ID (이 댓글이 속한 게시글)
    private String content;       // 댓글 내용
    private Long authorId;        // 작성자 ID


// 앞으로: 유효성 검사 애너테이션 추가 (예: @NotEmpty)
}
