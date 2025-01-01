package com.example.sns_project.service;

import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.dto.UserRegistrationDTO;
import com.example.sns_project.enums.RequestStatus;
import com.example.sns_project.model.*;
import com.example.sns_project.repository.*;
import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 테스트용 더미 데이터를 생성하는 서비스
 * 게시글, 댓글, 좋아요 등의 더미 데이터를 대량으로 생성하여 데이터베이스에 저장
 * 알림 기능을 선택적으로 포함하여 실제 서비스와 유사한 환경 테스트 가능
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
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendService friendService;
    private final RoleRepository roleRepository;
    private final AuthService authService;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Faker faker;

    private final Random random = new Random();

    /**
     * 생성자를 통한 의존성 주입
     */
    public FakeDataService(
            UserRepository userRepository,
            PostRepository postRepository,
            PostService postService,
            CommentRepository commentRepository,
            CommentService commentService,
            EntityManager entityManager,
            FriendRequestRepository friendRequestRepository,
            FriendshipRepository friendshipRepository,
            FriendService friendService,
            RoleRepository roleRepository,
            AuthService authService,
            NotificationRepository notificationRepository,
            NotificationService notificationService

    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.commentService = commentService;
        this.entityManager = entityManager;
        this.postService = postService;
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendService = friendService;
        this.roleRepository = roleRepository;
        this.authService = authService;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;

        this.passwordEncoder = new BCryptPasswordEncoder();
        this.faker = new Faker(Locale.KOREAN);
    }

    /**
     * 사용자 더미 데이터 생성
     * 지정된 수만큼의 사용자를 생성하고 등록
     */
    @Transactional
    public Map<String, Object> generateAndRegisterUsers(int count) {
        log.info("사용자 더미 데이터 생성 시작. 목표 생성 수: {}", count);
        long startTime = System.currentTimeMillis();

        int batchSize = 500;
        int processedCount = 0;
        List<String> registeredUsernames = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();

        try {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("기본 사용자 역할(ROLE_USER)이 존재하지 않습니다."));

            for (int i = 0; i < count; i++) {
                String username = generateUniqueUsername();
                UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
                userRegistrationDTO.setUsername(username);
                userRegistrationDTO.setEmail(generateUniqueEmail(username));
                userRegistrationDTO.setPassword("123");

                try {
                    UserDTO registeredUser = authService.register(userRegistrationDTO);
                    registeredUsernames.add(registeredUser.getUsername());
                    processedCount++;

                    if (processedCount % batchSize == 0) {
                        entityManager.flush();
                        entityManager.clear();
                        log.info("사용자 생성 진행 중... (처리된 사용자 수: {})", processedCount);
                    }
                } catch (Exception e) {
                    log.warn("사용자 '{}' 생성 실패: {}", username, e.getMessage());
                    continue;
                }
            }

            long executionTime = System.currentTimeMillis() - startTime;
            result.put("status", "success");
            result.put("generatedUsers", processedCount);
            result.put("usernames", registeredUsernames);
            result.put("executionTimeMs", executionTime);

        } catch (Exception e) {
            log.error("사용자 생성 중 오류 발생: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("error", e.getMessage());
            result.put("completedCount", processedCount);
        }

        return result;
    }

    /**
     * 게시글 더미 데이터 생성
     *
     * @param numberOfPosts 생성할 게시글 수
     */
    @Transactional
    public void generatePosts(int numberOfPosts) {
        int batchSize = 5000;
        List<User> users = userRepository.findAll();
        List<Post> postsToSave = new ArrayList<>();

        for (int i = 0; i < numberOfPosts; i++) {
            User user = users.get(ThreadLocalRandom.current().nextInt(users.size()));

            Post post = new Post();
            post.setTitle(faker.lorem().sentence());
            post.setContent(faker.lorem().paragraph(3));
            post.setUser(user);

            postsToSave.add(post);

            if (postsToSave.size() >= batchSize) {
                postRepository.saveAll(postsToSave);
                postsToSave.clear();
            }
        }

        if (!postsToSave.isEmpty()) {
            postRepository.saveAll(postsToSave);
        }
    }

    /**
     * 댓글 더미 데이터 생성
     *
     * @param numberOfComments  생성할 댓글 수
     * @param maxDepth          대댓글 최대 깊이
     * @param withNotifications 알림 생성 여부
     */
    @Transactional
    public void generateComments(int numberOfComments, int maxDepth, boolean withNotifications) {
        List<Post> posts = postRepository.findAll();
        List<User> users = userRepository.findAll();
        List<Comment> commentsToSave = new ArrayList<>();
        int batchSize = 5000;

        for (int i = 0; i < numberOfComments; i++) {
            Post post = posts.get(faker.number().numberBetween(0, posts.size()));
            User user = users.get(faker.number().numberBetween(0, users.size()));

            // 본인을 제외하지 않고 모든 사용자 포함
            List<User> possibleReplyUsers = new ArrayList<>(users);

            Comment rootComment = createCommentWithNotification(
                    post,
                    user,
                    null,
                    possibleReplyUsers,
                    maxDepth,
                    0,
                    withNotifications
            );

            if (rootComment != null) {
                commentsToSave.add(rootComment);
            }

            if (commentsToSave.size() >= batchSize) {
                commentRepository.saveAll(commentsToSave);
                entityManager.flush();
                entityManager.clear();
                commentsToSave.clear();
                log.info("댓글 생성 중... (처리된 댓글 수: {})", i + 1);
            }
        }

        if (!commentsToSave.isEmpty()) {
            commentRepository.saveAll(commentsToSave);
        }
    }

    /**
     * 댓글과 대댓글을 재귀적으로 생성
     */
    private Comment createCommentWithNotification(
            Post post,
            User originalUser,
            Comment parentComment,
            List<User> possibleUsers,
            int maxDepth,
            int currentDepth,
            boolean withNotifications
    ) {
        if (currentDepth > maxDepth) {
            return null;
        }

        // 본인을 포함한 랜덤 사용자 선택
        User user = (parentComment == null) ? originalUser :
                possibleUsers.get(ThreadLocalRandom.current().nextInt(possibleUsers.size()));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(faker.lorem().sentence());
        comment.setParentComment(parentComment);

        // 알림 생성 로직
        if (withNotifications) {
            if (parentComment == null) {
                notificationService.sendPostCommentNotification(
                        post.getUser().getId(),
                        user.getUsername()
                );
            } else {
                notificationService.sendCommentNotification(
                        parentComment.getUser().getId(),
                        user.getUsername()
                );
            }
        }

        int numberOfReplies = ThreadLocalRandom.current().nextInt(6);
        for (int i = 0; i < numberOfReplies; i++) {
            Comment childComment = createCommentWithNotification(
                    post,
                    originalUser,
                    comment,
                    possibleUsers,
                    maxDepth,
                    currentDepth + 1,
                    withNotifications
            );

            if (childComment != null) {
                comment.getChildrenComments().add(childComment);
            }
        }

        return comment;
    }


    /**
     * 게시글 좋아요 더미 데이터 생성
     *
     * @param likeRatio         좋아요를 누를 사용자 비율 (0.0 ~ 1.0)
     * @param withNotifications 알림 생성 여부
     */
    @Transactional
    public void generatePostLikes(double likeRatio, boolean withNotifications) {
        int pageSize = 1000;
        int pageNumber = 0;
        int processedCount = 0;
        List<User> allUsers = userRepository.findAll();

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<Long> postIds = postRepository.findPostIdsByPage(pageable);
            if (postIds.isEmpty()) break;

            List<Post> allPosts = postRepository.findAllWithLikesByIds(postIds);

            for (Post post : allPosts) {
                Set<Long> existingLikes = post.getLikes().stream()
                        .map(like -> like.getUser().getId())
                        .collect(Collectors.toSet());

                int numberOfLikes = (int) (allUsers.size() * random.nextDouble()* likeRatio);
                List<User> shuffledUsers = new ArrayList<>(allUsers);
                Collections.shuffle(shuffledUsers);

                for (int i = 0; i < numberOfLikes && i < shuffledUsers.size(); i++) {
                    User user = shuffledUsers.get(i);
                    if (!existingLikes.contains(user.getId())) {
                        PostLike postLike = new PostLike();
                        postLike.setPost(post);
                        postLike.setUser(user);
                        entityManager.persist(postLike);

                        if (withNotifications) {
                            notificationService.sendPostLikeNotification(
                                    post.getUser().getId(),
                                    user.getUsername()
                            );
                        }

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

    /**
     * 댓글 좋아요 더미 데이터 생성
     */
    @Transactional
    public void generateCommentLikes(double likeRatio, boolean withNotifications) {
        int pageSize = 100000;
        int pageNumber = 0;
        int processedCount = 0;
        List<User> allUsers = userRepository.findAll();

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<Long> commentIds = commentRepository.findCommentIdsByPage(pageable);
            if (commentIds.isEmpty()) break;

            List<Comment> allComments = commentRepository.findAllWithLikesByIds(commentIds);

            for (Comment comment : allComments) {
                Set<Long> existingLikes = comment.getLikes().stream()
                        .map(like -> like.getUser().getId())
                        .collect(Collectors.toSet());

                int numberOfLikes = (int) (allUsers.size() * random.nextDouble()*likeRatio);
                List<User> shuffledUsers = new ArrayList<>(allUsers);
                Collections.shuffle(shuffledUsers);

                for (int i = 0; i < numberOfLikes && i < shuffledUsers.size(); i++) {
                    User user = shuffledUsers.get(i);
                    if (!existingLikes.contains(user.getId())) {
                        CommentLike commentLike = new CommentLike();
                        commentLike.setComment(comment);
                        commentLike.setUser(user);
                        entityManager.persist(commentLike);

                        if (withNotifications) {
                            notificationService.sendCommentLikeNotification(
                                    comment.getUser().getId(),
                                    user.getUsername()
                            );
                        }

                        processedCount++;
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

        entityManager.flush();
        entityManager.clear();
        log.info("댓글 좋아요 생성 완료. 총 생성 수: {}", processedCount);
    }

    @Transactional
    public void generateFriendships(double friendshipRatio, boolean withNotifications) {
        List<User> allUsers = userRepository.findAll();
        int batchSize = 5000;
        int processedCount = 0;
        Set<String> existingFriendships = new HashSet<>();
        Map<String, FriendRequest> existingRequests = friendRequestRepository.findAll().stream()
                .collect(Collectors.toMap(
                        request -> generateRequestKey(
                                request.getSender().getId(),
                                request.getReceiver().getId()
                        ),
                        request -> request,
                        (existing, replacement) -> existing
                ));

        for (User user : allUsers) {
            int numberOfFriends = (int) ((allUsers.size() - 1) * random.nextDouble()*friendshipRatio);
            List<User> potentialFriends = allUsers.stream()
                    .filter(u -> !u.equals(user))
                    .collect(Collectors.toList());
            Collections.shuffle(potentialFriends);

            for (int i = 0; i < numberOfFriends && i < potentialFriends.size(); i++) {
                User friend = potentialFriends.get(i);
                String friendshipKey = generateFriendshipKey(user.getId(), friend.getId());

                if (!existingFriendships.contains(friendshipKey)) {
                    // 친구 관계 생성
                    Friendship friendship = new Friendship();
                    friendship.setUser1(user);
                    friendship.setUser2(friend);
                    entityManager.persist(friendship);

                    // 친구 요청 처리
                    String forwardRequestKey = generateRequestKey(user.getId(), friend.getId());
                    String reverseRequestKey = generateRequestKey(friend.getId(), user.getId());

                    FriendRequest existingForwardRequest = existingRequests.get(forwardRequestKey);
                    FriendRequest existingReverseRequest = existingRequests.get(reverseRequestKey);

                    if (existingForwardRequest != null) {
                        existingForwardRequest.setStatus(RequestStatus.ACCEPTED);
                        entityManager.merge(existingForwardRequest);
                    } else if (existingReverseRequest != null) {
                        existingReverseRequest.setStatus(RequestStatus.ACCEPTED);
                        entityManager.merge(existingReverseRequest);
                    } else {
                        FriendRequest newRequest = new FriendRequest();
                        newRequest.setSender(user);
                        newRequest.setReceiver(friend);
                        newRequest.setStatus(RequestStatus.ACCEPTED);
                        entityManager.persist(newRequest);
                        existingRequests.put(forwardRequestKey, newRequest);
                    }

                    // 알림 생성
                    if (withNotifications) {
                        notificationService.sendFriendAddedNotification(user.getId(), friend.getId());
                        notificationService.sendFriendAddedNotification(friend.getId(), user.getId());
                    }

                    existingFriendships.add(friendshipKey);
                    processedCount++;

                    if (processedCount % batchSize == 0) {
                        entityManager.flush();
                        entityManager.clear();
                        log.info("친구 관계 생성 중... (처리된 관계 수: {})", processedCount);
                    }
                }
            }
        }

        entityManager.flush();
        entityManager.clear();
        log.info("친구 관계 생성 완료. 총 생성된 관계 수: {}", processedCount);
    }

    /**
     * 친구 요청 더미 데이터 생성
     */
    @Transactional
    public void generateFriendRequests(int requestsPerUser) {
        List<User> allUsers = userRepository.findAll();
        int batchSize = 5000;
        int processedCount = 0;

        Set<String> existingFriendships = friendshipRepository.findAll().stream()
                .map(f -> generateFriendshipKey(f.getUser1().getId(), f.getUser2().getId()))
                .collect(Collectors.toSet());

        Set<String> existingRequests = friendRequestRepository.findAll().stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .map(request -> generateFriendshipKey(
                        request.getSender().getId(),
                        request.getReceiver().getId()
                ))
                .collect(Collectors.toSet());

        for (User sender : allUsers) {
            List<User> potentialReceivers = allUsers.stream()
                    .filter(u -> !u.equals(sender))
                    .filter(receiver -> {
                        String key = generateFriendshipKey(sender.getId(), receiver.getId());
                        return !existingFriendships.contains(key) && !existingRequests.contains(key);
                    })
                    .collect(Collectors.toList());

            Collections.shuffle(potentialReceivers);

            for (int i = 0; i < requestsPerUser && i < potentialReceivers.size(); i++) {
                User receiver = potentialReceivers.get(i);

                FriendRequest request = new FriendRequest();
                request.setSender(sender);
                request.setReceiver(receiver);
                request.setStatus(RequestStatus.PENDING);

                entityManager.persist(request);

                // 친구 요청에 대한 알림은 항상 생성 (실제 서비스 로직과 동일)
                notificationService.sendFriendRequestNotification(
                        sender.getId(),
                        receiver.getId()
                );

                processedCount++;
                existingRequests.add(generateFriendshipKey(sender.getId(), receiver.getId()));

                if (processedCount % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                    log.info("친구 요청 생성 중... (처리된 요청 수: {})", processedCount);
                }
            }
        }

        entityManager.flush();
        entityManager.clear();
        log.info("친구 요청 생성 완료. 총 생성된 요청 수: {}", processedCount);
    }

    // 유틸리티 메소드들
    private String generateUniqueUsername() {
        String username;
        int attempts = 0;
        do {
            username = faker.name().username();
            if (attempts++ > 10) {
                username = username + ThreadLocalRandom.current().nextInt(1000);
            }
        } while (userRepository.existsByUsername(username));
        return username;
    }

    private String generateUniqueEmail(String username) {
        return username.toLowerCase() + "@" + faker.internet().domainName().toLowerCase();
    }

    private String generateFriendshipKey(Long userId1, Long userId2) {
        return userId1 < userId2 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    private String generateRequestKey(Long senderId, Long receiverId) {
        return senderId + "_" + receiverId;
    }
}