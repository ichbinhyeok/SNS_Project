package com.example.sns_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment_likes")
public class CommentLike  extends BaseEntity {

    // ID 필드는 BaseEntity에서 상속받음


    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment; // 댓글

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user; // 사용자



}
