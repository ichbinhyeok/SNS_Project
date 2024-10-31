package com.example.sns_project.batch.service;

import com.example.sns_project.batch.entity.Child;
import com.example.sns_project.batch.entity.Parent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchService {

    @PersistenceContext
    private EntityManager entityManager;
//
    private static final int BATCH_SIZE = 10000;  // Batch 크기 설정

    @Transactional
    public void batchInsertParent(List<Parent> parents) {
        int count = 0;

        for (Parent p : parents) {
            entityManager.persist(p);  // 엔티티를 영속성 컨텍스트에 추가
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

    @Transactional
    public void batchInsertChild(List<Child> children) {
        int count = 0;

        for (Child child : children) {
            entityManager.persist(child);  // 엔티티를 영속성 컨텍스트에 추가
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

