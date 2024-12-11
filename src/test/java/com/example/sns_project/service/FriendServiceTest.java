package com.example.sns_project.service;

import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.enums.RequestStatus;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.model.FriendRequest;
import com.example.sns_project.model.Friendship;
import com.example.sns_project.model.Notification;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.FriendRequestRepository;
import com.example.sns_project.repository.FriendshipRepository;
import com.example.sns_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FriendServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendRequestRepository friendRequestRepository;
    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private FriendService friendService;

    private User sender; // 친구 요청을 보내는 사용자
    private User receiver; // 친구 요청을 받는 사용자
    private UserDTO receiverDTO;
    private FriendRequest friendRequest;
    private Friendship friendship;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 사용자 객체 초기화
        sender = new User();
        sender.setId(1L);
        sender.setUsername("sender");

        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("receiver");

        friendRequest = new FriendRequest();
        friendRequest.setId(1L);
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(RequestStatus.PENDING);


        friendship = new Friendship();
        friendship.setUser1(sender);
        friendship.setUser2(receiver);

    }

    @Test
    @DisplayName("친구 보내기 요청_성공")
    public void testSendFriendRequest_Success() {
        when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        friendService.sendFriendRequest(sender.getId(), receiver.getId());

        verify(friendRequestRepository).save(any(FriendRequest.class));

        verify(notificationService).sendFriendRequestNotification(sender.getId(), receiver.getId());


    }


    @Test
    @DisplayName("친구 요청 보내기_실패_Sender not found")
    public void testSendFriendRequest_SenderNotFound() {
        when(userRepository.findById(sender.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                friendService.sendFriendRequest(sender.getId(), receiver.getId()));

        assertThat(exception.getMessage()).isEqualTo("Sender not found");
    }

    @Test
    @DisplayName("친구 요청 보내기_실패_Receiver not found")
    public void testSendFriendRequest_ReceiverNotFound() {
        when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                friendService.sendFriendRequest(sender.getId(), receiver.getId()));

        assertThat(exception.getMessage()).isEqualTo("Receiver not found");
    }


    @Test
    @DisplayName("친구 요청 수락_성공")
    public void testAcceptFriendRequest() {
        // Given
        friendRequest.setStatus(RequestStatus.PENDING);
        when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));
        when(friendshipRepository.existsByUser1IdAndUser2Id(anyLong(), anyLong())).thenReturn(false);

        // When
        friendService.acceptFriendRequest(friendRequest.getId());

        // Then
        verify(friendshipRepository).save(any(Friendship.class));
        assertThat(friendRequest.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
        verify(notificationService).sendFriendAddedNotification(
                friendRequest.getReceiver().getId(),
                friendRequest.getSender().getId());
    }

    @Test
    @DisplayName("친구 요청 수락_실패_이미 수락된 요청")
    public void testAcceptFriendRequest_AlreadyAccepted() {
        friendRequest.setStatus(RequestStatus.ACCEPTED);
        when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                friendService.acceptFriendRequest(friendRequest.getId()));
        assertThat(exception.getMessage()).isEqualTo("Friend request is accepted");
    }

    @Test
    @DisplayName("친구 요청 수락_실패_이미 거절된 요청")
    public void testAcceptFriendRequest_AlreadyRejected() {
        friendRequest.setStatus(RequestStatus.REJECTED);
        when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                friendService.acceptFriendRequest(friendRequest.getId()));
        assertThat(exception.getMessage()).isEqualTo("Friend request is rejected");
    }

    @Test
    @DisplayName("친구 요청 수락_실패_이미 친구인 경우")
    public void testAcceptFriendRequest_AlreadyFriends() {
        friendRequest.setStatus(RequestStatus.PENDING);
        when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));
        when(friendshipRepository.existsByUser1IdAndUser2Id(anyLong(), anyLong())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                friendService.acceptFriendRequest(friendRequest.getId()));
        assertThat(exception.getMessage()).isEqualTo("Friendship already exists");
    }


    @Test
    @DisplayName("친구 요청 수락_실패_FriendRequest not found")
    public void testAcceptFriendRequest_FriendRequestNotFound() {
        when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                friendService.acceptFriendRequest(friendRequest.getId()));

        assertThat(exception.getMessage()).isEqualTo("Friend request not found");
    }

    @Test
    @DisplayName("친구 요청 거절_성공")
    public void testRejectFriendRequest() {
        when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));
        friendService.rejectFriendRequest(friendRequest.getId());
        assertThat(friendRequest.getStatus()).isEqualTo(RequestStatus.REJECTED);
        verify(friendRequestRepository).save(any(FriendRequest.class));
    }

    @Test
    @DisplayName("친구 목록 조회_성공")
    public void testGetFriends(){
        when(friendshipRepository.findFriendsByUserId(sender.getId())).thenReturn(List.of(receiverDTO));
        List<UserDTO> friends = friendService.getFriends(sender.getId());
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getUsername()).isEqualTo(receiver.getUsername());

    }

    @Test
    @DisplayName("친구 삭제_성공")
    public void testRemoveFriend(){
        when(friendshipRepository.findByUserIds(sender.getId(), receiver.getId())).thenReturn(Optional.of(friendship));
        friendService.removeFriend(sender.getId(), receiver.getId());

        verify(friendshipRepository).delete(friendship);

        // 삭제 후 친구 관계를 조회할 때 Optional.empty()가 반환되는지 검증
        when(friendshipRepository.findByUserIds(sender.getId(), receiver.getId())).thenReturn(Optional.empty());
        Optional<Friendship> deletedFriendship = friendshipRepository.findByUserIds(sender.getId(), receiver.getId());

        assertThat(deletedFriendship).isEmpty(); // 친구 관계가 삭제되었는지 확인
    }


    @Test
    @DisplayName("친구 삭제_실패_Friendship not found")
    public void testRemoveFriend_FriendshipNotFound() {
        when(friendshipRepository.findByUserIds(sender.getId(), receiver.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                friendService.removeFriend(sender.getId(), receiver.getId()));

        assertThat(exception.getMessage()).isEqualTo("Friendship not found");
    }


    @Test
    @DisplayName("친구 요청 상태 조회_성공")
    public void testGetFriendRequestStatus_Success() {
        when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));

        RequestStatus status = friendService.getFriendRequestStatus(friendRequest.getId());

        assertThat(status).isEqualTo(RequestStatus.PENDING);
    }

    @Test
    @DisplayName("친구 요청 상태 조회_실패_FriendRequest not found")
    public void testGetFriendRequestStatus_FriendRequestNotFound() {
        when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                friendService.getFriendRequestStatus(friendRequest.getId()));

        assertThat(exception.getMessage()).isEqualTo("Friend request not found");
    }

    @Test
    @DisplayName("친구 요청 목록 조회_성공")
    public void testGetFriendRequests_Success() {
        when(friendRequestRepository.findByReceiverId(receiver.getId())).thenReturn(List.of(friendRequest));

        var requests = friendService.getFriendRequests(receiver.getId());

        assertThat(requests).containsExactly(friendRequest);
    }

    @Test
    @DisplayName("친구 추천 기능_성공")
    public void testRecommendFriends_Success() {
        when(userRepository.findNonFriendsByUserId(sender.getId())).thenReturn(List.of(receiver));

        var recommendations = friendService.recommendFriends(sender.getId());

        assertThat(recommendations).containsExactly(receiver);
    }


}
