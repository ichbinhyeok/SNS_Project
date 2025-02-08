package com.example.sns_project.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentHierarchyDTO {

    private Long id;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedDate; // 이 필드는 사용하려면 추가해야 함
    private int depth;
    private Long parentCommentId;
    private Long authorId;
    private String authorName;
    private List<CommentHierarchyDTO> replies = new ArrayList<>();

//    private int totalReplies; // 총 대댓글 수 추가

}
