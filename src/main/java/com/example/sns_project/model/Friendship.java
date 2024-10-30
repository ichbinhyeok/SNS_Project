package com.example.sns_project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "friendships")
public class Friendship extends BaseEntity{
    // ID 필드는 BaseEntity에서 상속받음


    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1; // 친구 1

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2; // 친구 2
}
