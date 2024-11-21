package com.example.sns_project.batch.job;


import com.example.sns_project.batch.config.BatchInsertConfig;
import com.example.sns_project.batch.config.FakerBatchConfig;
import com.example.sns_project.batch.service.JpaInsertService;
import com.example.sns_project.batch.service.JpaInsertServiceWithFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.batch.job.enabled=false")
public class InsertPerformanceTest {

    @Autowired
    private JpaInsertService jpaInsertService;

    @Autowired
    private JpaInsertServiceWithFaker jpaInsertServiceWithFaker;

    @Autowired
    private BatchInsertConfig batchInsertConfig;

    @Autowired
    private FakerBatchConfig fakerBatchConfig;

    @Autowired
    private JobLauncher jobLauncher;

    int dataCount = 1000000;

    @Test
    @DisplayName("단순 반복문 인서트와 배치 인서트 시간 비교")
    void compareInsertPerformance() throws Exception {


        // dataCount를 포맷해서 출력
        String formattedDataCount = String.format("%,d", dataCount);
        System.out.println("데이터 삽입 개수 : " + formattedDataCount);


        // JPA 반복문
        long jpaStart = System.currentTimeMillis();
        jpaInsertService.insertUsingJpa(dataCount);
        long jpaEnd = System.currentTimeMillis();
        System.out.println("JPA Insert Time: " + (jpaEnd - jpaStart) + "ms");

        // Spring Batch
        long batchStart = System.currentTimeMillis();
        jobLauncher.run(batchInsertConfig.insertJob(), new JobParameters());
        long batchEnd = System.currentTimeMillis();
        System.out.println("Batch Insert Time: " + (batchEnd - batchStart) + "ms");
    }


    @Test
    @DisplayName("단순 반복문 인서트와 배치 인서트 시간 비교 (Faker)")
    void compareInsertPerformanceWithFaker() throws Exception {

        // JPA 반복문
        long jpaStart = System.currentTimeMillis();
        jpaInsertServiceWithFaker.insertUsingJpa(dataCount);
        long jpaEnd = System.currentTimeMillis();
        System.out.println("Faker JPA Insert Time : " + (jpaEnd - jpaStart) + "ms");

        // Spring Batch
        long batchStart = System.currentTimeMillis();
        jobLauncher.run(fakerBatchConfig.fakerJob(), new JobParameters());
        long batchEnd = System.currentTimeMillis();
        System.out.println("Faker Batch Insert Time : " + (batchEnd - batchStart) + "ms");
    }
}
