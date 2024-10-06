package com.example.sns_project.service;

// 사용자 인증 및 권한 관리를 처리하는 서비스
import com.example.sns_project.dto.LoginRequest;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;  // UserRepository 의존성 주입

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(LoginRequest loginRequest) {
        // 로그인 처리 로직 (비밀번호 확인, JWT 발급 등)
        return "JWT_TOKEN";  // JWT 토큰 반환
    }

    public UserDTO register(UserDTO userDTO) {
        // 사용자 등록 처리 로직 (유효성 검사 후 저장)
        return userDTO;  // 등록된 사용자 반환
    }

    // 앞으로: 비밀번호 암호화 및 검증 로직 추가
}
