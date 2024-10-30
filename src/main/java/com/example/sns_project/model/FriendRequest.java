package com.example.sns_project.model;

import com.example.sns_project.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "friend_requests")
@Getter
@Setter
public class FriendRequest extends BaseEntity {
    // 친구 요청 ID는 BaseEntity에 정의되어 있으므로 필요 없음
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // 요청 보낸 사용자

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 요청 받은 사용자

    // 요청 상태 (예: PENDING, ACCEPTED, REJECTED)
    @Enumerated(EnumType.STRING)
    private RequestStatus status; // 요청 상태

    // timestamp 필드를 제거하고 BaseEntity의 createdDate 사용
}
