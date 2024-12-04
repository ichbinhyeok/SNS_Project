package com.example.sns_project.controller;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        CommentDTO createdComment = commentService.createComment(commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글에 대한 댓글 조회", description = "특정 게시글의 모든 댓글을 조회합니다.")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@Parameter(description = "게시글 ID") @PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    public ResponseEntity<CommentDTO> updateComment(
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId,
            @RequestBody CommentDTO commentDTO) {
        CommentDTO updatedComment = commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    public ResponseEntity<Void> deleteComment(@Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like/{userId}")
    @Operation(summary = "댓글 좋아요", description = "특정 댓글에 좋아요를 추가합니다.")
    public ResponseEntity<Void> likeComment(
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId,
            @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        commentService.likeComment(commentId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{commentId}/like/{userId}")
    @Operation(summary = "댓글 좋아요 취소", description = "특정 댓글의 좋아요를 취소합니다.")
    public ResponseEntity<Void> unlikeComment(
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId,
            @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        commentService.unlikeComment(commentId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{parentCommentId}/replies")
    @Operation(summary = "대댓글 추가", description = "특정 댓글에 대댓글을 추가합니다.")
    public ResponseEntity<CommentDTO> addReply(
            @Parameter(description = "부모 댓글 ID") @PathVariable("parentCommentId") Long parentCommentId,
            @RequestBody CommentDTO replyDTO) {
        CommentDTO replyComment = commentService.addReply(parentCommentId, replyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(replyComment);
    }

    @GetMapping("/{parentCommentId}/replies")
    @Operation(summary = "대댓글 조회", description = "특정 댓글에 대한 대댓글을 조회합니다.")
    public ResponseEntity<List<CommentDTO>> getReplies(@Parameter(description = "부모 댓글 ID") @PathVariable("parentCommentId") Long parentCommentId) {
        List<CommentDTO> replies = commentService.getReplies(parentCommentId);
        return ResponseEntity.ok(replies);
    }

    // 대댓글 좋아요 기능 추가
    @PostMapping("/replies/{replyId}/like/{userId}")
    @Operation(summary = "대댓글 좋아요", description = "특정 대댓글에 좋아요를 추가합니다.")
    public ResponseEntity<Void> likeReply(
            @Parameter(description = "대댓글 ID") @PathVariable("replyId") Long replyId,
            @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        commentService.likeComment(replyId, userId); // 대댓글도 댓글과 동일하게 처리
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/replies/{replyId}/like/{userId}")
    @Operation(summary = "대댓글 좋아요 취소", description = "특정 대댓글의 좋아요를 취소합니다.")
    public ResponseEntity<Void> unlikeReply(
            @Parameter(description = "대댓글 ID") @PathVariable("replyId") Long replyId,
            @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        commentService.unlikeComment(replyId, userId); // 대댓글도 댓글과 동일하게 처리
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
