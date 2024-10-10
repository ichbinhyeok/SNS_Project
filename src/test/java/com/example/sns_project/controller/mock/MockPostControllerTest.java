//package com.example.sns_project.controller.mock;
//
//import com.example.sns_project.controller.PostController;
//import com.example.sns_project.dto.PostDTO;
//import com.example.sns_project.model.Post;
//import com.example.sns_project.model.User;
//import com.example.sns_project.repository.PostRepository;
//import com.example.sns_project.repository.UserRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class MockPostControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc; // MockMvc를 자동으로 주입받음
//
//    @Mock
//    private PostRepository postRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private PostController postController; // 테스트할 컨트롤러
//
//    private ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기
//
//    private User testUser; // 작성자를 위한 User 객체
//
//    @BeforeEach
//    public void setUp() {
//        // Mock 사용자 설정
//        testUser = new User();
//        testUser.setId(1L);
//        testUser.setUsername("admin");
//        testUser.setEmail("admin@example.com");
//        testUser.setPassword("password");
//
//        when(userRepository.findByUsername("admin")).thenReturn(java.util.Optional.of(testUser));
//    }
//
//    @Test
//    @DisplayName("게시글 작성 및 조회 통합 테스트")
//    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증 추가
//    public void testCreateAndGetPost() throws Exception {
//        // Given
//        PostDTO postDTO = new PostDTO(null, "Test Title", "Test Content", testUser.getId());
//        Post savedPost = new Post(1L, "Test Title", "Test Content", testUser.getId());
//
//        // Mock 설정
//        when(postRepository.save(any(Post.class))).thenReturn(savedPost); // 게시글 저장 시 Mock 동작 설정
//        when(postRepository.findById(savedPost.getId())).thenReturn(java.util.Optional.of(savedPost)); // 게시글 조회 시 Mock 동작 설정
//
//        // When: 게시글 작성 요청
//        mockMvc.perform(post("/api/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(postDTO)))
//                .andExpect(status().isOk());
//
//        // Then: 게시글 조회 요청
//        mockMvc.perform(get("/api/posts/{id}", savedPost.getId())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(result -> {
//                    PostDTO foundPostDTO = objectMapper.readValue(result.getResponse().getContentAsString(), PostDTO.class);
//                    assertEquals("Test Title", foundPostDTO.getTitle());
//                    assertEquals("Test Content", foundPostDTO.getContent());
//                    assertEquals(testUser.getId(), foundPostDTO.getAuthorId());
//                });
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 게시글 조회 시 404 응답 테스트")
//    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증 추가
//    public void testGetPostByIdNotFound() throws Exception {
//        // When & Then: 존재하지 않는 게시글 조회
//        mockMvc.perform(get("/api/posts/{id}", 999L)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());  // 404 상태 코드 검증
//    }
//}
