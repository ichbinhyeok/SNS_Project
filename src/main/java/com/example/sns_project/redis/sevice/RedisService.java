package com.example.sns_project.redis.sevice;

import com.example.sns_project.redis.model.RedisTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper; // JSON 직렬화/역직렬화 도구

    public void setValues(String key, String data) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    @Transactional(readOnly = true)
    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return "false";
        }
        return (String) values.get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public void expireValues(String key, int timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    public void setHashOps(String key, Map<String, String> data) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.putAll(key, data);
    }

    @Transactional(readOnly = true)
    public String getHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        return Boolean.TRUE.equals(values.hasKey(key, hashKey)) ? (String) redisTemplate.opsForHash().get(key, hashKey) : "";
    }

    public void deleteHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.delete(key, hashKey);
    }

    public boolean checkExistsValue(String value) {
        return !value.equals("false");
    }

    // 엔티티 저장
    public void saveEntity(String key, RedisTest entity, Duration duration) throws JsonProcessingException {
        String jsonValue = objectMapper.writeValueAsString(entity); // 엔티티를 JSON으로 변환
        redisTemplate.opsForValue().set(key, jsonValue, duration);  // Redis에 저장
    }

    // 엔티티 조회
    public RedisTest getEntity(String key) throws JsonProcessingException {
        String jsonValue = redisTemplate.opsForValue().get(key); // JSON 형태로 데이터 조회
        if (jsonValue == null) {
            return null;
        }
        return objectMapper.readValue(jsonValue, RedisTest.class); // JSON을 엔티티로 변환
    }

    // 모든 엔티티 삭제
    public void deleteEntity(String key) {
        redisTemplate.delete(key);
    }

    // 레디스에 저장되어있는 모든 내역 출력
    public void printAllValues() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null) {
            keys.forEach(key -> {
                String value = redisTemplate.opsForValue().get(key);
                System.out.println(key + ": " + value);
            });
        }
    }

}
