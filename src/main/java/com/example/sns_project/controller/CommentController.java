package com.example.sns_project.controller;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    public ResponseEntity<CommentDTO> createComment(
            HttpServletRequest request,
            @RequestBody CommentDTO commentDTO) {
        Long authorId = (Long) request.getAttribute("userId");
        commentDTO.setAuthorId(authorId); // 토큰에서 추출한 사용자 ID를 작성자로 설정
        CommentDTO createdComment = commentService.createComment(commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글에 대한 댓글 조회", description = "특정 게시글의 모든 댓글을 조회합니다.")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    public ResponseEntity<CommentDTO> updateComment(
            HttpServletRequest request,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId,
            @RequestBody CommentDTO commentDTO) {
        Long authorId = (Long) request.getAttribute("userId");
        // 수정 권한 확인 (작성자와 현재 사용자가 같은지)
        commentDTO.setId(commentId);
        commentDTO.setAuthorId(authorId);
        CommentDTO updatedComment = commentService.updateComment(commentDTO);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    public ResponseEntity<Void> deleteComment(
            HttpServletRequest request,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId) {
        Long authorId = (Long) request.getAttribute("userId");
        commentService.deleteComment(commentId, authorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like")
    @Operation(summary = "댓글 좋아요", description = "특정 댓글에 좋아요를 추가합니다.")
    public ResponseEntity<Void> likeComment(
            HttpServletRequest request,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.likeComment(commentId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{commentId}/like")
    @Operation(summary = "댓글 좋아요 취소", description = "특정 댓글의 좋아요를 취소합니다.")
    public ResponseEntity<Void> unlikeComment(
            HttpServletRequest request,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.unlikeComment(commentId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{parentCommentId}/replies")
    @Operation(summary = "대댓글 추가", description = "특정 댓글에 대댓글을 추가합니다.")
    public ResponseEntity<CommentDTO> addReply(
            HttpServletRequest request,
            @Parameter(description = "부모 댓글 ID") @PathVariable("parentCommentId") Long parentCommentId,
            @RequestBody CommentDTO replyDTO) {
        Long authorId = (Long) request.getAttribute("userId");
        replyDTO.setAuthorId(authorId);
        CommentDTO replyComment = commentService.addReply(parentCommentId, replyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(replyComment);
    }

    @GetMapping("/{parentCommentId}/replies")
    @Operation(summary = "대댓글 조회", description = "특정 댓글에 대한 대댓글을 조회합니다.")
    public ResponseEntity<List<CommentDTO>> getReplies(
            @Parameter(description = "부모 댓글 ID") @PathVariable("parentCommentId") Long parentCommentId) {
        List<CommentDTO> replies = commentService.getReplies(parentCommentId);
        return ResponseEntity.ok(replies);
    }

    @PostMapping("/replies/{replyId}/like")
    @Operation(summary = "대댓글 좋아요", description = "특정 대댓글에 좋아요를 추가합니다.")
    public ResponseEntity<Void> likeReply(
            HttpServletRequest request,
            @Parameter(description = "대댓글 ID") @PathVariable("replyId") Long replyId) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.likeComment(replyId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/replies/{replyId}/like")
    @Operation(summary = "대댓글 좋아요 취소", description = "특정 대댓글의 좋아요를 취소합니다.")
    public ResponseEntity<Void> unlikeReply(
            HttpServletRequest request,
            @Parameter(description = "대댓글 ID") @PathVariable("replyId") Long replyId) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.unlikeComment(replyId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}