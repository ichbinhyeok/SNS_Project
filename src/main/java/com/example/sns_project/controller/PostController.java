package com.example.sns_project.controller;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "게시글 API", description = "게시글 관련 기능")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    public ResponseEntity<PostDTO> createPost(@Parameter(description = "게시글 데이터") @RequestBody PostDTO postDTO) {
        PostDTO createdPost = postService.createPost(postDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 조회", description = "특정 ID의 게시글을 조회합니다.")
    public ResponseEntity<PostDTO> getPostById(@Parameter(description = "게시글 ID") @PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "특정 ID의 게시글을 수정합니다.")
    public ResponseEntity<PostDTO> updatePost(@Parameter(description = "게시글 ID") @PathVariable("id") Long id,
                                              @Parameter(description = "수정할 게시글 데이터") @RequestBody PostDTO postDTO) {
        PostDTO updatedPost = postService.updatePost(id, postDTO);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "특정 ID의 게시글을 삭제합니다.")
    public ResponseEntity<Void> deletePost(@Parameter(description = "게시글 ID") @PathVariable("id") Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "특정 사용자의 게시글 조회", description = "특정 사용자가 작성한 모든 게시글을 조회합니다.")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        List<PostDTO> posts = postService.getPostByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "모든 게시글을 조회합니다.")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like/{userId}")
    @Operation(summary = "게시글 좋아요", description = "특정 게시글에 좋아요를 추가합니다.")
    public ResponseEntity<Void> likePost(@Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
                                         @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        postService.likePost(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{postId}/like/{userId}")
    @Operation(summary = "게시글 좋아요 취소", description = "특정 게시글의 좋아요를 취소합니다.")
    public ResponseEntity<Void> unlikePost(@Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
                                           @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        postService.unlikePost(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{postId}/comments")
    @Operation(summary = "댓글 추가", description = "특정 게시글에 댓글을 추가합니다.")
    public ResponseEntity<CommentDTO> addComment(@Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
                                                 @Parameter(description = "사용자 ID") @RequestParam("userId") Long userId,
                                                 @Parameter(description = "댓글 내용") @RequestParam("content") String content) {
        CommentDTO comment = postService.addComment(postId, userId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "게시글에 대한 댓글 조회", description = "특정 게시글에 대한 댓글을 조회합니다.")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@Parameter(description = "게시글 ID") @PathVariable Long postId) {
        List<CommentDTO> comments = postService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/generate-DummyPosts")
    public ResponseEntity<String> createDummyPosts(@RequestParam int count) {
        postService.createDummyPost(count);
        return ResponseEntity.ok("Dummy posts created: " + count);
    }

    @PostMapping("/generate-DummyPostsByEM")
    public ResponseEntity<String> createDummyPostByEM(@RequestParam int count) {
        postService.createDummyPostByEM(count);
        return ResponseEntity.ok("Dummy posts created: " + count);
    }

}
