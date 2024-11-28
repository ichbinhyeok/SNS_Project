package com.example.sns_project.service;

import com.example.sns_project.model.*;
import com.example.sns_project.repository.CommentRepository;
import com.example.sns_project.repository.PostRepository;
import com.example.sns_project.repository.UserRepository;
import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 테스트용 더미 데이터를 생성하는 서비스
 * 게시글, 댓글 등의 더미 데이터를 대량으로 생성하여 데이터베이스에 저장
 */
@Slf4j
@Service
public class FakeDataService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final CommentRepository commentRepository;
    private final EntityManager entityManager;
    private final CommentService commentService;

    // Faker 객체를 통해 랜덤한 더미 데이터 생성
    private final Faker faker;

    /**
     * 생성자를 통한 의존성 주입
     *
     * @param userRepository    사용자 저장소
     * @param postRepository    게시글 저장소
     * @param commentRepository 댓글 저장소
     * @param entityManager     JPA 엔티티 매니저
     */
    public FakeDataService(
            UserRepository userRepository,
            PostRepository postRepository,
            PostService postService,
            CommentRepository commentRepository,
            CommentService commentService,
            EntityManager entityManager
    ) {

        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.commentService = commentService;
        this.entityManager = entityManager;
        this.postService = postService;
        // 한국어 로케일로 Faker 초기화
        this.faker = new Faker(Locale.KOREAN);
    }

    /**
     * 더미 게시글 생성 메소드
     *
     * @param numberOfPosts 생성할 게시글 수
     */
    @Transactional
    public void generatePosts(int numberOfPosts) {
        // 배치 처리를 위한 사이즈 설정
        int batchSize = 5000;
        // 모든 사용자 목록을 미리 조회 (N+1 문제 방지)
        List<User> users = userRepository.findAll();

        List<Post> postsToSave = new ArrayList<>();

        for (int i = 0; i < numberOfPosts; i++) {
            // 랜덤한 사용자 선택
            User user = users.get(
                    ThreadLocalRandom.current().nextInt(users.size())
            );

            // 새 게시글 생성 및 설정
            Post post = new Post();
            post.setTitle(faker.lorem().sentence());
            post.setContent(faker.lorem().paragraph(3));
            post.setUser(user);

            postsToSave.add(post);

            // 배치 사이즈에 도달하면 일괄 저장
            if (postsToSave.size() >= batchSize) {
                postRepository.saveAll(postsToSave);
                postsToSave.clear();
            }
        }

        // 남은 게시글들 처리
        if (!postsToSave.isEmpty()) {
            postRepository.saveAll(postsToSave);
        }
    }

    /**
     * 더미 댓글 생성 메소드
     *
     * @param numberOfComments 생성할 최상위 댓글 수
     * @param maxDepth         대댓글의 최대 깊이 (1: 대댓글 없음, 2: 대댓글 1단계, 3: 대댓글 2단계...)
     */
    @Transactional
    public void generateComments(int numberOfComments, int maxDepth) {
        // 모든 게시글과 사용자 목록을 미리 조회 (N+1 문제 방지)
        List<Post> posts = postRepository.findAll();
        List<User> users = userRepository.findAll();

        List<Comment> commentsToSave = new ArrayList<>();
        int batchSize = 5000;

        for (int i = 0; i < numberOfComments; i++) {
            // 랜덤한 게시글과 사용자 선택
            Post post = posts.get(faker.number().numberBetween(0, posts.size()));
            User user = users.get(faker.number().numberBetween(0, users.size()));

            // 원 댓글 작성자를 제외한 사용자 목록 생성 (대댓글용)
            List<User> possibleReplyUsers = users.stream()
                    .filter(u -> !u.equals(user))
                    .collect(Collectors.toList());

            // 재귀적으로 댓글 트리 생성
            Comment rootComment = createComment(
                    post,
                    user,
                    null,
                    possibleReplyUsers,
                    maxDepth,
                    0
            );

            if (rootComment != null) {
                commentsToSave.add(rootComment);
            }

            // 배치 사이즈에 도달하면 일괄 저장
            if (commentsToSave.size() >= batchSize) {
                commentRepository.saveAll(commentsToSave);
                commentsToSave.clear();
            }
        }

        // 남은 댓글들 처리
        if (!commentsToSave.isEmpty()) {
            commentRepository.saveAll(commentsToSave);
        }
    }

    /**
     * 재귀적으로 댓글과 대댓글을 생성하는 private 메소드
     *
     * @param post          댓글이 달릴 게시글
     * @param originalUser  원 댓글 작성자
     * @param parentComment 부모 댓글 (최상위 댓글의 경우 null)
     * @param possibleUsers 대댓글 작성 가능한 사용자 목록
     * @param maxDepth      최대 댓글 깊이
     * @param currentDepth  현재 댓글 깊이
     * @return 생성된 댓글 객체
     */
    private Comment createComment(
            Post post,
            User originalUser,
            Comment parentComment,
            List<User> possibleUsers,
            int maxDepth,
            int currentDepth
    ) {
        // 최대 깊이 도달 시 중단
        if (currentDepth > maxDepth) {
            return null;
        }

        // 댓글 작성자 설정 (대댓글인 경우 랜덤한 다른 사용자 선택)
        User user = originalUser;
        if (parentComment != null) {
            user = possibleUsers.get(
                    ThreadLocalRandom.current().nextInt(possibleUsers.size())
            );
        }

        // 새 댓글 생성 및 설정
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(faker.lorem().sentence());
        comment.setParentComment(parentComment);

        // 랜덤하게 0~5개의 대댓글 생성
        int numberOfReplies = ThreadLocalRandom.current().nextInt(6);
        for (int i = 0; i < numberOfReplies; i++) {
            Comment childComment = createComment(
                    post,
                    originalUser,
                    comment,
                    possibleUsers,
                    maxDepth,
                    currentDepth + 1
            );

            if (childComment != null) {
                comment.getChildrenComments().add(childComment);
            }
        }

        return comment;
    }

    /**
     * 게시글과 댓글에 대한 좋아요를 랜덤하게 생성
     *
     * @param likeRatio 전체 사용자 중 좋아요를 누를 비율 (0.0 ~ 1.0)
     */
    @Transactional
    public void generatePostLikes(double likeRatio) {
        int pageSize = 10000;
        int pageNumber = 0;
        int processedCount = 0;

        List<User> allUsers = userRepository.findAll();

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            // 1단계: Post ID 페이징
            List<Long> postIds = postRepository.findPostIdsByPage(pageable);
            if (postIds.isEmpty()) break;

            // 2단계: 페치 조인으로 Post와 likes 로드
            List<Post> allPosts = postRepository.findAllWithLikesByIds(postIds);

            for (Post post : allPosts) {
                Set<Long> existingLikes = post.getLikes().stream()
                        .map(like -> like.getUser().getId())
                        .collect(Collectors.toSet());

                int numberOfLikes = (int) (allUsers.size() * likeRatio);
                List<User> shuffledUsers = new ArrayList<>(allUsers);
                Collections.shuffle(shuffledUsers);

                for (int i = 0; i < numberOfLikes && i < shuffledUsers.size(); i++) {
                    User user = shuffledUsers.get(i);
                    if (!existingLikes.contains(user.getId())) {
                        PostLike postLike = new PostLike();
                        postLike.setPost(post);
                        postLike.setUser(user);

                        entityManager.persist(postLike);
                        processedCount++;

                        if (processedCount % pageSize == 0) {
                            entityManager.flush();
                            entityManager.clear();
                            log.info("좋아요 생성 중... {}", processedCount);
                        }
                    }
                }
            }

            pageNumber++;
        }

        entityManager.flush();
        entityManager.clear();
        log.info("좋아요 생성 완료. 총 생성 수: {}", processedCount);
    }

    //천오백만건 약 35분
    @Transactional
    public void generateCommentLikes(double likeRatio) {
        int pageSize = 10000;
        int pageNumber = 0;
        int processedCount = 0;

        // 전체 사용자 목록 조회
        List<User> allUsers = userRepository.findAll();

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            // 1단계: Comment ID 페이징
            List<Long> commentIds = commentRepository.findCommentIdsByPage(pageable);
            if (commentIds.isEmpty()) break;

            // 2단계: 페치 조인으로 Comment와 likes 로드
            List<Comment> allComments = commentRepository.findAllWithLikesByIds(commentIds);

            for (Comment comment : allComments) {
                // 이미 좋아요한 사용자 ID 수집
                Set<Long> existingLikes = comment.getLikes().stream()
                        .map(like -> like.getUser().getId())
                        .collect(Collectors.toSet());

                // 생성할 좋아요 수 계산
                int numberOfLikes = (int) (allUsers.size() * likeRatio);
                List<User> shuffledUsers = new ArrayList<>(allUsers);
                Collections.shuffle(shuffledUsers);

                // 좋아요 생성
                for (int i = 0; i < numberOfLikes && i < shuffledUsers.size(); i++) {
                    User user = shuffledUsers.get(i);
                    if (!existingLikes.contains(user.getId())) {
                        CommentLike commentLike = new CommentLike();
                        commentLike.setComment(comment);
                        commentLike.setUser(user);

                        entityManager.persist(commentLike);
                        processedCount++;

                        // 메모리 관리를 위한 주기적인 플러시
                        if (processedCount % pageSize == 0) {
                            entityManager.flush();
                            entityManager.clear();
                            log.info("댓글 좋아요 생성 중... {}", processedCount);
                        }
                    }
                }
            }

            pageNumber++;
        }

        // 마지막 플러시
        entityManager.flush();
        entityManager.clear();
        log.info("댓글 좋아요 생성 완료. 총 생성 수: {}", processedCount);
    }


}