//package com.example.sns_project.controller;
//
//import com.example.sns_project.dto.UserDTO;
//import com.example.sns_project.init.DatabaseInitializer;
//import com.example.sns_project.model.User;
//import com.example.sns_project.repository.UserRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test") // test 프로파일 활성화
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private DatabaseInitializer databaseInitializer;
//
//    private User testUser;
//
//    @BeforeEach
//    public void setUp() {
////         테스트용 사용자 생성 및 데이터베이스에 저장
//        // 기본 사용자 'admin'이 없는 경우 추가
//        if (!userRepository.findByUsername("admin").isPresent()) {
//            User admin = new User();
//            admin.setUsername("admin");
//            admin.setEmail("admin@example.com");
//            admin.setPassword("password"); // 실제로는 비밀번호를 암호화해야 함
//            userRepository.save(admin);
//        }
//
//        // 기본 사용자 가져오기
//        testUser = userRepository.findByUsername("admin")
//                .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));
//    }
//
//    @AfterEach
//    public void tearDown() {
//        // 테스트 후 사용자 데이터 삭제
//        userRepository.delete(testUser);
//    }
//
//    @Test
//    @DisplayName("사용자 ID로 조회 테스트")
//    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증
//    public void testGetUserById() throws Exception {
//        mockMvc.perform(get("/api/users/{id}", testUser.getId())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("사용자 정보 수정 테스트")
//    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증
//    public void testUpdateUser() throws Exception {
//        UserDTO updatedUserDTO = new UserDTO(testUser.getId(), "updatedUser06", "updated06@example.com", "newPassword");
//
//        mockMvc.perform(put("/api/users/{id}", testUser.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(updatedUserDTO)))
//                .andExpect(status().isOk());
//    }
//}
