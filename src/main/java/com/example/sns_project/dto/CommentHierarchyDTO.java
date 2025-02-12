package com.example.sns_project.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentHierarchyDTO {
    private Long id;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedDate;
    private int depth;
    private Long parentCommentId;
    private Long authorId;
    private String authorName;
    private long replyCount;  // 대댓글 수
    private List<CommentHierarchyDTO> replies = new ArrayList<>();

    public CommentHierarchyDTO(
            Long id, Long postId, String content,
            LocalDateTime createdAt, LocalDateTime modifiedDate,
            int depth, Long parentCommentId,
            Long authorId, String authorName,
            long replyCount) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedDate = modifiedDate;
        this.depth = depth;
        this.parentCommentId = parentCommentId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.replyCount = replyCount;
    }
}