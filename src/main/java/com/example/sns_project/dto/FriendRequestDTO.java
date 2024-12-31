package com.example.sns_project.dto;

import com.example.sns_project.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDTO {
    private Long id; // FriendRequest ID
    private String senderUsername; // 요청 보낸 사용자 이름
    private Long SenderId;
    private String receiverUsername; // 요청 받은 사용자 이름
    private RequestStatus status; // 요청 상태
    private LocalDateTime createdDate; // 요청 생성 날짜
}