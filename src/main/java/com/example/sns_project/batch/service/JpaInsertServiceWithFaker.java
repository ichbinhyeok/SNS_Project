package com.example.sns_project.batch.service;

import com.example.sns_project.batch.entity.TestEntity;
import com.example.sns_project.batch.repository.TestEntityRepository;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class JpaInsertServiceWithFaker {

    private final TestEntityRepository repository;

    private final Faker faker = new Faker();  // Faker 인스턴스 생성

    public JpaInsertServiceWithFaker(TestEntityRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void insertUsingJpa(int count) {
        for (int i = 0; i < count; i++) {
            TestEntity entity = new TestEntity();
            entity.setName(faker.name().fullName());  // 랜덤 이름
            entity.setAge(String.valueOf(faker.number().numberBetween(18, 60)));
            repository.save(entity);
        }
    }
}
