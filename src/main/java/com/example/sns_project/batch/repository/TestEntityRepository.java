package com.example.sns_project.batch.repository;

import com.example.sns_project.batch.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  TestEntityRepository extends JpaRepository<TestEntity, Long> {
}
