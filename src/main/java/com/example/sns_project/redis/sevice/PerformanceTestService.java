package com.example.sns_project.redis.sevice;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PerformanceTestService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int DATA_SIZE = 100000; // 테스트 데이터 개수
    private final Map<String, String> localMap = new HashMap<>();

    /**
     * 데이터 초기화: Redis와 HashMap에 동일한 데이터 저장
     */
    @PostConstruct
    public void initializeData() {
        for (int i = 0; i < DATA_SIZE; i++) {
            String key = "key" + i;
            String value = "value" + i;

            // Redis에 데이터 저장
            redisTemplate.opsForValue().set(key, value);

            // HashMap에 데이터 저장
            localMap.put(key, value);
        }
    }

    // Redis 초기화
    public void clearRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushAll(); // 모든 Redis 데이터 삭제
    }

    // HashMap 초기화
    public void clearHashMap() {
        localMap.clear(); // 모든 HashMap 데이터 삭제
    }


    /**
     * Redis에서 데이터 조회
     */
    public long measureRedisPerformance() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < DATA_SIZE; i++) {
            redisTemplate.opsForValue().get("key" + i);
        }

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    /**
     * HashMap에서 데이터 조회
     */
    public long measureHashMapPerformance() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < DATA_SIZE; i++) {
            localMap.get("key" + i);
        }

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}

