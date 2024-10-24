package com.example.sns_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="post_likes")
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //좋아요 Id

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post; // 게시글

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user; // 사용자



}
