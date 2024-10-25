package com.example.sns_project.batch.controller;

import com.example.sns_project.batch.entity.TestEntity;
import com.example.sns_project.batch.service.TestEntityBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestEntityBatchController {

    @Autowired
    private TestEntityBatchService testEntityBatchService;

    @PostMapping("/batch-insert")
    public String batchInsertTestEntities() {
        List<TestEntity> testEntities = new ArrayList<>();

        // 대량 데이터 생성 (예: 10,000개)
        for (int i = 1; i <= 1000000; i++) {
            TestEntity entity = new TestEntity();
            entity.setName("Name" + i);
            entity.setAge(String.valueOf(20 + (i % 50)));
            testEntities.add(entity);
        }

        // 배치 인서트 실행
        testEntityBatchService.batchInsert(testEntities);

        return "Batch Insert Completed!";
    }
}
