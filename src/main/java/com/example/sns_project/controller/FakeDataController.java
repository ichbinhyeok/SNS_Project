package com.example.sns_project.controller;

import com.example.sns_project.service.FakeDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fake-data")
@RequiredArgsConstructor
@Tag(name = "Fake Data API", description = "테스트용 더미 데이터 생성 API")
public class FakeDataController {

    private final FakeDataService fakeDataService;

    @PostMapping("/generate/all")
    @Operation(summary = "전체 더미 데이터 생성", description = "게시글, 댓글, 좋아요 더미 데이터를 한번에 생성합니다.")
    public ResponseEntity<Map<String, Object>> generateAllData(
            @RequestParam(defaultValue = "100") @Parameter(description = "생성할 게시글 수") int postCount,
            @RequestParam(defaultValue = "10") @Parameter(description = "게시글당 평균 댓글 수") int commentsPerPost,
            @RequestParam(defaultValue = "2") @Parameter(description = "댓글 최대 깊이 (1: 대댓글 없음)") int maxDepth,
            @RequestParam(defaultValue = "0.4") @Parameter(description = "좋아요 비율 (0.0 ~ 1.0)") double likeRatio
    ) {
        try {
            long startTime = System.currentTimeMillis();

            // 게시글 생성
            fakeDataService.generatePosts(postCount);

            // 댓글 생성
            fakeDataService.generateComments(postCount * commentsPerPost, maxDepth);

            // 좋아요 생성
            fakeDataService.generateLikes(likeRatio);

            // 결과 반환
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("generatedPosts", postCount);
            statistics.put("generatedComments", postCount * commentsPerPost);
            statistics.put("generatedLikes", likeRatio * postCount);
            statistics.put("executionTimeMs", System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
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

    @PostMapping("/generate/likes")
    @Operation(summary = "좋아요 더미 데이터 생성")
    public ResponseEntity<Map<String, Object>> generateLikes(
            @RequestParam(defaultValue = "0.4") @Parameter(description = "좋아요 비율 (0.0 ~ 1.0)") double likeRatio
    ) {
        try {
            long startTime = System.currentTimeMillis();
            fakeDataService.generateLikes(likeRatio);

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
}
