package com.example.sns_project.service;

import com.example.sns_project.dto.NotificationDTO;
import com.example.sns_project.enums.NotificationType;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.model.Notification;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.NotificationRepository;
import com.example.sns_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 User와 Notification 객체 설정
        user = new User();
        user.setId(1L);

        notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setMessage("Test message");
        notification.setRead(false);
        notification.setNotificationType(NotificationType.DEFAULT);
    }

    @Test
    @DisplayName("특정 사용자의 모든 알림을 조회")
    void testGetUserNotifications() {
        // 지정된 사용자 ID로 알림 리스트를 반환하도록 Mock 설정
        when(notificationRepository.findByUserId(user.getId())).thenReturn(Arrays.asList(notification));
        // 서비스 메서드 호출
        List<NotificationDTO> notifications = notificationService.getUserNotifications(user.getId());
        // 결과 검증
        assertEquals(1, notifications.size());
        assertEquals("Test message", notifications.get(0).getMessage());
        assertFalse(notifications.get(0).isRead());
    }

    @Test
    @DisplayName("알림 전송 성공 테스트")
    void testSendNotification() {
        // 사용자 ID로 사용자 찾기를 성공하도록 Mock 설정
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        // 알림 전송 호출
        notificationService.sendNotification(user.getId(), "Test message", NotificationType.COMMENT);
        // 알림 저장 메서드가 한 번 호출되었는지 검증
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자에게 알림 전송 시 예외 발생")
    void testSendNotificationUserNotFound() {
        // 존재하지 않는 사용자로 설정
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        // 예외 발생 검증
        assertThrows(ResourceNotFoundException.class, () -> notificationService.sendNotification(user.getId(), "Test message", NotificationType.COMMENT));
    }

    @Test
    @DisplayName("알림을 읽음 상태로 마킹")
    void testMarkAsRead() {
        // 알림 ID로 알림 찾기 성공하도록 Mock 설정
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        // 읽음 상태로 마킹
        notificationService.markAsRead(notification.getId());
        // 알림이 읽음 상태로 설정되었는지 확인하고, 저장 메서드가 호출되었는지 검증
        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("존재하지 않는 알림 ID로 읽음 마킹 시 예외 발생")
    void testMarkAsReadNotificationNotFound() {
        // 알림 찾기 실패 설정
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.empty());
        // 예외 발생 검증
        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(notification.getId()));
    }

    @Test
    @DisplayName("알림 삭제 성공")
    void testDeleteNotification() {
        // 알림이 존재하는 상태로 설정
        when(notificationRepository.existsById(notification.getId())).thenReturn(true);
        // 알림 삭제 호출
        notificationService.deleteNotification(notification.getId());
        // 삭제 메서드가 호출되었는지 확인
        verify(notificationRepository, times(1)).deleteById(notification.getId());
    }

    @Test
    @DisplayName("존재하지 않는 알림 ID로 삭제 시 예외 발생")
    void testDeleteNotificationNotFound() {
        // 알림이 존재하지 않는 상태로 설정
        when(notificationRepository.existsById(notification.getId())).thenReturn(false);
        // 예외 발생 검증
        assertThrows(ResourceNotFoundException.class, () -> notificationService.deleteNotification(notification.getId()));
    }

    @Test
    @DisplayName("읽지 않은 알림만 조회")
    void testGetUnreadNotifications() {
        // 읽지 않은 알림 리스트 반환하도록 Mock 설정
        when(notificationRepository.findByUserIdAndIsRead(user.getId(), false)).thenReturn(Arrays.asList(notification));
        // 읽지 않은 알림 조회 호출
        List<NotificationDTO> unreadNotifications = notificationService.getUnreadNotifications(user.getId());
        // 읽지 않은 알림 목록 확인
        assertEquals(1, unreadNotifications.size());
        assertFalse(unreadNotifications.get(0).isRead());
    }

    @Test
    @DisplayName("포스트에 좋아요 알림 전송 성공")
    void testSendPostLikeNotification() {
        // 사용자 ID로 사용자 찾기 성공하도록 Mock 설정
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        // 포스트 좋아요 알림 전송 호출
        notificationService.sendPostLikeNotification(user.getId(), "John");
        // 알림 저장 메서드가 한 번 호출되었는지 검증
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
