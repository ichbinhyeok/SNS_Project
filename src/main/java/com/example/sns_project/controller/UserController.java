package com.example.sns_project.controller;

// 사용자 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.dto.UserPasswordUpdateDTO; // 비밀번호 수정 DTO 추가
import com.example.sns_project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 정보를 조회합니다.")
    public ResponseEntity<UserDTO> getUserById(@Parameter(description = "사용자 ID") @PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // 사용자 정보 수정 (비밀번호 제외)
    @PutMapping("/{id}")
    @Operation(summary = "사용자 정보 수정", description = "사용자의 기본 정보(이름, 이메일 등)를 수정합니다.")
    public ResponseEntity<UserDTO> updateUserInfo(
            @Parameter(description = "사용자 ID") @PathVariable("id") Long id,
            @Parameter(description = "사용자 정보") @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUserInfo(id, userDTO));
    }

    // 비밀번호 변경
    @PutMapping("/{id}/password")
    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "사용자 ID") @PathVariable("id") Long id,
            @Parameter(description = "비밀번호 수정 정보") @RequestBody UserPasswordUpdateDTO passwordDTO) {
        userService.updatePassword(id, passwordDTO);
        return ResponseEntity.ok().build();
    }



    // 앞으로: 유효성 검사 및 예외 처리 추가 필요
}
