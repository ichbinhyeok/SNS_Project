package com.example.sns_project.controller;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.model.Comment;
import com.example.sns_project.model.Post;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CommentControllerTest {

    @Autowired
    private CommentController commentController;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private Long savedPostId;
    private Long savedUserId;

    @BeforeEach
    public void setUp() {
        User user = userRepository.findByUsername("admin").orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(29L).orElseGet(() -> {
            Post newPost = new Post();
            newPost.setTitle("test post");
            newPost.setContent("test content");
            return postRepository.save(newPost);
        });

        savedPostId = post.getId();
        savedUserId = user.getId();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"}) // Mock 사용자 인증
    @DisplayName("댓글 생성 테스트")
    @Commit
    public void testCreateComment() throws Exception {
        String commentJson = "{\"postId\":" + savedPostId + ", \"content\":\"Test comment\", \"authorId\":" + savedUserId + "}";

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andDo(print()) // 요청 및 응답 로그 출력
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test comment"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    @DisplayName("댓글 조회 테스트")
    public void testGetCommentsByPostId() throws Exception {
        //PostID가 savedPostId인거 조회
        mockMvc.perform(get("/api/comments/" + savedPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test comment")); // 생성한 댓글 내용 확인


    }


}
