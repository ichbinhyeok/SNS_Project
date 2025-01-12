package com.example.sns_project.service;

import com.example.sns_project.dto.CommentDTO;
import com.example.sns_project.dto.PostDTO;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.enums.NotificationType;
import com.example.sns_project.exception.AlreadyLikedException;
import com.example.sns_project.exception.ResourceNotFoundException;
import com.example.sns_project.model.*;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostLikeRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private final PostLikeRepository postLikeRepository;

    @Autowired
    private EntityManager entityManager;


    // 현재 구현된 기능: 게시물 생성
    @Transactional
    public PostDTO createPost(PostDTO postDTO, long userId) {  // username 파라미터 추가
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        // username으로 사용자 찾기
        User user = userService.findById(userId);  // findByUsername 메서드 필요
        post.setUser(user);
        postRepository.save(post);

        return convertToDTO(post, user);
    }

    // 현재 구현된 기능: 게시물 수정
    @Transactional
    public PostDTO updatePost(Long postId, PostDTO postDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        postRepository.save(post);

        return convertToDTO(post, post.getUser());
    }

    // 현재 구현된 기능: 게시물 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        postRepository.delete(post);
    }

    // 현재 구현된 기능: 게시물 ID로 조회
    @Transactional
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return convertToDTO(post, post.getUser());
    }

    // 현재 구현된 기능: 특정 사용자 ID로 게시물 조회
    public List<PostDTO> getPostByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(post -> convertToDTO(post, post.getUser()))
                .collect(Collectors.toUnmodifiableList());
    }



    // 현재 구현된 기능: 게시물 좋아요 기능
    @Transactional
    public void likePost(Long postId, Long userId) {
        // 이미 좋아요 했는지 확인
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new AlreadyLikedException("이미 좋아요를 누른 게시물입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userService.findById(userId);

        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);

        postLikeRepository.save(postLike);

        notificationService.sendPostLikeNotification(post.getUser().getId(), user.getUsername());
    }

    // 현재 구현된 기능: 게시물 좋아요 취소
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


    // DTO 변환 헬퍼 메서드
    private PostDTO convertToDTO(Post post, User user) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                new UserDTO(user.getId(), user.getUsername(), user.getEmail()),
                post.getLikes().stream()
                        .map(postLike -> postLike.getUser().getId())                         .collect(Collectors.toSet())
        );
    }


    // 현재 구현된 기능: 모든 게시물 조회
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> convertToDTO(post, post.getUser()))
                .toList();
    }

    // 게시물 페이징
    public Page<PostDTO> getPostsByPagination(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> convertToDTO(post, post.getUser()));
    }

    /**
     * 인기 게시물을 조회하는 메서드
     * 좋아요 수와 댓글 수를 기반으로 인기도를 계산합니다.
     */
    @Transactional
    public Page<PostDTO> getPopularPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findPopularPosts(pageable);
        return posts.map(post -> {
            PostDTO dto = convertToDTO(post, post.getUser());
            dto.setLikedBy(post.getLikes().stream()
                    .map(like -> like.getUser().getId())
                    .collect(Collectors.toSet()));
            return dto;
        });
    }


    /**
     * 실시간 인기 게시물 조회 메서드
     * 최근 24시간 동안의 활동을 기준으로 합니다.
     */
    @Transactional
    public List<PostDTO> getHotPosts(int limit) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        List<Post> hotPosts = postRepository.findHotPosts(oneDayAgo, PageRequest.of(0, limit));

        return hotPosts.stream()
                .map(post -> {
                    PostDTO dto = convertToDTO(post, post.getUser());
                    dto.setLikedBy(post.getLikes().stream()
                            .map(like -> like.getUser().getId())
                            .collect(Collectors.toSet()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 앞으로 구현될 기능: 게시물 검색 기능
    public List<PostDTO> searchPosts(String keyword) {
        // 키워드로 제목 또는 내용 검색
        return null; // 실제 구현 필요
    }

    // 앞으로 구현될 기능: 게시물 필터링 기능
    public List<PostDTO> getFilteredPosts(Long userId, String filter) {
        // 친구의 게시물, 최신순 등으로 필터링
        return null; // 실제 구현 필요
    }

    // 앞으로 구현될 기능: 게시물 수정 기록 기능
    public void addEditHistory(Long postId) {
        // 게시물 수정 기록 추가
        return; // 실제 구현 필요
    }

    // 앞으로 구현될 기능: 게시물 신고 기능
    public void reportPost(Long postId, Long userId, String reason) {
        // 게시물 신고 처리
        return; // 실제 구현 필요
    }

    // 앞으로 구현될 기능: 게시물 통계 기능
//    public PostStatisticsDTO getPostStatistics(Long postId) {
//        // 게시물 통계 반환
//        return null; // 실제 구현 필요
//    }

    // 앞으로 구현될 기능: 게시물 알림 관리 기능
    public void togglePostNotification(Long postId, Long userId) {
        // 게시물 알림 설정 관리
        return; // 실제 구현 필요
    }

    // 앞으로 구현될 기능: 게시물에 대한 액세스 제어 기능
    public void setPostAccessControl(Long postId, List<Long> allowedUserIds) {
        // 특정 사용자만 게시물 접근 허용
        return; // 실제 구현 필요
    }

    // 앞으로 구현될 기능: 게시물 버전 관리 기능
    public void managePostVersion(Long postId) {
        // 게시물 버전 관리
        return; // 실제 구현 필요
    }
}
