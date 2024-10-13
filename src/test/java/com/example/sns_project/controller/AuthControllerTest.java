package com.example.sns_project.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.sns_project.model.User;
import com.example.sns_project.repository.RoleRepository;
import com.example.sns_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // test 프로파일 활성화
@Transactional // 트랜잭션 사용
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository; // 역할 레포지토리 추가

    @BeforeEach
    public void setup() {
//        userRepository.deleteAll(); // 매 테스트마다 사용자 초기화
//        roleRepository.deleteAll(); // 매 테스트마다 역할 초기화
        // DatabaseInitializer에 의해 역할이 생성되므로 이 부분은 생략 가능
    }

    @Test
    public void testRegisterIntegration() throws Exception {
        // 실제 데이터베이스에 저장되는지 테스트
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser02\", \"email\":\"test02@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser02"))
                .andExpect(jsonPath("$.email").value("test02@example.com"));

        // 추가: 새로 등록된 사용자에게 역할을 할당
        User newUser = userRepository.findByUsername("testuser02").orElseThrow();
        newUser.getRoles().add(roleRepository.findByName("ROLE_USER").orElseThrow()); // 역할 할당
        userRepository.save(newUser); // 역할이 할당된 사용자 저장
    }

    @Test
    public void testLoginIntegration() throws Exception {
        // 먼저 사용자 등록
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser03\", \"email\":\"test03@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isOk());

        // 추가: 새로 등록된 사용자에게 역할을 할당
        User newUser = userRepository.findByUsername("testuser03").orElseThrow();
        newUser.getRoles().add(roleRepository.findByName("ROLE_USER").orElseThrow()); // 역할 할당
        userRepository.save(newUser); // 역할이 할당된 사용자 저장

        // 로그인 테스트
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser03\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("로그인 성공")); // 로그인 성공 메시지
    }
}
