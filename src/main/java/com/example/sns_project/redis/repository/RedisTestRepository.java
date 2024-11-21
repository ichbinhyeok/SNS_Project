package com.example.sns_project.redis.repository;

import com.example.sns_project.redis.model.RedisTest;
import org.springframework.data.repository.CrudRepository;

public interface RedisTestRepository extends CrudRepository<RedisTest, String> {
}