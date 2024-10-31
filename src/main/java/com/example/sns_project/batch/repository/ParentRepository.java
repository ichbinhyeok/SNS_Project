package com.example.sns_project.batch.repository;

import com.example.sns_project.batch.entity.Child;
import com.example.sns_project.batch.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
}
