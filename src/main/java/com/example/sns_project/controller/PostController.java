package com.example.sns_project.controller;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.exception.ForbiddenException;
import com.example.sns_project.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.sns_project.util.SortUtils.getSortOrder;

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
    public ResponseEntity<PostDTO> createPost(
            @Parameter(description = "게시글 데이터") @RequestBody PostDTO postDTO,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        PostDTO createdPost = postService.createPost(postDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 조회", description = "특정 ID의 게시글을 조회합니다.")
    public ResponseEntity<PostDTO> getPostById(
            @Parameter(description = "게시글 ID") @PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "특정 ID의 게시글을 수정합니다.")
    public ResponseEntity<?> updatePost(
            @Parameter(description = "게시글 ID") @PathVariable("id") Long id,
            @Parameter(description = "수정할 게시글 데이터") @RequestBody PostDTO postDTO,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostDTO existingPost = postService.getPostById(id);

        if (!existingPost.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("본인의 게시글만 수정할 수 있습니다.");
        }

        PostDTO updatedPost = postService.updatePost(id, postDTO);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "특정 ID의 게시글을 삭제합니다.")
    public ResponseEntity<?> deletePost(
            @Parameter(description = "게시글 ID") @PathVariable("id") Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostDTO existingPost = postService.getPostById(id);

        if (!existingPost.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("본인의 게시글만 수정할 수 있습니다.");
        }

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "특정 사용자의 게시글 조회", description = "특정 사용자가 작성한 모든 게시글을 조회합니다.")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(
            @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        List<PostDTO> posts = postService.getPostByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/all")
    @Operation(summary = "게시글 목록 조회", description = "모든 게시글을 조회합니다.")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/paged")
    @Operation(summary = "게시글 목록 페이징 조회", description = "페이지 단위로 게시글을 조회합니다.")
    public ResponseEntity<Page<PostDTO>> getPostsByPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), "createdDate");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<PostDTO> posts = postService.getPostsByPagination(pageRequest);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요", description = "특정 게시글에 좋아요를 추가합니다.")
    public ResponseEntity<Void> likePost(
            @Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        postService.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 취소", description = "특정 게시글의 좋아요를 취소합니다.")
    public ResponseEntity<Void> unlikePost(
            @Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        postService.unlikePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "게시글에 대한 댓글 조회", description = "특정 게시글에 대한 댓글을 조회합니다.")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        List<CommentDTO> comments = postService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * 전체 인기 게시물 조회 API
     */
    @GetMapping("/popular")
    @Operation(summary = "인기 게시글 조회", description = "좋아요 수와 댓글 수를 기반으로 인기 게시글을 조회합니다.")
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "페이지 크기", schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(name = "sort", description = "정렬 기준 (예: id,desc / createdDate,asc)",
                    schema = @Schema(type = "string", defaultValue = "id,desc"))
    })
    public ResponseEntity<Page<PostDTO>> getPopularPosts(
            @PageableDefault(page = 0, size = 10)
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPopularPosts(pageable));
    }

    /**
     * 실시간 인기 게시물 조회 API
     */
    @GetMapping("/hot")
    @Operation(summary = "실시간 인기 게시글 조회", description = "최근 24시간 동안의 활동(좋아요, 댓글)을 기준으로 인기 게시글을 조회합니다.")
    public ResponseEntity<List<PostDTO>> getHotPosts(
            @Parameter(description = "조회할 게시글 수 (기본값: 10)")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(postService.getHotPosts(limit));
    }

//    @PostMapping("/{postId}/comments")
//    @Operation(summary = "댓글 추가", description = "특정 게시글에 댓글을 추가합니다.")
//    public ResponseEntity<CommentDTO> addComment(@Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
//                                                 @Parameter(description = "사용자 ID") @RequestParam("userId") Long userId,
//                                                 @Parameter(description = "댓글 내용") @RequestParam("content") String content) {
//        CommentDTO comment = postService.addComment(postId, userId, content);
//        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
//    }






}
