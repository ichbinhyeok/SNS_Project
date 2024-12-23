package com.example.sns_project.redis;

import com.example.sns_project.redis.sevice.RedisService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisInsertTest {

    @Autowired
    private RedisService redisService;

    private static final String TEST_KEY_PREFIX = "testKey";

    @AfterEach
    void tearDown() {
        // Redis에서 테스트 데이터 삭제
        for (int i = 1; i <= 5; i++) {
//            redisService.deleteValues(TEST_KEY_PREFIX + i);
        }
    }

    @Test
    @DisplayName("RedisService를 통해 데이터를 저장하고 조회한다.")
    void redisServiceSaveAndRetrieveAllTest() {

        // 테스트 결과 레디스는 인서트 성능에는 영향을 주지 않음.. 조회용인가 보다.

        long start = System.currentTimeMillis();

        // 1. Redis에 여러 데이터 저장
        for (int i = 1; i <= 5; i++) {
            String key = TEST_KEY_PREFIX + i;
            String value = "value" + i;
            redisService.setValues(key, value);
        }

        long end = System.currentTimeMillis();
        System.out.println("Redis Insert Time: " + (end - start) + "ms");

        // 2. 저장된 데이터를 조회 및 검증
        for (int i = 1; i <= 5; i++) {
            String key = TEST_KEY_PREFIX + i;
            String expectedValue = "value" + i;

            // RedisService를 통해 값 조회
            String actualValue = redisService.getValues(key);

            // 값 검증
            assertThat(actualValue).isNotNull();
            assertThat(actualValue).isEqualTo(expectedValue);
        }


        redisService.printAllValues();


    }

    @Test
    @DisplayName("RedisService를 통해 Hash 데이터 저장 및 조회")
    void redisServiceHashOpsTest() {
        // 1. Hash 데이터 생성
        String hashKey = TEST_KEY_PREFIX + "Hash";
        Map<String, String> hashData = new HashMap<>();
        hashData.put("field1", "value1");
        hashData.put("field2", "value2");
        hashData.put("field3", "value3");

        // 2. RedisService로 Hash 데이터 저장
        redisService.setHashOps(hashKey, hashData);

        // 3. 저장된 Hash 데이터 조회 및 검증
        for (Map.Entry<String, String> entry : hashData.entrySet()) {
            String field = entry.getKey();
            String expectedValue = entry.getValue();

            // RedisService를 통해 Hash 데이터 조회
            String actualValue = redisService.getHashOps(hashKey, field);

            // 값 검증
            assertThat(actualValue).isNotNull();
            assertThat(actualValue).isEqualTo(expectedValue);
        }
    }
}
