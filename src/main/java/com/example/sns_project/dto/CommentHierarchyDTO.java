package com.example.sns_project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CommentHierarchyDTO {

    private Long id;
    private Long postId;
    private String content;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private int depth;
    private List<CommentHierarchyDTO> replies = new ArrayList<>();

//    private int totalReplies; // 총 대댓글 수 추가

}
