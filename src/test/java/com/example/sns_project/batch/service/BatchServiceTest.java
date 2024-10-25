package com.example.sns_project.batch.service;

import com.example.sns_project.batch.entity.Child;
import com.example.sns_project.batch.entity.Parent;
import com.example.sns_project.batch.repository.ChildRepository;
import com.example.sns_project.batch.repository.ParentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class BatchServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(BatchServiceTest.class);

    @Autowired
    private BatchService batchService;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ChildRepository childRepository;


    @Test
    @Transactional
    @Rollback(false) // 테스트 트랜잭션 롤백 방지
    @DisplayName("Parent 배치 인서트 테스트")
    public void testBatchInsert() {
        // Given: 1,000,000개의 대량 데이터 생성
        List<Parent> parents = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 1000000; i++) {
            Parent parent = new Parent();
            parent.setName("Name" + i);
            parent.setAge(String.valueOf(random.nextInt(100) + 1)); // 1에서 100까지의 랜덤 나이
            parents.add(parent);
        }

        // When: 배치 인서트 실행
        batchService.batchInsertParent(parents);

        // Then: 데이터베이스에 데이터가 잘 저장되었는지 확인하고 로그 출력
        long count = parentRepository.count();
        logger.info("총 저장된 Parent 개수: {}", count);
    }

    @Test
    @Transactional
    @Rollback(false) // 테스트 트랜잭션 롤백 방지
    @DisplayName("Parent와 Child 배치 인서트 테스트")
    public void testBatchInsertWithChildren() {
        List<Parent> parents = new ArrayList<>();
        Random random = new Random();

        // Given: 100,000명의 부모 데이터 생성
        for (int i = 1; i <= 10000; i++) {
            Parent parent = new Parent();
            parent.setName("ParentName" + i);
            parent.setAge(String.valueOf(random.nextInt(100) + 1)); // 1에서 100까지의 랜덤 나이

            List<Child> children = new ArrayList<>();
            int childCount = random.nextInt(10) + 1; // 부모당 1에서 10명 사이의 랜덤 자식 수

            for (int j = 1; j <= childCount; j++) {
                Child child = new Child();
                child.setName("ChildName" + i + "_" + j);
                child.setAge(String.valueOf(random.nextInt(100) + 1)); // 1에서 100까지의 랜덤 나이
                child.setParent(parent); // 자식에 부모 설정
                children.add(child);
            }

            parent.setChildren(children); // 부모에 자식 설정
            parents.add(parent);

        }

        // When: 배치 인서트 실행
        batchService.batchInsertParent(parents);


        // Then: 데이터베이스에 데이터가 잘 저장되었는지 확인하고 로그 출력
        long parentCount = parentRepository.count();
        long childCount = childRepository.count();
        logger.info("총 저장된 Parent 개수: {}", parentCount);
        logger.info("총 저장된 Child 개수: {}", childCount);
    }
}
