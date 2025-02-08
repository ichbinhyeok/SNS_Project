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
@Table(name = "post_likes",
        indexes = {
                @Index(name = "idx_post_user", columnList = "post_id,user_id", unique = true)
        }
)
public class PostLike extends BaseEntity{
    // ID 필드는 BaseEntity에서 상속받음


    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post; // 게시글

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user; // 사용자



}
