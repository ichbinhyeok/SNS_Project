package com.example.sns_project.batch.service;

import com.example.sns_project.batch.entity.TestEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestEntityBatchService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 10000;  // Batch 크기 설정

    @Transactional
    public void batchInsert(List<TestEntity> entities) {
        int count = 0;

        for (TestEntity entity : entities) {
            entityManager.persist(entity);  // 엔티티를 영속성 컨텍스트에 추가
            count++;

            if (count % BATCH_SIZE == 0) {
                entityManager.flush();  // Batch size만큼 쌓이면 DB에 반영
                entityManager.clear();  // 메모리 절약을 위해 영속성 컨텍스트 초기화
            }
        }

        // 마지막 배치가 BATCH_SIZE에 맞지 않으면, flush()를 통해 나머지 처리
        entityManager.flush();
        entityManager.clear();
    }
}

