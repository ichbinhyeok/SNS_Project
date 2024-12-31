package com.example.sns_project.service;

import com.example.sns_project.dto.FriendRequestDTO;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.enums.RequestStatus;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.exception.UnauthorizedException;
import com.example.sns_project.exception.DuplicateRequestException;
import com.example.sns_project.exception.InvalidRequestStateException;
import com.example.sns_project.model.FriendRequest;
import com.example.sns_project.model.Friendship;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.FriendRequestRepository;
import com.example.sns_project.repository.FriendshipRepository;
import com.example.sns_project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationService notificationService;

    @Transactional
    public void sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Can't send friend request to yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        // 이미 친구인지 확인
        if (friendshipRepository.existsFriendship(senderId, receiverId)) {
            throw new DuplicateRequestException("Already friends");
        }

        // 중복 요청인지 확인
        if (friendRequestRepository.existsBySenderAndReceiverAndStatus(sender, receiver, RequestStatus.PENDING)) {
            throw new DuplicateRequestException("Friend request already sent");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(RequestStatus.PENDING);

        friendRequestRepository.save(friendRequest);
        notificationService.sendFriendRequestNotification(senderId, receiverId);
    }

    @Transactional
    public void acceptFriendRequest(Long requestId, Long userId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        // 요청을 받은 사용자가 맞는지 확인
        if (!friendRequest.getReceiver().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to accept this request");
        }

        // 올바른 요청 상태인지 확인
        if (friendRequest.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestStateException("Request already " + friendRequest.getStatus().toString().toLowerCase());
        }

        // 이미 친구 관계인지 확인
        if (friendshipRepository.existsFriendship(friendRequest.getSender().getId(), friendRequest.getReceiver().getId())) {
            throw new DuplicateRequestException("Friendship already exists");
        }

        Friendship friendship = new Friendship();
        friendship.setUser1(friendRequest.getSender());
        friendship.setUser2(friendRequest.getReceiver());

        friendshipRepository.save(friendship);
        friendRequest.setStatus(RequestStatus.ACCEPTED);
        friendRequestRepository.save(friendRequest);
        notificationService.sendFriendAddedNotification(friendRequest.getReceiver().getId(), friendRequest.getSender().getId());
    }

    @Transactional
    public void rejectFriendRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        // 요청을 받은 사용자가 맞는지 확인
        if (!request.getReceiver().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to reject this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestStateException("Request already processed");
        }

        request.setStatus(RequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }

    public List<UserDTO> getFriends(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return friendshipRepository.findFriendsByUserId(userId);
    }

    public List<FriendRequestDTO> getFriendRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return friendRequestRepository.findByReceiverId(userId).stream()
                .map(request -> {
                    FriendRequestDTO dto = new FriendRequestDTO();
                    dto.setId(request.getId());
                    dto.setSenderId(request.getSender().getId());
                    dto.setSenderUsername(request.getSender().getUsername());
                    dto.setStatus(request.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        // 친구 관계가 실제로 존재하는지 확인
        Friendship friendship = friendshipRepository.findByUserIds(userId, friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship not found"));

        // 삭제 권한이 있는지 확인 (양쪽 모두 삭제 가능)
        if (!friendship.getUser1().getId().equals(userId) && !friendship.getUser2().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to remove this friendship");
        }

        friendshipRepository.delete(friendship);
    }

    public RequestStatus getFriendRequestStatus(Long requestId, Long userId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        // 요청의 발신자나 수신자만 상태를 조회할 수 있음
        if (!friendRequest.getSender().getId().equals(userId) &&
                !friendRequest.getReceiver().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to view this request");
        }

        return friendRequest.getStatus();
    }

    public List<User> recommendFriends(Long userId) {
        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 공통 친구 수 기반으로 추천 (최대 10명)
        return userRepository.findRecommendedUsers(userId, PageRequest.of(0, 10));
    }
}