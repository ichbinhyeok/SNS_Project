package com.example.sns_project.controller;

// 사용자 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.dto.UserPasswordUpdateDTO; // 비밀번호 수정 DTO 추가
import com.example.sns_project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    public ResponseEntity<UserDTO> getMyInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "다른 사용자 정보 조회", description = "다른 사용자의 기본 정보를 조회합니다.")
    public ResponseEntity<UserDTO> getOtherUserInfo(
            @Parameter(description = "사용자 ID") @PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getPublicUserInfo(id));
    }

    @PutMapping("/me")
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 기본 정보를 수정합니다.")
    public ResponseEntity<UserDTO> updateMyInfo(
            HttpServletRequest request,
            @Parameter(description = "사용자 정보") @RequestBody UserDTO userDTO) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userService.updateUserInfo(userId, userDTO));
    }

    @PutMapping("/me/password")
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다.")
    public ResponseEntity<Void> updateMyPassword(
            HttpServletRequest request,
            @Parameter(description = "비밀번호 수정 정보") @RequestBody UserPasswordUpdateDTO passwordDTO) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, passwordDTO);
        return ResponseEntity.ok().build();
    }
}