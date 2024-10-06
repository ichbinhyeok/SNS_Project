package com.example.sns_project.controller;

// 게시글 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;  // PostService 의존성 주입

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) {
        // 게시글 작성 로직
        return ResponseEntity.ok(postService.createPost(postDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        // 게시글 조회 로직
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // 앞으로: 이미지 업로드 처리 및 예외 처리 추가 필요
}
