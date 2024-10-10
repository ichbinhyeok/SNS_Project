package com.example.sns_project.controller.real;

import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RealUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        // userName이 admin인 사용자 가져오기
        testUser = userRepository.findByUsername("admin")
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @AfterEach
    public void tearDown() {
        // 테스트 후 사용자 데이터 삭제 (필요시)
        // userRepository.deleteAll(); // 필요에 따라 주석 해제
    }

    @Test
    @DisplayName("사용자 ID로 조회 테스트")
    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증
    public void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자 정보 수정 테스트")
    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증
    public void testUpdateUser() throws Exception {
        UserDTO updatedUserDTO = new UserDTO(testUser.getId(), "updatedUser02", "updated02@example.com", "newPassword");

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedUserDTO)))
                .andExpect(status().isOk());
    }
}
