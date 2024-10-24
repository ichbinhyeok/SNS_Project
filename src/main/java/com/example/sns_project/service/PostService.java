package com.example.sns_project.service;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.enums.NotificationType;
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

    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final NotificationService notificationService;

    @Transactional
    public PostDTO createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        User user = userService.findById(postDTO.getAuthor().getId());
        post.setUser(user);
        postRepository.save(post);

        return convertToDTO(post, user);
    }

    @Transactional
    public PostDTO updatePost(Long postId, PostDTO postDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        postRepository.save(post);

        return convertToDTO(post, post.getUser());
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        postRepository.delete(post);
    }

    @Transactional
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return convertToDTO(post, post.getUser());
    }

    public List<PostDTO> getPostByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(post -> convertToDTO(post, post.getUser()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userService.findById(userId);

        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        post.getLikes().add(postLike);
        user.getLikedPosts().add(postLike);

        // 알림 생성
        String message = user.getUsername() + "님이 당신의 포스트에 좋아요를 눌렀습니다.";
        notificationService.sendNotification(post.getUser().getId(), message, NotificationType.LIKE);

        postRepository.save(post);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userService.findById(userId);

        PostLike postLike = post.getLikes().stream()
                .filter(like -> like.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User has not liked this post"));

        post.getLikes().remove(postLike);
        user.getLikedPosts().remove(postLike);
        postRepository.save(post);
    }

    @Transactional
    public CommentDTO addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userService.findById(userId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        post.getComments().add(comment); // 게시글의 댓글 목록에 추가
        user.getComments().add(comment); // 사용자의 댓글 목록에 추가
        postRepository.save(post); // 게시글에 변경사항 저장

        // 알림 생성
        String message = user.getUsername() + "님이 당신의 포스트에 댓글을 달았습니다.";
        notificationService.sendNotification(post.getUser().getId(), message, NotificationType.COMMENT);


        // CommentDTO로 변환하여 반환
        return new CommentDTO(comment.getId(), postId, comment.getContent(), user.getId());
    }

    // DTO 변환 헬퍼 메서드
    private PostDTO convertToDTO(Post post, User user) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                new UserDTO(user.getId(), user.getUsername(), user.getEmail()),
                null
        );
    }

    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> convertToDTO(post, post.getUser()))
                .toList();
    }
}


