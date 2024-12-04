package com.example.sns_project.service;

// 사용자 관련 비즈니스 로직을 처리하는 서비스
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.dto.UserPasswordUpdateDTO;
import com.example.sns_project.exception.ResourceNotFoundException;
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

    private final UserRepository userRepository;  // UserRepository 의존성 주입
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더

    private final Faker faker = new Faker();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // 비밀번호 인코더 초기화
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }

    // 사용자 정보 수정
    public UserDTO updateUserInfo(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        userRepository.save(user);

        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }

    // 비밀번호 변경
    public void updatePassword(Long id, UserPasswordUpdateDTO passwordDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (passwordDTO.getPassword() != null && !passwordDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
        } else {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        userRepository.save(user);
    }
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


    public User randomSelectUser(){


        List<User> users = userRepository.findAll();
        Collections.shuffle(users);

        return users.get(0);


    }





    // 앞으로: 유효성 검사 및 예외 처리 추가
}
