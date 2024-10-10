package com.example.sns_project.controller.mock;

import com.example.sns_project.controller.UserController;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class MockUserControllerTest {

    @Mock
    private UserService userService; // UserService 모의 객체

    @InjectMocks
    private UserController userController; // UserController 인스턴스에 모의 객체 주입

    public MockUserControllerTest() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    @DisplayName("(모의)사용자 ID로 조회 테스트")
    public void testGetUserById() {
        UserDTO userDTO = new UserDTO(1L, "testuser", "test@example.com", "password123");

        when(userService.getUserById(anyLong())).thenReturn(userDTO); // 모의 서비스 메서드

        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    @DisplayName("(모의)사용자 정보 수정 테스트")
    public void testUpdateUser() {
        UserDTO userDTO = new UserDTO(1L, "updatedUser", "updated@example.com", "newPassword");

        // userDTO를 매처로 사용하여 모의 서비스 메서드
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updateUser(1L, userDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDTO, response.getBody());
    }
}
