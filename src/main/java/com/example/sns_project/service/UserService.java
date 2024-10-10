package com.example.sns_project.service;

// 사용자 관련 비즈니스 로직을 처리하는 서비스
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;  // UserRepository 의존성 주입
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // 비밀번호 인코더 초기화
    }

    public UserDTO getUserById(Long id) {
        // 사용자 정보 조회 로직
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")); // 사용자 조회

        // User 엔티티를 UserDTO로 변환하여 반환
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), null);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        // 사용자 정보 수정 로직
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")); // 사용자 조회

        // 수정할 정보 설정
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // 비밀번호 암호화
        }

        // 사용자 정보 저장
        userRepository.save(user);

        // 수정된 사용자 DTO 반환
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), null);
    }

    // 앞으로: 유효성 검사 및 예외 처리 추가
}
