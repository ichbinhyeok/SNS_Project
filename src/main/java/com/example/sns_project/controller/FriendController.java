package com.example.sns_project.controller;

import com.example.sns_project.dto.FriendRequestDTO;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.enums.RequestStatus;
import com.example.sns_project.model.FriendRequest;
import com.example.sns_project.model.User;
import com.example.sns_project.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/request/{receiverId}")
    @Operation(summary = "친구 요청 보내기", description = "친구 요청을 보냅니다.")
    public ResponseEntity<Void> sendFriendRequest(
            HttpServletRequest request,
            @Parameter(description = "받는 사용자 ID") @PathVariable Long receiverId) {
        Long senderId = (Long) request.getAttribute("userId");
        friendService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{requestId}")
    @Operation(summary = "친구 요청 수락", description = "친구 요청을 수락합니다.")
    public ResponseEntity<Void> acceptFriendRequest(
            HttpServletRequest request,
            @Parameter(description = "친구 요청 ID") @PathVariable Long requestId) {
        Long userId = (Long) request.getAttribute("userId");
        friendService.acceptFriendRequest(requestId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{requestId}")
    @Operation(summary = "친구 요청 거절", description = "친구 요청을 거절합니다.")
    public ResponseEntity<Void> rejectFriendRequest(
            HttpServletRequest request,
            @Parameter(description = "친구 요청 ID") @PathVariable Long requestId) {
        Long userId = (Long) request.getAttribute("userId");
        friendService.rejectFriendRequest(requestId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-friends")
    @Operation(summary = "내 친구 목록 조회", description = "현재 사용자의 친구 목록을 조회합니다.")
    public ResponseEntity<List<UserDTO>> getMyFriends(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<UserDTO> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/my-requests")
    @Operation(summary = "내 친구 요청 목록 조회", description = "현재 사용자의 친구 요청 목록을 조회합니다.")
    public ResponseEntity<List<FriendRequestDTO>> getMyFriendRequests(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<FriendRequestDTO> requests = friendService.getFriendRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @DeleteMapping("/remove/{friendId}")
    @Operation(summary = "친구 삭제", description = "특정 친구를 삭제합니다.")
    public ResponseEntity<Void> removeFriend(
            HttpServletRequest request,
            @Parameter(description = "삭제할 친구 ID") @PathVariable Long friendId) {
        Long userId = (Long) request.getAttribute("userId");
        friendService.removeFriend(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/request-status/{requestId}")
    @Operation(summary = "친구 요청 상태 조회", description = "특정 친구 요청의 상태를 조회합니다.")
    public ResponseEntity<RequestStatus> getFriendRequestStatus(
            HttpServletRequest request,
            @Parameter(description = "친구 요청 ID") @PathVariable Long requestId) {
        Long userId = (Long) request.getAttribute("userId");
        RequestStatus status = friendService.getFriendRequestStatus(requestId, userId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/recommendations")
    @Operation(summary = "친구 추천", description = "친구가 될 수 있는 사용자 목록을 추천합니다.")
    public ResponseEntity<List<User>> recommendFriends(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<User> recommendations = friendService.recommendFriends(userId);
        return ResponseEntity.ok(recommendations);
    }
}