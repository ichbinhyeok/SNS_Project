package com.example.sns_project.batch.repository;

import com.example.sns_project.batch.entity.InputType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputTypeRepository extends JpaRepository<InputType, Long> {
}
