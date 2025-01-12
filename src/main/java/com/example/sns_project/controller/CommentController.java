package com.example.sns_project.controller;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.dto.CommentHierarchyDTO;
import com.example.sns_project.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;


import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Operation(summary = "댓글 작성", description = "게시글에 댓글이나 대댓글을 작성합니다.")
    public ResponseEntity<CommentDTO> createComment(
            HttpServletRequest request,
            @Parameter(description = "부모 댓글 ID (대댓글인 경우)")
            @RequestParam(required = false) Long parentId,
            @RequestBody CommentDTO commentDTO) {
        Long authorId = (Long) request.getAttribute("userId");
        commentDTO.setAuthorId(authorId);
        CommentDTO createdComment = commentService.createComment(parentId, commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "루트 댓글 조회", description = "게시글의 루트 댓글을 페이징하여 조회합니다.")
    public ResponseEntity<Page<CommentHierarchyDTO>> getRootComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").ascending());
        return ResponseEntity.ok(commentService.getRootComments(postId, pageable));
    }


    @GetMapping("/{commentId}/replies")
    @Operation(summary = "자식 댓글 전체 조회", description = "특정 댓글의 모든 하위 댓글을 조회합니다.")
    public ResponseEntity<List<CommentHierarchyDTO>> getAllChildComments(
            @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getAllChildComments(commentId));
    }



    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글 또는 대댓글을 수정합니다.")
    public ResponseEntity<CommentDTO> updateComment(
            HttpServletRequest request,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @RequestBody CommentDTO commentDTO) {
        Long authorId = (Long) request.getAttribute("userId");
        commentDTO.setId(commentId);
        commentDTO.setAuthorId(authorId);
        CommentDTO updatedComment = commentService.updateComment(commentDTO);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글 또는 대댓글을 삭제합니다.")
    public ResponseEntity<Void> deleteComment(
            HttpServletRequest request,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        Long authorId = (Long) request.getAttribute("userId");
        commentService.deleteComment(commentId, authorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like")
    @Operation(summary = "댓글 좋아요", description = "댓글 또는 대댓글에 좋아요를 추가합니다.")
    public ResponseEntity<Void> likeComment(
            HttpServletRequest request,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.likeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/like")
    @Operation(summary = "댓글 좋아요 취소", description = "댓글 또는 대댓글의 좋아요를 취소합니다.")
    public ResponseEntity<Void> unlikeComment(
            HttpServletRequest request,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.unlikeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }
}