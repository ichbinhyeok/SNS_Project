package com.example.sns_project.controller;

import com.example.sns_project.service.FakeDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/fake-data")
@RequiredArgsConstructor
@Tag(name = "Fake Data API", description = "테스트용 더미 데이터 생성 API")
public class FakeDataController {

    private final FakeDataService fakeDataService;

    @PostMapping("/generate/all")
    @Operation(summary = "전체 더미 데이터 생성",
            description = "사용자, 친구 관계, 게시글, 댓글, 좋아요 등 모든 더미 데이터를 순차적으로 생성합니다.")
    public ResponseEntity<Map<String, Object>> generateCompleteData(
            @RequestParam(name = "userCount", defaultValue = "100")
            @Parameter(description = "생성할 사용자 수") int userCount,

            @RequestParam(name = "friendshipRatio", defaultValue = "0.1")
            @Parameter(description = "사용자당 친구 관계 비율 (0.0 ~ 1.0)") double friendshipRatio,

            @RequestParam(name = "friendRequestsPerUser", defaultValue = "5")
            @Parameter(description = "사용자당 친구 요청 수") int friendRequestsPerUser,

            @RequestParam(name = "postsPerUser", defaultValue = "5")
            @Parameter(description = "사용자당 게시글 수") int postsPerUser,

            @RequestParam(name = "commentsPerPost", defaultValue = "10")
            @Parameter(description = "게시글당 평균 댓글 수") int commentsPerPost,

            @RequestParam(name = "maxDepth", defaultValue = "2")
            @Parameter(description = "댓글 최대 깊이 (1: 대댓글 없음)") int maxDepth,

            @RequestParam(name = "likeRatio", defaultValue = "0.1")
            @Parameter(description = "좋아요 비율 (0.0 ~ 1.0)") double likeRatio,

            @RequestParam(name = "withNotifications", defaultValue = "false")
            @Parameter(description = "알림 데이터 생성 여부") boolean withNotifications
    ) {
        Map<String, Object> statistics = new HashMap<>();
        long totalStartTime = System.currentTimeMillis();

        try {
            log.info("전체 더미 데이터 생성 시작 ===========================================");
            log.info("설정된 파라미터:");
            log.info("- 사용자 수: {}", userCount);
            log.info("- 친구 관계 비율: {}", friendshipRatio);
            log.info("- 사용자당 친구 요청 수: {}", friendRequestsPerUser);
            log.info("- 사용자당 게시글 수: {}", postsPerUser);
            log.info("- 게시글당 댓글 수: {}", commentsPerPost);
            log.info("- 댓글 최대 깊이: {}", maxDepth);
            log.info("- 좋아요 비율: {}", likeRatio);
            log.info("- 알림 생성 여부: {}", withNotifications);

            // 1. 사용자 생성
            log.info("[1/7] 사용자 생성 시작 - 목표: {} 명", userCount);
            long userStartTime = System.currentTimeMillis();
            Map<String, Object> userResult = fakeDataService.generateAndRegisterUsers(userCount);
            statistics.put("userGeneration", userResult);
            statistics.put("userGenerationTime", System.currentTimeMillis() - userStartTime);
            log.info("사용자 생성 완료");

            // 2. 친구 관계 생성
            log.info("[2/7] 친구 관계 생성 시작 - 비율: {}", friendshipRatio);
            long friendshipStartTime = System.currentTimeMillis();
            fakeDataService.generateFriendships(friendshipRatio, withNotifications);
            statistics.put("friendshipGenerationTime", System.currentTimeMillis() - friendshipStartTime);
            log.info("친구 관계 생성 완료");

            // 3. 친구 요청 생성
            log.info("[3/7] 친구 요청 생성 시작 - 사용자당 요청 수: {}", friendRequestsPerUser);
            long requestStartTime = System.currentTimeMillis();
            fakeDataService.generateFriendRequests(friendRequestsPerUser);
            statistics.put("friendRequestGenerationTime", System.currentTimeMillis() - requestStartTime);
            log.info("친구 요청 생성 완료");

            // 4. 게시글 생성
            int totalPosts = userCount * postsPerUser;
            log.info("[4/7] 게시글 생성 시작 - 총 게시글 수: {}", totalPosts);
            long postStartTime = System.currentTimeMillis();
            fakeDataService.generatePosts(totalPosts);
            statistics.put("postGenerationTime", System.currentTimeMillis() - postStartTime);
            log.info("게시글 생성 완료");

            // 5. 댓글 생성
            int totalComments = totalPosts * commentsPerPost;
            log.info("[5/7] 댓글 생성 시작 - 총 댓글 수: {}", totalComments);
            long commentStartTime = System.currentTimeMillis();
            fakeDataService.generateComments(totalComments, maxDepth, withNotifications);
            statistics.put("commentGenerationTime", System.currentTimeMillis() - commentStartTime);
            log.info("댓글 생성 완료");

            // 6. 게시글 좋아요 생성
            log.info("[6/7] 게시글 좋아요 생성 시작 - 비율: {}", likeRatio);
            long postLikeStartTime = System.currentTimeMillis();
            fakeDataService.generatePostLikes(likeRatio, withNotifications);
            statistics.put("postLikeGenerationTime", System.currentTimeMillis() - postLikeStartTime);
            log.info("게시글 좋아요 생성 완료");

            // 7. 댓글 좋아요 생성
            log.info("[7/7] 댓글 좋아요 생성 시작 - 비율: {}", likeRatio);
            long commentLikeStartTime = System.currentTimeMillis();
            fakeDataService.generateCommentLikes(likeRatio, withNotifications);
            statistics.put("commentLikeGenerationTime", System.currentTimeMillis() - commentLikeStartTime);
            log.info("댓글 좋아요 생성 완료");

            // 최종 통계
            long totalTime = System.currentTimeMillis() - totalStartTime;
            statistics.put("totalExecutionTimeMs", totalTime);
            statistics.put("withNotifications", withNotifications);

            log.info("전체 더미 데이터 생성 완료 =========================================");
            log.info("총 소요 시간: {}ms", totalTime);

            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            log.error("전체 더미 데이터 생성 중 오류 발생 - 진행 시간: {}ms",
                    System.currentTimeMillis() - totalStartTime, e);

            statistics.put("status", "error");
            statistics.put("error", e.getMessage());
            statistics.put("timeUntilError", System.currentTimeMillis() - totalStartTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(statistics);
        }
    }

    @PostMapping("/generate/users")
    @Operation(summary = "사용자 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generateUsers(
            @RequestParam(name = "count", defaultValue = "100")
            @Parameter(description = "생성할 사용자 수") int count
    ) {
        try {
            log.info("사용자 생성 시작. 생성할 사용자 수: {}", count);
            long startTime = System.currentTimeMillis();

            Map<String, Object> result = fakeDataService.generateAndRegisterUsers(count);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            log.info("사용자 생성 완료: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("사용자 생성 실패: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/posts")
    @Operation(summary = "게시글 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generatePosts(
            @RequestParam(name = "count", defaultValue = "100")
            @Parameter(description = "생성할 게시글 수") int count
    ) {
        try {
            log.info("게시글 생성 시작. 생성할 게시글 수: {}", count);
            long startTime = System.currentTimeMillis();

            fakeDataService.generatePosts(count);

            Map<String, Object> result = new HashMap<>();
            result.put("generatedPosts", count);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            log.info("게시글 생성 완료: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("게시글 생성 실패: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/comments")
    @Operation(summary = "댓글 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generateComments(
            @RequestParam(name = "count", defaultValue = "1000")
            @Parameter(description = "생성할 댓글 수") int count,

            @RequestParam(name = "maxDepth", defaultValue = "2")
            @Parameter(description = "댓글 최대 깊이") int maxDepth,

            @RequestParam(name = "withNotifications", defaultValue = "false")
            @Parameter(description = "알림 데이터 생성 여부") boolean withNotifications
    ) {
        try {
            log.info("댓글 생성 시작. 생성할 댓글 수: {}, 최대 깊이: {}, 알림 생성: {}",
                    count, maxDepth, withNotifications);
            long startTime = System.currentTimeMillis();

            fakeDataService.generateComments(count, maxDepth, withNotifications);

            Map<String, Object> result = new HashMap<>();
            result.put("generatedComments", count);
            result.put("maxDepth", maxDepth);
            result.put("withNotifications", withNotifications);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            log.info("댓글 생성 완료: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("댓글 생성 실패: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/postLikes")
    @Operation(summary = "게시글 좋아요 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generatePostLikes(
            @RequestParam(name = "likeRatio", defaultValue = "0.4")
            @Parameter(description = "좋아요 비율 (0.0 ~ 1.0)") double likeRatio,

            @RequestParam(name = "withNotifications", defaultValue = "false")
            @Parameter(description = "알림 데이터 생성 여부") boolean withNotifications
    ) {
        try {
            log.info("게시글 좋아요 생성 시작. 비율: {}, 알림 생성: {}", likeRatio, withNotifications);
            long startTime = System.currentTimeMillis();

            fakeDataService.generatePostLikes(likeRatio, withNotifications);

            Map<String, Object> result = new HashMap<>();
            result.put("likeRatio", likeRatio);
            result.put("withNotifications", withNotifications);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            log.info("게시글 좋아요 생성 완료: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("게시글 좋아요 생성 실패: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/commentLikes")
    @Operation(summary = "댓글 좋아요 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generateCommentLikes(
            @RequestParam(name = "likeRatio", defaultValue = "0.4")
            @Parameter(description = "댓글 좋아요 비율 (0.0 ~ 1.0)") double likeRatio,

            @RequestParam(name = "withNotifications", defaultValue = "false")
            @Parameter(description = "알림 데이터 생성 여부") boolean withNotifications
    ) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("댓글 좋아요 생성 시작. 비율: {}, 알림 생성: {}", likeRatio, withNotifications);

            fakeDataService.generateCommentLikes(likeRatio, withNotifications);

            Map<String, Object> result = new HashMap<>();
            result.put("likeRatio", likeRatio);
            result.put("withNotifications", withNotifications);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            log.info("댓글 좋아요 생성 완료: {}", result);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("댓글 좋아요 생성 실패: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("executionTimeMs", System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/friendships")
    @Operation(summary = "친구 관계 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generateFriendships(
            @RequestParam(name = "friendshipRatio", defaultValue = "0.2")
            @Parameter(description = "사용자당 친구 관계 비율 (0.0 ~ 1.0)") double friendshipRatio,

            @RequestParam(name = "withNotifications", defaultValue = "false")
            @Parameter(description = "알림 데이터 생성 여부") boolean withNotifications
    ) {
        try {
            log.info("친구 관계 생성 시작. 비율: {}, 알림 생성: {}", friendshipRatio, withNotifications);
            long startTime = System.currentTimeMillis();

            fakeDataService.generateFriendships(friendshipRatio, withNotifications);

            Map<String, Object> result = new HashMap<>();
            result.put("friendshipRatio", friendshipRatio);
            result.put("withNotifications", withNotifications);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            log.info("친구 관계 생성 완료: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("친구 관계 생성 실패: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/friendRequests")
    @Operation(summary = "친구 요청 더미 데이터 생성",
            description = "친구 요청은 항상 알림을 생성합니다 (실제 서비스 로직과 동일)")
    public ResponseEntity<Map<String, Object>> generateFriendRequests(
            @RequestParam(name = "requestsPerUser", defaultValue = "5")
            @Parameter(description = "사용자당 친구 요청 수") int requestsPerUser
    ) {
        try {
            log.info("친구 요청 생성 시작. 사용자당 요청 수: {}", requestsPerUser);
            long startTime = System.currentTimeMillis();

            fakeDataService.generateFriendRequests(requestsPerUser);

            Map<String, Object> result = new HashMap<>();
            result.put("requestsPerUser", requestsPerUser);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            log.info("친구 요청 생성 완료: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("친구 요청 생성 실패: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}