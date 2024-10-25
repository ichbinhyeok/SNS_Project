package com.example.sns_project.model;

import com.example.sns_project.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 친구 요청 ID

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // 요청 보낸 사용자

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 요청 받은 사용자

    private LocalDateTime timestamp; // 요청 시간

    // 추가 필드: 요청 상태 (예: PENDING, ACCEPTED, REJECTED)
    @Enumerated(EnumType.STRING)
    private RequestStatus status; // 요청 상태
}
