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
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendService friendService;
    private final RoleRepository roleRepository;
    private final AuthService authService;

    private final BCryptPasswordEncoder passwordEncoder;

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
            EntityManager entityManager,
            FriendRequestRepository friendRequestRepository,
            FriendshipRepository friendshipRepository,
            FriendService friendService,
            RoleRepository roleRepository,
            AuthService authService
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

        this.passwordEncoder = new BCryptPasswordEncoder();
        // 한국어 로케일로 Faker 초기화
        this.faker = new Faker(Locale.KOREAN);
    }


    @Transactional
    public Map<String, Object> generateAndRegisterUsers(int count) {
        log.info("사용자 더미 데이터 생성 시작. 목표 생성 수: {}", count);
        long startTime = System.currentTimeMillis();

        int batchSize = 500;
        int processedCount = 0;
        List<String> registeredUsernames = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();

        try {
            // 기본 사용자 역할 미리 조회
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("기본 사용자 역할(ROLE_USER)이 존재하지 않습니다."));

            for (int i = 0; i < count; i++) {
                // 고유한 사용자명 생성 (중복 방지)
                String username = generateUniqueUsername();

                // 사용자 등록 DTO 생성
                UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
                userRegistrationDTO.setUsername(username);
                userRegistrationDTO.setEmail(generateUniqueEmail(username));
                userRegistrationDTO.setPassword("123"); // 테스트용 기본 비밀번호


                try {
                    UserDTO registeredUser = authService.register(userRegistrationDTO);
                    registeredUsernames.add(registeredUser.getUsername());
                    processedCount++;

                    // 배치 처리를 위한 주기적인 플러시
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

            // 최종 결과 수집
            long executionTime = System.currentTimeMillis() - startTime;
            result.put("status", "success");
            result.put("generatedUsers", processedCount);
            result.put("usernames", registeredUsernames);
            result.put("executionTimeMs", executionTime);

            log.info("사용자 생성 완료. 총 생성 수: {}, 소요 시간: {}ms", processedCount, executionTime);

        } catch (Exception e) {
            log.error("사용자 생성 중 오류 발생: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("error", e.getMessage());
            result.put("completedCount", processedCount);
        }

        return result;
    }

    /**
     * 중복되지 않는 고유한 사용자명 생성
     */
    private String generateUniqueUsername() {
        String username;
        int attempts = 0;
        do {
            username = faker.name().username();
            if (attempts++ > 10) {
                // 충돌 방지를 위해 랜덤 숫자 추가
                username = username + ThreadLocalRandom.current().nextInt(1000);
            }
        } while (userRepository.existsByUsername(username));
        return username;
    }

    /**
     * 사용자명 기반의 고유한 이메일 주소 생성
     */
    private String generateUniqueEmail(String username) {
        return username.toLowerCase() + "@" +
                faker.internet().domainName().toLowerCase();
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


    /**
     * 친구 관계 더미 데이터 생성
     * 친구 관계와 친구 요청의 방향성을 고려하여 생성
     */
    @Transactional
    public void generateFriendships(double friendshipRatio) {
        List<User> allUsers = userRepository.findAll();
        int batchSize = 5000;
        int processedCount = 0;

        // 친구 관계 추적을 위한 Set (양방향)
        Set<String> existingFriendships = new HashSet<>();

        // 친구 요청 추적을 위한 Map (단방향)
        // Key: "sender_receiver", Value: FriendRequest
        Map<String, FriendRequest> existingRequests = friendRequestRepository.findAll().stream()
                .collect(Collectors.toMap(
                        request -> generateRequestKey(
                                request.getSender().getId(),
                                request.getReceiver().getId()
                        ),
                        request -> request,
                        (existing, replacement) -> existing  // 충돌 시 기존 요청 유지
                ));

        for (User user : allUsers) {
            int numberOfFriends = (int) ((allUsers.size() - 1) * friendshipRatio);

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

                    // 양방향 요청 키 생성
                    String forwardRequestKey = generateRequestKey(user.getId(), friend.getId());
                    String reverseRequestKey = generateRequestKey(friend.getId(), user.getId());

                    // 기존 요청 확인 및 처리
                    FriendRequest existingForwardRequest = existingRequests.get(forwardRequestKey);
                    FriendRequest existingReverseRequest = existingRequests.get(reverseRequestKey);

                    // 이미 존재하는 요청 처리
                    if (existingForwardRequest != null) {
                        existingForwardRequest.setStatus(RequestStatus.ACCEPTED);
                        entityManager.merge(existingForwardRequest);
                    } else if (existingReverseRequest != null) {
                        existingReverseRequest.setStatus(RequestStatus.ACCEPTED);
                        entityManager.merge(existingReverseRequest);
                    } else {
                        // 새로운 요청 생성
                        FriendRequest newRequest = new FriendRequest();
                        newRequest.setSender(user);
                        newRequest.setReceiver(friend);
                        newRequest.setStatus(RequestStatus.ACCEPTED);
                        entityManager.persist(newRequest);
                        existingRequests.put(forwardRequestKey, newRequest);
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
     * 각 사용자마다 지정된 수만큼의 친구 요청을 랜덤하게 생성하되,
     * 기존 친구 관계와 이미 존재하는 요청은 제외
     *
     * @param requestsPerUser 사용자당 생성할 친구 요청 수
     */
    @Transactional
    public void generateFriendRequests(int requestsPerUser) {
        List<User> allUsers = userRepository.findAll();
        int batchSize = 5000;
        int processedCount = 0;

        // 이미 친구인 관계를 조회하여 중복 방지
        Set<String> existingFriendships = friendshipRepository.findAll().stream()
                .map(f -> generateFriendshipKey(f.getUser1().getId(), f.getUser2().getId()))
                .collect(Collectors.toSet());

        // 이미 존재하는 친구 요청을 조회 (PENDING 상태)
        Set<String> existingRequests = friendRequestRepository.findAll().stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .map(request -> generateFriendshipKey(
                        request.getSender().getId(),
                        request.getReceiver().getId()
                ))
                .collect(Collectors.toSet());

        for (User sender : allUsers) {
            // 잠재적 수신자 목록 생성 (현재 사용자 제외)
            List<User> potentialReceivers = allUsers.stream()
                    .filter(u -> !u.equals(sender))
                    .filter(receiver -> {
                        String key = generateFriendshipKey(sender.getId(), receiver.getId());
                        // 이미 친구가 아니고, 대기 중인 요청도 없는 경우만 포함
                        return !existingFriendships.contains(key) && !existingRequests.contains(key);
                    })
                    .collect(Collectors.toList());

            // 수신자 목록을 섞어서 랜덤성 확보
            Collections.shuffle(potentialReceivers);

            // 설정된 수만큼 요청 생성
            for (int i = 0; i < requestsPerUser && i < potentialReceivers.size(); i++) {
                User receiver = potentialReceivers.get(i);

                FriendRequest request = new FriendRequest();
                request.setSender(sender);
                request.setReceiver(receiver);
                request.setStatus(RequestStatus.PENDING);

                entityManager.persist(request);
                processedCount++;

                // 생성된 요청 키를 기존 요청 세트에 추가하여 중복 방지
                existingRequests.add(generateFriendshipKey(sender.getId(), receiver.getId()));

                // 배치 처리를 위한 주기적인 플러시
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


    /**
     * 친구 관계의 키 생성 (양방향)
     * 항상 작은 ID가 앞에 오도록 하여 양방향 관계를 하나의 키로 표현
     */
    private String generateFriendshipKey(Long userId1, Long userId2) {
        return userId1 < userId2
                ? userId1 + "_" + userId2
                : userId2 + "_" + userId1;
    }

    /**
     * 친구 요청의 키 생성 (단방향)
     * 요청의 방향성을 보존하기 위해 sender와 receiver의 순서를 유지
     */
    private String generateRequestKey(Long senderId, Long receiverId) {
        return senderId + "_" + receiverId;
    }

    /**
     * 랜덤한 친구 요청 상태를 반환하는 메소드
     */
    private RequestStatus getRandomRequestStatus() {
        double random = ThreadLocalRandom.current().nextDouble();
        if (random < 0.6) return RequestStatus.ACCEPTED;
        if (random < 0.8) return RequestStatus.PENDING;
        return RequestStatus.REJECTED;
    }

}