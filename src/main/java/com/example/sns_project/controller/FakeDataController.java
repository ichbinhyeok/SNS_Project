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
    @Operation(summary = "전체 더미 데이터 생성", description = "게시글, 댓글, 좋아요 더미 데이터를 한번에 생성합니다.")
    public ResponseEntity<Map<String, Object>> generateAllData(
            @RequestParam(name = "postCount", defaultValue = "100")
            @Parameter(description = "생성할 게시글 수") int postCount,

            @RequestParam(name = "commentsPerPost", defaultValue = "10")
            @Parameter(description = "게시글당 평균 댓글 수") int commentsPerPost,

            @RequestParam(name = "maxDepth", defaultValue = "2")
            @Parameter(description = "댓글 최대 깊이 (1: 대댓글 없음)") int maxDepth,

            @RequestParam(name = "likeRatio", defaultValue = "0.4")
            @Parameter(description = "좋아요 비율 (0.0 ~ 1.0)") double likeRatio
    ) {
        try {
            log.info("더미 데이터 생성 시작: posts={}, commentsPerPost={}, maxDepth={}, likeRatio={}",
                    postCount, commentsPerPost, maxDepth, likeRatio);

            // 게시글 생성
            log.info("게시글 생성 시작");
            fakeDataService.generatePosts(postCount);
            log.info("게시글 {} 개 생성 완료", postCount);

            // 댓글 생성
            int totalComments = postCount * commentsPerPost;
            log.info("댓글 생성 시작");
            fakeDataService.generateComments(totalComments, maxDepth);
            log.info("댓글 {} 개 생성 완료", totalComments);

            // 좋아요 생성
            log.info("게시글 좋아요 생성 시작");
            fakeDataService.generatePostLikes(likeRatio);
            log.info("게시글 좋아요 생성 완료");

            // 댓글 좋아요 생성
            log.info("댓글 좋아요 생성 시작");
            fakeDataService.generateCommentLikes(likeRatio);
            log.info("댓글 좋아요 생성 완료");

            // 결과 반환
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("generatedPosts", postCount);
            statistics.put("generatedComments", totalComments);
            statistics.put("generatedPostLikes", (int) (likeRatio * postCount));
            statistics.put("generatedCommentLikes", (int) (likeRatio * totalComments));

            log.info("더미 데이터 생성 완료: {}", statistics);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("더미 데이터 생성 실패: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/posts")
    @Operation(summary = "게시글 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generatePosts(
            @RequestParam(defaultValue = "100") @Parameter(description = "생성할 게시글 수") int count
    ) {
        try {
            long startTime = System.currentTimeMillis();
            fakeDataService.generatePosts(count);

            Map<String, Object> result = new HashMap<>();
            result.put("generatedPosts", count);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/comments")
    @Operation(summary = "댓글 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generateComments(
            @RequestParam(defaultValue = "1000") @Parameter(description = "생성할 댓글 수") int count,
            @RequestParam(defaultValue = "2") @Parameter(description = "댓글 최대 깊이") int maxDepth
    ) {
        try {
            long startTime = System.currentTimeMillis();
            fakeDataService.generateComments(count, maxDepth);

            Map<String, Object> result = new HashMap<>();
            result.put("generatedComments", count);
            result.put("maxDepth", maxDepth);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/postLikes")
    @Operation(summary = "포스트좋아요 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generateLikes(
            @RequestParam(defaultValue = "0.4") @Parameter(description = "좋아요 비율 (0.0 ~ 1.0)") double likeRatio
    ) {
        try {
            long startTime = System.currentTimeMillis();
            fakeDataService.generatePostLikes(likeRatio);

            Map<String, Object> result = new HashMap<>();
            result.put("likeRatio", likeRatio);
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate/commentLikes")
    @Operation(summary = "댓글 좋아요 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generateCommentLikes(
            @RequestParam(name = "likeRatio", defaultValue = "0.4") @Parameter(description = "댓글 좋아요 비율 (0.0 ~ 1.0)") double likeRatio
    ) {
        try {
            log.info("댓글 좋아요 생성 시작. 비율:{}", likeRatio);
            fakeDataService.generateCommentLikes(likeRatio);
            Map<String, Object> result = new HashMap<>();
            result.put("likeRatio", likeRatio);

            log.info("댓글 좋아요 생성 완료");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("댓글 좋아요 생성 실패:{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
