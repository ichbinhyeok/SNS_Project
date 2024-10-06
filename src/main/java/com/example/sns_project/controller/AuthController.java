package com.example.sns_project.controller;

// 사용자 인증 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.LoginRequest;  // LoginRequest DTO 임포트
import com.example.sns_project.dto.UserDTO;       // UserDTO 임포트
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
        // 로그인 처리 로직
        String token = authService.login(loginRequest);  // 로그인 후 JWT 토큰 반환
        return ResponseEntity.ok(token);  // JWT 토큰을 응답으로 반환
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        // 회원가입 처리 로직
        UserDTO registeredUser = authService.register(userDTO);
        return ResponseEntity.ok(registeredUser);  // 등록된 사용자 정보를 응답으로 반환
    }

    // 앞으로: 예외 처리 및 응답 형식 정의 추가 필요
}
