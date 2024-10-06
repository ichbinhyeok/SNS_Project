package com.example.sns_project.controller;

// 사용자 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;  // UserService 의존성 주입

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        // 사용자 정보 조회 로직
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        // 사용자 정보 수정 로직
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    // 앞으로: 유효성 검사 및 예외 처리 추가 필요
}
