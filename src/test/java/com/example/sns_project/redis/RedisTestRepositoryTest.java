package com.example.sns_project.redis;

import com.example.sns_project.redis.model.RedisTest;
import com.example.sns_project.redis.repository.RedisTestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisTestRepositoryTest {

    @Autowired
    private RedisTestRepository redisTestRepository;

    @Test
    public void testSaveMultipleDataWithAutoIncrementId() {
        // Given: Multiple dummy data entries
        int numberOfEntries = 100; // 원하는 데이터 개수 설정
        for (int i = 1 ; i <= numberOfEntries; i++) {
            RedisTest redisTest = new RedisTest();
            redisTest.setId(String.valueOf(i)); // 자동 증가 ID 설정
            redisTest.setName("Test User " + (i + 1));
            redisTest.setAge(20 + (i % 10)); // 나이를 20~29로 설정

            // Save each entry
            redisTestRepository.save(redisTest);
        }

        // When: Retrieving one of the entries to verify
        RedisTest retrievedData = redisTestRepository.findById("50").orElse(null);

        // Then: Verifying data is present
        assertThat(retrievedData).isNotNull();
        assertThat(retrievedData.getName()).isEqualTo("Test User 50");
        assertThat(retrievedData.getAge()).isEqualTo(20 + (49 % 10)); // 인덱스 49 (0-based)
    }
}
