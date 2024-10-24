package com.example.sns_project.service;

import com.example.sns_project.enums.RequestStatus;
import com.example.sns_project.model.FriendRequest;
import com.example.sns_project.model.Friendship;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.FriendRequestRepository;
import com.example.sns_project.repository.FriendshipRepository;
import com.example.sns_project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationService notificationService;

    // 친구 요청 보내기
    @Transactional
    public void sendFriendRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setTimestamp(LocalDateTime.now());
        friendRequest.setStatus(RequestStatus.PENDING);

        friendRequestRepository.save(friendRequest);
        notificationService.sendFriendRequestNotification(senderId, receiverId);
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        Friendship friendship = new Friendship();
        friendship.setUser1(friendRequest.getSender());
        friendship.setUser2(friendRequest.getReceiver());

        friendshipRepository.save(friendship);
        friendRequest.setStatus(RequestStatus.ACCEPTED);
        friendRequestRepository.save(friendRequest);
        notificationService.sendFriendAddedNotification(friendRequest.getReceiver().getId(), friendRequest.getSender().getId());
    }

    // 친구 요청 거절
    @Transactional
    public void rejectFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        friendRequest.setStatus(RequestStatus.REJECTED);
        friendRequestRepository.save(friendRequest);
    }

    // 친구 목록 조회
    public List<User> getFriends(Long userId) {
        return friendshipRepository.findFriendsByUserId(userId);
    }

    // 친구 요청 목록 조회
    public List<FriendRequest> getFriendRequests(Long userId) {
        return friendRequestRepository.findByReceiverId(userId);
    }

    // 친구 삭제 기능
    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        Friendship friendship = friendshipRepository.findByUserIds(userId, friendId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));
        friendshipRepository.delete(friendship);
    }

    // 친구 요청 상태 조회
    public RequestStatus getFriendRequestStatus(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        return friendRequest.getStatus();
    }

    // 친구 추천 기능
    public List<User> recommendFriends(Long userId) {
        return userRepository.findNonFriendsByUserId(userId);
    }
}
