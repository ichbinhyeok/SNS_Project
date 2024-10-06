package com.example.sns_project.controller;

// 댓글 관련 API를 처리하는 컨트롤러
import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;  // CommentService 의존성 주입

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        // 댓글 작성 로직
        return ResponseEntity.ok(commentService.createComment(commentDTO));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        // 게시글에 대한 댓글 조회 로직
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    // 앞으로: 댓글 수정 및 예외 처리 추가 필요
}
