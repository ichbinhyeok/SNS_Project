package com.example.sns_project.service;

import com.example.sns_project.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void batchInsert(List<User> entities) {
        int batchSize = 50;  // 배치 크기 설정

        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));  // 엔티티 삽입

            // 배치 크기만큼 모아서 flush 및 clear 호출
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();  // 데이터베이스에 반영
                entityManager.clear();  // 영속성 컨텍스트 초기화 (메모리 절약)
            }
        }

        // 마지막 남은 엔티티들에 대한 flush/clear 호출
        entityManager.flush();
        entityManager.clear();
    }
}
