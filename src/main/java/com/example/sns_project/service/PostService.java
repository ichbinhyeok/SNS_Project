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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    // 현재 구현된 기능: 게시물에 대한 댓글 조회
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentService.getCommentsByPostId(postId);
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

    // 현재 구현된 기능: 게시물에 댓글 추가
    // 날짜1205필요없는 기능 CommentService에서 구현함
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
        notificationService.sendPostCommentNotification(post.getUser().getId(), user.getUsername());

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


    // 현재 구현된 기능: 모든 게시물 조회
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> convertToDTO(post, post.getUser()))
                .toList();
    }

    // Service 계층
    public Page<PostDTO> getPostsByPagination(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> convertToDTO(post, post.getUser()));
    }


    @Transactional
    public void createDummyPostByEM(int count) {
        Faker faker = new Faker(); // Faker 인스턴스는 루프 밖에서 생성
        int batchSize = 1000; // 배치 크기 설정
        List<User> cachedUsers = userRepository.findAll(); // 모든 사용자 리스트를 한 번에 가져오기
        Random random = new Random(); // 랜덤 객체 생성

        for (int i = 0; i < count; i++) {
//            // 랜덤 사용자 선택
//            User user = userService.randomSelectUser();
            // 캐싱된 유저 리스트에서 랜덤으로 사용자 선택
            User user = cachedUsers.get(random.nextInt(cachedUsers.size()));
            UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail());

            // PostDTO 생성
            PostDTO postDTO = new PostDTO();
            postDTO.setTitle(faker.lorem().sentence());
            postDTO.setContent(faker.lorem().sentence(10)); // 10개의 단어로 구성된 문장 생성
            postDTO.setAuthor(userDTO); // 게시글 작성자 설정

            // createPost 메서드 호출 (Post 엔티티로 변환 후 persist)
            Post post = new Post();
            post.setTitle(postDTO.getTitle());
            post.setContent(postDTO.getContent());
            post.setUser(user); // User 엔티티를 직접 설정

            entityManager.persist(post); // 엔티티 매니저에 게시글 추가

            // 일정 수마다 flush 및 clear
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush(); // 데이터베이스에 반영
                entityManager.clear(); // 영속성 컨텍스트 비우기
            }
        }
        entityManager.flush(); // 남은 데이터 flush
        entityManager.clear(); // 마지막으로 영속성 컨텍스트 비우기
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
