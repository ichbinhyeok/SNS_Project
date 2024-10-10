package com.example.sns_project.controller.mock;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // test 프로파일 활성화
public class MockAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll(); // 매 테스트마다 데이터 초기화
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
    }

    @Test
    public void testLoginIntegration() throws Exception {
        // 먼저 사용자 등록
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser03\", \"email\":\"test03@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isOk());

        // 로그인 테스트
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser03\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("로그인 성공")); // 로그인 성공 메시지
    }
}
