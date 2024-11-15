package com.example.sns_project.redis.controller;

import com.example.sns_project.redis.model.RedisTest;
import com.example.sns_project.redis.repository.RedisTestRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/redis-tests")
public class RedisTestController {
    private final RedisTestRepository redisTestRepository;

    public RedisTestController(RedisTestRepository redisTestRepository) {
        this.redisTestRepository = redisTestRepository;
    }

    @PostMapping
    public RedisTest createRedisTest(@RequestBody RedisTest redisTest) {
        return redisTestRepository.save(redisTest);
    }

    @GetMapping("/{id}")
    public Optional<RedisTest> getRedisTest(@PathVariable String id) {
        return redisTestRepository.findById(id);
    }
}