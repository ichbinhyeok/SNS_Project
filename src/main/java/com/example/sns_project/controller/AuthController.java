package com.example.sns_project.controller;

// 사용자 인증 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.LoginRequest;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.dto.UserRegistrationDTO;
import com.example.sns_project.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "로그인", description = "사용자가 로그인합니다.")
    public ResponseEntity<String> login(@Parameter(description = "로그인 요청 데이터") @RequestBody LoginRequest loginRequest) {
        String response = authService.login(loginRequest);
        return ResponseEntity.ok(response);  // 로그인 결과 반환
    }

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새 사용자를 등록합니다.")
    public ResponseEntity<UserDTO> register(@Parameter(description = "회원가입 요청 데이터") @RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserDTO registeredUser = authService.register(userRegistrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);  // 등록된 사용자 정보를 반환
    }

    // 앞으로: 예외 처리 및 응답 형식 정의 추가 필요
}
