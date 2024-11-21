package com.example.sns_project.batch.config;

import com.example.sns_project.batch.entity.TestEntity;
import com.example.sns_project.batch.listener.FakerListener;
import com.example.sns_project.batch.listener.InsertJobListener;
import com.example.sns_project.batch.repository.TestEntityRepository;
import com.github.javafaker.Faker;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class FakerBatchConfig {

    @Autowired
    private org.springframework.batch.core.repository.JobRepository jobRepository;

    @Autowired
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    @Autowired
    private TestEntityRepository repository;

    private final Faker faker = new Faker();  // Faker 인스턴스 생성


    @Bean
    public Job fakerJob() {
        return new JobBuilder("fakerJob", jobRepository)
                .start(fakerStep())
                .listener(fakerListener())
                .build();
    }

    @Bean
    public Step fakerStep() {
        return new StepBuilder("fakerStep", jobRepository)
                .<TestEntity, TestEntity>chunk(1000, transactionManager)
                .reader(fakerReader())
                .processor(fakerProcessor())
                .writer(fakerWriter(repository))
                .build();
    }


    @Bean
    public ItemReader<TestEntity> fakerReader() {
        return new ItemReader<>() {
            private int count = 0;
            private final int limit = 1000000;

            @Override
            public TestEntity read() {
                if (count < limit) {
                    // Faker를 사용하여 랜덤 데이터 생성
                    TestEntity entity = new TestEntity();
                    entity.setName(faker.name().fullName());  // 랜덤 이름
                    entity.setAge(String.valueOf(faker.number().numberBetween(18, 60)));  // 랜덤 나이 (18~60세)
                    count++;
                    return entity;
                }
                return null;
            }
        };
    }

    @Bean
    public ItemProcessor<TestEntity, TestEntity> fakerProcessor() {
        return item -> item;
    }

    @Bean
    public ItemWriter<TestEntity> fakerWriter(TestEntityRepository repository) {
        return repository::saveAll;
    }

    @Bean
    public JobExecutionListener fakerListener() {
        return new FakerListener();
    }
}
