package com.example.sns_project.batch.service;

import com.example.sns_project.batch.entity.TestEntity;
import com.example.sns_project.batch.repository.TestEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TestEntityBatchServiceTest {

    @Autowired
    private TestEntityBatchService testEntityBatchService;

    @Autowired
    private TestEntityRepository testEntityRepository;

    @Test
    @Transactional
    @Rollback(false) //트랜잭션 처리하면 테스트는 롤백시켜버려서 롤백 닫아둬야함
    @DisplayName("배치 인서트 (백만개)")
    public void testBatchInsert() {
        // Given: 10,000개의 대량 데이터 생성
        List<TestEntity> testEntities = new ArrayList<>();
        for (int i = 1; i <= 1000000; i++) {
            TestEntity entity = new TestEntity();
            entity.setName("Name" + i);
            entity.setAge(String.valueOf(20 + (i % 50)));
            testEntities.add(entity);
        }

        // When: 배치 인서트 실행
        testEntityBatchService.batchInsert(testEntities);

        // Then: 데이터베이스에 10,000개의 데이터가 잘 저장되었는지 확인
        long count = testEntityRepository.count();
//        assertThat(count).isEqualTo(50000);
    }
}
