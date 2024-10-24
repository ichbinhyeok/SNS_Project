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
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //좋아요 ID

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment; // 댓글

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user; // 사용자



}
