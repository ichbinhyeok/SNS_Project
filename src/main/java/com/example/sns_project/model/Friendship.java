package com.example.sns_project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "friendships")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 친구 관계 ID

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1; // 친구 1

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2; // 친구 2
}
