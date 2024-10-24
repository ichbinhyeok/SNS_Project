package com.example.sns_project.controller;

// 사용자 인증 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.LoginRequest;  // LoginRequest DTO 임포트
import com.example.sns_project.dto.UserDTO;       // UserDTO 임포트
import com.example.sns_project.dto.UserRegistrationDTO;
import com.example.sns_project.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;  // AuthService 의존성 주입

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // Spring Security에 의해 처리되므로, 별도로 로그인 처리가 필요 없음
        return ResponseEntity.ok("로그인 성공");  // 로그인 성공 메시지 반환
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        // 회원가입 처리 로직
        UserDTO registeredUser = authService.register(userRegistrationDTO);
        return ResponseEntity.ok(registeredUser);  // 등록된 사용자 정보를 응답으로 반환
    }


    // 앞으로: 예외 처리 및 응답 형식 정의 추가 필요
}
