package com.example.sns_project.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePost() throws Exception {
        // Mocking post creation behavior
        when(postService.createPost(any(PostDTO.class))).thenReturn(new PostDTO());

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content("{\"title\":\"Test Post\", \"content\":\"This is a test post.\"}"))
                .andExpect(status().isCreated());
    }
}