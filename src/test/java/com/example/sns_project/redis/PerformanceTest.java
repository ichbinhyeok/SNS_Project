package com.example.sns_project.redis;

import com.example.sns_project.redis.sevice.PerformanceTestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class PerformanceTest {

    @Autowired
    private PerformanceTestService performanceTestService;

    @AfterEach
    public void tearDown() {
        performanceTestService.clearRedis();
        performanceTestService.clearHashMap();
        System.out.println("Redis와 HashMap이 초기화되었습니다.");
    }

    // 레디스 조회 성능 테스트
    @Test
    public void testRedisPerformance() {
        System.out.println("성능 테스트를 시작합니다...");

        // Redis 조회 성능 측정
        long redisTime = performanceTestService.measureRedisPerformance();
        System.out.println("Redis 조회 시간: " + redisTime + " ms");

        // HashMap 조회 성능 측정
        long hashMapTime = performanceTestService.measureHashMapPerformance();
        System.out.println("HashMap 조회 시간: " + hashMapTime + " ms");
    }


}
