package com.example.sns_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories(basePackages = "com.example.sns_project.redis.repository")
public class SnsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnsProjectApplication.class, args);
    }

}
