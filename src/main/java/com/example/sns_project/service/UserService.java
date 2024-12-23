package com.example.sns_project.service;

// 사용자 관련 비즈니스 로직을 처리하는 서비스
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.dto.UserPasswordUpdateDTO;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.exception.UnauthorizedException;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // 전체 사용자 정보 조회 (인증된 사용자용)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }

    // 공개 사용자 정보 조회 (다른 사용자 조회용)
    public UserDTO getPublicUserInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // 공개 정보만 반환 (이메일 등 민감한 정보 제외)
        return new UserDTO(user.getId(), user.getUsername(), null);
    }

    // 사용자 정보 수정
    @Transactional
    public UserDTO updateUserInfo(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        userRepository.save(user);
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }
    // 비밀번호 변경
    @Transactional
    public void updatePassword(Long id, UserPasswordUpdateDTO passwordDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (passwordDTO.getPassword() == null || passwordDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
        userRepository.save(user);
    }

    // 내부 사용을 위한 사용자 조회
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // 랜덤 사용자 선택 (필요한 경우에만 사용)
    public User randomSelectUser() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        Collections.shuffle(users);
        return users.get(0);
    }
}