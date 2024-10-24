package com.example.sns_project.service;

// 게시글 관련 비즈니스 로직을 처리하는 서비스

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.dto.UserDTO; // UserDTO 추가
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.model.*;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;  // PostRepository 의존성 주입
    private final UserService userService;        // UserService 의존성 주입
    private final UserRepository userRepository;  // UserRepository 의존성 주입
    private final CommentRepository commentRepository; // CommentRepository 의존성 주입
    private final CommentService commentService;

    // 게시글 생성
    @Transactional
    public PostDTO createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        // User 객체를 가져와서 설정
        User user = userService.findById(postDTO.getAuthor().getId()); // UserDTO에서 ID 가져오기
        post.setUser(user);  // User 객체 설정
        postRepository.save(post);  // 데이터베이스에 저장

        // 작성한 게시글 반환
        return new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                new UserDTO(user.getId(), user.getUsername(), user.getEmail()),
                null);
    }

    // 게시글 수정
    @Transactional
    public PostDTO updatePost(Long postId, PostDTO postDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        postRepository.save(post);  // 변경된 게시글 저장

        // 수정된 게시글 반환
        return new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                new UserDTO(post.getUser().getId(), post.getUser().getUsername(), post.getUser().getEmail()),
                null);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        postRepository.delete(post);  // 게시글 삭제
    }

    // 게시글 조회
    @Transactional
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                new UserDTO(post.getUser().getId(), post.getUser().getUsername(), post.getUser().getEmail()),
                null);
    }

    // 특정 사용자의 게시글 조회
    public List<PostDTO> getPostByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(post -> new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                        new UserDTO(post.getUser().getId(), post.getUser().getUsername(), post.getUser().getEmail()),
                        null))
                .collect(Collectors.toUnmodifiableList());
    }

    // 게시글에 대한 댓글 조회 (CommentService를 통해 호출)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentService.getCommentsByPostId(postId); // CommentService를 통해 호출
    }

    // 게시글 좋아요
    @Transactional
    public void likePost(Long postId, long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userService.findById(userId);

        // 좋아요 추가
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        post.getLikes().add(postLike); // 게시글의 좋아요 목록에 추가
        user.getLikedPosts().add(postLike); // 사용자의 좋아요 목록에 추가
        postRepository.save(post); // 변경사항 저장
    }

    // 게시글 좋아요 취소
    @Transactional
    public void unlikePost(Long postId, long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userService.findById(userId);

        // 좋아요를 찾고 제거
        PostLike postLike = post.getLikes().stream()
                .filter(like -> like.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User has not liked this post"));

        post.getLikes().remove(postLike); // 게시글의 좋아요 목록에서 제거
        user.getLikedPosts().remove(postLike); // 사용자의 좋아요 목록에서 제거
        postRepository.save(post); // 변경사항 저장
    }

    // 댓글 추가
    @Transactional
    public CommentDTO addComment(Long postId, long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userService.findById(userId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user); // User 객체 설정

        post.getComments().add(comment); // 게시글의 댓글 목록에 추가
        user.getComments().add(comment); // 사용자의 댓글 목록에 추가
        postRepository.save(post); // 게시글에 변경사항 저장

        // CommentDTO로 변환하여 반환
        return new CommentDTO(comment.getId(), postId, comment.getContent(), user.getId());
    }

    // 댓글 좋아요
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        User user = userService.findById(userId);

        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        comment.getLikes().add(commentLike); // 댓글의 좋아요 목록에 추가
        user.getLikedComments().add(commentLike); // 사용자의 댓글 좋아요 목록에 추가
        commentRepository.save(comment); // 변경사항 저장
    }

    // 댓글 좋아요 취소
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        User user = userService.findById(userId);

        // 좋아요를 찾고 제거
        CommentLike commentLike = comment.getLikes().stream()
                .filter(like -> like.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User has not liked this comment"));

        comment.getLikes().remove(commentLike); // 댓글의 좋아요 목록에서 제거
        user.getLikedComments().remove(commentLike); // 사용자의 댓글 좋아요 목록에서 제거
        commentRepository.save(comment); // 변경사항 저장
    }

    // 추가적인 기능 제안
    // 예를 들어, 게시글 목록 조회 기능을 추가할 수 있습니다.
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> new PostDTO(post.getId(), post.getTitle(), post.getContent(),
                        new UserDTO(post.getUser().getId(), post.getUser().getUsername(), post.getUser().getEmail()),
                        null))
                .collect(Collectors.toUnmodifiableList());
    }
}
