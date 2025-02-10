package com.example.sns_project.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDetailDTO {
    private Long id;
    private String title;
    private String content;
    private UserDTO author;
    private Long likeCount;
    private boolean isLikedByUser;
    private LocalDateTime createdDate;

    public PostDetailDTO(Long id, String title, String content,
                         Long authorId, String authorUsername, String authorEmail,
                         Long likeCount, boolean isLikedByUser, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = new UserDTO(authorId, authorUsername, authorEmail);
        this.likeCount = likeCount;
        this.isLikedByUser = isLikedByUser;
        this.createdDate = createdDate;
    }
}


