package com.example.sns_project.service;

// 사용자 관련 비즈니스 로직을 처리하는 서비스
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;  // UserRepository 의존성 주입

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO getUserById(Long id) {
        // 사용자 정보 조회 로직
        return new UserDTO();  // 조회된 사용자 반환
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        // 사용자 정보 수정 로직
        return userDTO;  // 수정된 사용자 반환
    }

    // 앞으로: 유효성 검사 및 예외 처리 추가
}
