package com.example.sns_project.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // test 프로파일 활성화
@Transactional // 트랜잭션을 사용하여 테스트
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Post testPost;

    @BeforeEach
    public void setup() {
        // 테스트용 사용자 생성 및 저장
        testUser = userRepository.findByUsername("admin").orElseGet(() -> {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@example.com");
            user.setPassword("password"); // 실제로는 비밀번호를 암호화해야 함
            return userRepository.save(user);
        });

        // 테스트용 포스트 초기화
        testPost = new Post();
        testPost.setTitle("Test Post");
        testPost.setContent("This is a test post.");
        testPost.setAuthorId(testUser.getId()); // authorId 설정
        testPost = postRepository.save(testPost); // 데이터베이스에 저장
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증
    @DisplayName("Post Insert")
    public void testCreatePost() throws Exception {
        // JSON 형태의 요청 본문 설정
        String newPostJson = "{\"title\":\"New Test Post\", \"content\":\"This is a new test post.\", \"authorId\":" + testUser.getId() + "}";

        // MockMvc를 사용하여 POST 요청 수행
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPostJson)) // JSON 문자열 전송
                .andExpect(status().isCreated()) // 상태 코드 201 기대
                .andExpect(jsonPath("$.title").value("New Test Post")) // 반환된 제목 확인
                .andExpect(jsonPath("$.content").value("This is a new test post.")); // 반환된 내용 확인
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증
    @DisplayName("PostId로 Post가져오기")
    public void testGetPostById() throws Exception {
        mockMvc.perform(get("/api/posts/{id}", testPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testPost.getTitle()))
                .andExpect(jsonPath("$.content").value(testPost.getContent()));
    }



    // @AfterEach 메서드가 필요 없음
}
