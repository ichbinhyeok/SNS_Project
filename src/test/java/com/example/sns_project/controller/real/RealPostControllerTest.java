package com.example.sns_project.controller;

import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.PostRepository;
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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Commit // 트랜잭션을 커밋하여 데이터베이스에 저장
@AutoConfigureMockMvc
@ActiveProfiles("test") // 테스트 프로파일 활성화
public class RealPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository; // UserRepository 추가

    private ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기

    private User testUser; // 작성자를 위한 User 객체

    @BeforeEach
    public void setUp() {
        // 각 테스트 전에 데이터베이스를 초기화
//        postRepository.deleteAll();

        // username이 "admin"인 사용자 가져오기
        testUser = userRepository.findByUsername("admin")
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @AfterEach
    public void tearDown() {
        // 각 테스트 후 데이터베이스 초기화
//        postRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 작성 및 조회 통합 테스트")
    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증 추가
    public void testCreateAndGetPost() throws Exception {
        // Given
        PostDTO postDTO = new PostDTO(null, "Test Title", "Test Content", testUser.getId());

        // When: 게시글 작성 요청
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDTO)))
                .andExpect(status().isOk());

        // Then: 게시글 조회 요청
        Post createdPost = postRepository.findAll().get(0);
        mockMvc.perform(get("/api/posts/{id}", createdPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    PostDTO foundPostDTO = objectMapper.readValue(result.getResponse().getContentAsString(), PostDTO.class);
                    assertEquals("Test Title", foundPostDTO.getTitle());
                    assertEquals("Test Content", foundPostDTO.getContent());
                    assertEquals(testUser.getId(), foundPostDTO.getAuthorId());
                });
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 404 응답 테스트")
    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증 추가
    public void testGetPostByIdNotFound() throws Exception {
        // When & Then: 존재하지 않는 게시글 조회
        mockMvc.perform(get("/api/posts/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());  // 404 상태 코드 검증
    }
}
