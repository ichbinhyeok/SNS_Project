package com.example.sns_project.batch.service;

import com.example.sns_project.batch.entity.TestEntity;
import com.example.sns_project.batch.repository.TestEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class JpaInsertService {

    private final TestEntityRepository repository;

    public JpaInsertService(TestEntityRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void insertUsingJpa(int count) {
        for (int i = 0; i < count; i++) {
            TestEntity entity = new TestEntity();
            entity.setName("Name " + i);
            entity.setAge(String.valueOf(i+10));
            repository.save(entity);
        }
    }
}
