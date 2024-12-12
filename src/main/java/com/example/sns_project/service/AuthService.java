package com.example.sns_project.service;

// 사용자 인증 및 권한 관리를 처리하는 서비스
import com.example.sns_project.dto.AuthResponse;
import com.example.sns_project.dto.LoginRequest;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.dto.UserRegistrationDTO; // UserRegistrationDTO 추가
import com.example.sns_project.jwt.JwtUtil;
import com.example.sns_project.model.Role; // Role 임포트
import com.example.sns_project.model.User;
import com.example.sns_project.repository.RoleRepository; // RoleRepository 임포트
import com.example.sns_project.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername());
                return new AuthResponse(token, "로그인 성공");
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    public UserDTO register(UserRegistrationDTO userRegistrationDTO) {
        // UserRegistrationDTO를 User 엔티티로 변환
        User user = new User();
        user.setUsername(userRegistrationDTO.getUsername());
        user.setEmail(userRegistrationDTO.getEmail());

        // 비밀번호를 암호화하여 저장
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));

        // 기본 역할 설정 (예: ROLE_USER)
        Role userRole = roleRepository.findByName("ROLE_USER") // ROLE_USER 역할을 가져옴
                .orElseThrow(() -> new RuntimeException("기본 역할이 존재하지 않습니다."));

        // Set<Role>으로 설정
        user.setRoles(Collections.singleton(userRole)); // 역할을 Set으로 설정

        // 사용자 저장
        userRepository.save(user);

        // UserDTO에 ID 추가하여 반환
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail()); // 등록된 사용자 DTO 반환
    }




    public List<String> generateAndRegisterUsers(int count) {
        Faker faker = new Faker();
        List<UserRegistrationDTO> userRegistrationDTOs = new ArrayList<>();
        List<String> registeredUsernames = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
            userRegistrationDTO.setUsername(faker.name().username());
            userRegistrationDTO.setEmail(faker.internet().emailAddress());
            String password = "123";
            userRegistrationDTO.setPassword(password);

//            userRegistrationDTO.setPassword(faker.internet().password());

            userRegistrationDTOs.add(userRegistrationDTO);
        }

        for (UserRegistrationDTO userRegistrationDTO : userRegistrationDTOs) {
            UserDTO registeredUser = register(userRegistrationDTO); // register 메서드는 사용자 등록 로직을 처리합니다.
            registeredUsernames.add(registeredUser.getUsername());
        }

        return registeredUsernames;
    }


    // 앞으로: 비밀번호 검증 로직 및 예외 처리 추가
}
