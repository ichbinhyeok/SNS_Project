package com.example.sns_project.redis.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("redisTest")
public class RedisTest implements Serializable {
    @Id
    private String id;
    private String name;
    private int age;

    // Getters and Setters
}