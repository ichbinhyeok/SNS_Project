package com.example.sns_project.service;

import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.enums.RequestStatus;
import com.example.sns_project.exception.ResourceNotFoundException;
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

        // 기본 검증
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Can't send friend request to yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        //이미 친구인지 확인
        if (friendshipRepository.existsFriendship(sender.getId(), receiver.getId())) {
            throw new IllegalStateException("Already friends");
        }
        //중복 요청인지 확인
        if (friendRequestRepository.existsBySenderAndReceiverAndStatus(sender, receiver, RequestStatus.PENDING)) {
            throw new IllegalStateException("Already friends");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(RequestStatus.PENDING);

        friendRequestRepository.save(friendRequest);
        notificationService.sendFriendRequestNotification(senderId, receiverId);
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        //요청이 존재하는지 확인
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        //올바른 요청인지 확인
        if (friendRequest.getStatus() != RequestStatus.PENDING) {
            if (friendRequest.getStatus() == RequestStatus.ACCEPTED) {
                throw new IllegalStateException("Friend request is accepted");
            } else if (friendRequest.getStatus() == RequestStatus.REJECTED) {
                throw new IllegalStateException("Friend request is rejected");
            }
        }

        //이미 친구 관계인지 확인
        if (friendshipRepository.existsByUser1IdAndUser2Id(friendRequest.getSender().getId(), friendRequest.getReceiver().getId())) {
            throw new IllegalStateException("Friendship already exists");
        }


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
        // 요청 존재 확인
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        // 처리 가능한 상태인지 확인
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request already processed");
        }

        // 상태 변경 및 저장
        request.setStatus(RequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }

    // 친구 목록 조회
    public List<UserDTO> getFriends(Long userId) {
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
