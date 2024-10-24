package com.example.sns_project.controller;

import com.example.sns_project.enums.RequestStatus;
import com.example.sns_project.model.FriendRequest;
import com.example.sns_project.model.User;
import com.example.sns_project.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/request")
    @Operation(summary = "친구 요청 보내기", description = "친구 요청을 보냅니다.")
    public ResponseEntity<Void> sendFriendRequest(
            @Parameter(description = "보내는 사용자 ID") @RequestParam Long senderId,
            @Parameter(description = "받는 사용자 ID") @RequestParam Long receiverId) {
        friendService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{requestId}")
    @Operation(summary = "친구 요청 수락", description = "친구 요청을 수락합니다.")
    public ResponseEntity<Void> acceptFriendRequest(
            @Parameter(description = "친구 요청 ID") @PathVariable Long requestId) {
        friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{requestId}")
    @Operation(summary = "친구 요청 거절", description = "친구 요청을 거절합니다.")
    public ResponseEntity<Void> rejectFriendRequest(
            @Parameter(description = "친구 요청 ID") @PathVariable Long requestId) {
        friendService.rejectFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/friends")
    @Operation(summary = "친구 목록 조회", description = "특정 사용자의 친구 목록을 조회합니다.")
    public ResponseEntity<List<User>> getFriends(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        List<User> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{userId}/friend-requests")
    @Operation(summary = "친구 요청 목록 조회", description = "특정 사용자의 친구 요청 목록을 조회합니다.")
    public ResponseEntity<List<FriendRequest>> getFriendRequests(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        List<FriendRequest> requests = friendService.getFriendRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/remove")
    @Operation(summary = "친구 삭제", description = "특정 친구를 삭제합니다.")
    public ResponseEntity<Void> removeFriend(
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "삭제할 친구 ID") @RequestParam Long friendId) {
        friendService.removeFriend(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/request-status/{requestId}")
    @Operation(summary = "친구 요청 상태 조회", description = "특정 친구 요청의 상태를 조회합니다.")
    public ResponseEntity<RequestStatus> getFriendRequestStatus(
            @Parameter(description = "친구 요청 ID") @PathVariable Long requestId) {
        RequestStatus status = friendService.getFriendRequestStatus(requestId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/recommendations/{userId}")
    @Operation(summary = "친구 추천", description = "친구가 될 수 있는 사용자 목록을 추천합니다.")
    public ResponseEntity<List<User>> recommendFriends(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        List<User> recommendations = friendService.recommendFriends(userId);
        return ResponseEntity.ok(recommendations);
    }
}
